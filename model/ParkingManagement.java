package model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Business logic of the parking system. Contains no Swing code.
 * Every action method returns null on success, or an error message.
 */
public class ParkingManagement {

    public static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

    public static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Vehicle types and their hourly rate (PHP). */
    public static final Map<String, Double> RATES = Map.of(
            "Car", 50.0,
            "Motorcycle", 30.0,
            "Van", 70.0,
            "Truck", 100.0
    );

    public static String[] vehicleTypes() {
        return new String[]{"Car", "Motorcycle", "Van", "Truck"};
    }

    private final List<Parking> records = new ArrayList<>();
    private final Path file;           // null = no saving
    private boolean ascending = true;  // current sort direction

    /** In-memory only (no file). */
    public ParkingManagement() {
        this(null);
    }

    /** Loads records from the file (if it exists) and saves after every change. */
    public ParkingManagement(Path file) {
        this.file = file;
        load();
    }

    // ================= PARK VEHICLE =================

    public String park(String type, String plate, String name,
                       String date, String entryTime, String slot) {

        type = clean(type);
        plate = clean(plate).toUpperCase();
        name = clean(name);
        date = clean(date);
        entryTime = clean(entryTime);
        slot = clean(slot).toUpperCase();

        if (type.isEmpty() || plate.isEmpty() || name.isEmpty()
                || date.isEmpty() || entryTime.isEmpty() || slot.isEmpty()) {
            return "Please fill in all fields.";
        }

        if (!RATES.containsKey(type)) {
            return "Unknown vehicle type.";
        }

        if (!isValidDate(date)) {
            return "Invalid date. Use yyyy-MM-dd (example: 2026-11-09).";
        }

        if (!isValidTime(entryTime)) {
            return "Invalid time. Use 24-hour HH:mm (example: 14:30).";
        }

        LocalDateTime entry = LocalDateTime.of(
                LocalDate.parse(date, DATE_FMT),
                LocalTime.parse(entryTime, TIME_FMT));

        if (entry.isAfter(LocalDateTime.now())) {
            return "Entry date/time cannot be in the future.";
        }

        for (Parking p : records) {
            if (!p.isParked()) {
                continue;
            }
            if (p.getLicensePlate().equalsIgnoreCase(plate)) {
                return "This vehicle is already parked.";
            }
            if (p.getSlotNumber().equalsIgnoreCase(slot)) {
                return "Parking slot " + slot + " is already occupied.";
            }
        }

        records.add(new Parking(type, plate, name, date, entryTime, slot));
        sortByEntryTime();   // also saves
        return null;
    }

    // ================= CHECKOUT =================

    /** Records the time out and computes the fee (rate x hours, rounded up, minimum 1 hour). */
    public String checkout(Parking parking) {

        String error = validateRecord(parking);
        if (error != null) {
            return error;
        }

        if (!parking.isParked()) {
            return "This vehicle has already checked out.";
        }

        LocalDateTime entry = LocalDateTime.of(
                LocalDate.parse(parking.getDate(), DATE_FMT),
                LocalTime.parse(parking.getEntryTime(), TIME_FMT));

        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);

        if (now.isBefore(entry)) {
            return "Checkout time cannot be earlier than entry time.";
        }

        long minutes = Duration.between(entry, now).toMinutes();
        long hours = Math.max(1, (minutes + 59) / 60);

        parking.setFee(hours * RATES.getOrDefault(parking.getVehicleType(), 50.0));
        parking.setTimeOut(now.format(TIME_FMT));

