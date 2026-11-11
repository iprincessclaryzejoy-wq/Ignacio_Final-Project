package model;

/**
 * One parking record (a vehicle that entered the lot).
 */
public class Parking {

    public static final String NOT_OUT = "-";
    public static final String UNPAID = "Unpaid";
    public static final String PAID = "Paid";

    private String vehicleType;
    private String licensePlate;
    private String customerName;
    private String date;
    private String entryTime;
    private String timeOut;
    private String slotNumber;
    private String status;
    private double fee;

    public Parking(String vehicleType, String licensePlate,
                   String customerName, String date,
                   String entryTime, String slotNumber) {

        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.customerName = customerName;
        this.date = date;
        this.entryTime = entryTime;
        this.slotNumber = slotNumber;
        this.timeOut = NOT_OUT;
        this.status = UNPAID;
        this.fee = 0;
    }

    // ---------- Getters ----------
    public String getVehicleType()  { return vehicleType; }
    public String getLicensePlate() { return licensePlate; }
    public String getCustomerName() { return customerName; }
    public String getDate()         { return date; }
    public String getEntryTime()    { return entryTime; }
    public String getTimeOut()      { return timeOut; }
    public String getSlotNumber()   { return slotNumber; }
    public String getStatus()       { return status; }
    public double getFee()          { return fee; }

    // ---------- Setters ----------
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
    public void setStatus(String status)   { this.status = status; }
    public void setFee(double fee)         { this.fee = fee; }

    // ---------- State ----------
    public boolean isPaid() {
        return PAID.equalsIgnoreCase(status);
    }

    /** True while the vehicle is still inside the lot (not checked out). */
    public boolean isParked() {
        return NOT_OUT.equals(timeOut);
    }

    public String getFeeText() {
        return isParked() ? "-" : String.format("PHP %.2f", fee);
    }

    /** Converts this record into a JTable row. */
    public Object[] toRow() {
        return new Object[]{
            vehicleType, licensePlate, customerName, date,
            entryTime, timeOut, slotNumber, getFeeText(), status
        };
    }
}