        save();
        return null;
    }

    // ================= MARK AS PAID =================

    public String markAsPaid(Parking parking) {

        String error = validateRecord(parking);
        if (error != null) {
            return error;
        }

        if (parking.isPaid()) {
            return "This record is already paid.";
        }

        if (parking.isParked()) {
            return "Check the vehicle out first so the fee can be computed.";
        }

        parking.setStatus(Parking.PAID);
        save();
        return null;
    }

    // ================= DELETE =================

    public String delete(Parking parking) {

        String error = validateRecord(parking);
        if (error != null) {
            return error;
        }

        records.remove(parking);
        save();
        return null;
    }

    // ================= QUERIES =================

    public List<Parking> searchByPlate(String text) {

        List<Parking> results = new ArrayList<>();
        String keyword = clean(text).toLowerCase();

        for (Parking p : records) {
            if (keyword.isEmpty()
                    || p.getLicensePlate().toLowerCase().contains(keyword)) {
                results.add(p);
            }
        }
        return results;
    }

    public List<Parking> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(records));
    }

    public List<Parking> getActiveParking() {
        List<Parking> active = new ArrayList<>();
        for (Parking p : records) {
            if (p.isParked()) {
                active.add(p);
            }
        }
        return Collections.unmodifiableList(active);
    }

    // ================= QUICK SORT (entry date + time) =================

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
        sortByEntryTime();
    }

    public void toggleSortOrder() {
        setAscending(!ascending);
    }

    public void sortByEntryTime() {
        if (records.size() > 1) {
            quickSort(0, records.size() - 1);
        }
        save();
    }

    private void quickSort(int low, int high) {
        while (low < high) {
            int p = partition(low, high);
            // recurse into the smaller half, loop on the larger (limits stack depth)
            if (p - low < high - p) {
                quickSort(low, p - 1);
                low = p + 1;
            } else {
                quickSort(p + 1, high);
                high = p - 1;
            }
        }
    }

    private int partition(int low, int high) {

        // middle element as pivot: avoids worst case on already-sorted data
        swap((low + high) >>> 1, high);
        long pivot = sortKey(records.get(high));

        int i = low - 1;
        for (int j = low; j < high; j++) {
            long k = sortKey(records.get(j));
            if (ascending ? k <= pivot : k >= pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int a, int b) {
        Parking temp = records.get(a);
        records.set(a, records.get(b));
        records.set(b, temp);
    }

    /** Minutes since epoch of the entry date + time. */
    private long sortKey(Parking p) {
        long days = LocalDate.parse(p.getDate(), DATE_FMT).toEpochDay();
        long minutes = LocalTime.parse(p.getEntryTime(), TIME_FMT).toSecondOfDay() / 60;
        return days * 1440L + minutes;
    }

    // ================= VALIDATION =================

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DATE_FMT);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, TIME_FMT);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            return false;
        }
    }

    private String validateRecord(Parking p) {
        if (p == null || !records.contains(p)) {
            return "Please select a valid record first.";
        }
        return null;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim().replaceAll("[\\t\\r\\n]+", " ");
    }

    // ================= FILE STORAGE (tab-separated text) =================

    private void save() {
        if (file == null) {
            return;
        }
        List<String> lines = new ArrayList<>();
        for (Parking p : records) {
            lines.add(String.join("\t",
                    p.getVehicleType(), p.getLicensePlate(), p.getCustomerName(),
                    p.getDate(), p.getEntryTime(), p.getTimeOut(),
                    p.getSlotNumber(), p.getStatus(), String.valueOf(p.getFee())));
        }
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not save records: " + e.getMessage());
        }
    }

    private void load() {
        if (file == null || !Files.exists(file)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                String[] f = line.split("\t", -1);
                if (f.length < 9 || !isValidDate(f[3]) || !isValidTime(f[4])) {
                    continue;   // skip damaged lines
                }
                Parking p = new Parking(f[0], f[1], f[2], f[3], f[4], f[6]);
                p.setTimeOut(f[5]);
                p.setStatus(f[7]);
                try {
                    p.setFee(Double.parseDouble(f[8]));
                } catch (NumberFormatException ignored) {
                    p.setFee(0);
                }
                records.add(p);
            }
            if (records.size() > 1) {
                quickSort(0, records.size() - 1);
            }
        } catch (IOException e) {
            System.err.println("Could not load records: " + e.getMessage());
        }
    }
}
