
package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Parking;
import model.ParkingManagement;
import ui.Mainform;

/**
 * Connects the Mainform (view) to ParkingManagement (model).
 */
public class Controller {

    private static final int ENTRY_TIME_COLUMN = 4;

    private final ParkingManagement model;
    private final Mainform view;

    // Records currently displayed in the table.
    private List<Parking> displayed = new ArrayList<>();

    public Controller(ParkingManagement model, Mainform view) {
        this.model = model;
        this.view = view;

        initListeners();
        refreshTable();
    }

    // ================= EVENT LISTENERS =================

    private void initListeners() {

        view.getBtnPark().addActionListener(e -> park());
        view.getBtnCheckout().addActionListener(e -> checkout());
        view.getBtnMarkPaid().addActionListener(e -> markAsPaid());
        view.getBtnDelete().addActionListener(e -> delete());
        view.getBtnClear().addActionListener(e -> view.clearFields());

        // Live search by license plate.
        view.getSearchField().getDocument().addDocumentListener(
            new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    refreshTable();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    refreshTable();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    refreshTable();
                }
            }
        );

        // Sort by entry time.
        view.getTable().getTableHeader().addMouseListener(
            new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {

                    JTable table = view.getTable();

                    int column = table.columnAtPoint(e.getPoint());

                    if (column >= 0 &&
                        table.convertColumnIndexToModel(column)
                                == ENTRY_TIME_COLUMN) {

                        model.toggleSortOrder();
                        refreshTable();
                    }
                }
            }
        );
    }

    // ================= PARK VEHICLE =================

    private void park() {

        String error = model.park(
                view.getVehicleType(),
                view.getLicensePlate(),
                view.getCustomerName(),
                view.getDateText(),
                view.getEntryTime(),
                view.getParkingSlot()
        );

        if (error != null) {
            view.showError(error);
            return;
        }

        view.clearSearch();
        view.clearFields();

        refreshTable();

        view.showMessage("Vehicle successfully parked!");
    }

    // ================= CHECKOUT =================

    private void checkout() {

        Parking selected = getSelected();

        if (selected == null) {
            return;
        }

        String error = model.checkout(selected);

        if (error != null) {
            view.showError(error);
            return;
        }

        refreshTable();

        view.showMessage(
                "Vehicle checked out successfully!\n\n"
                + "License Plate: " + selected.getLicensePlate()
                + "\nParking Fee: " + selected.getFeeText()
        );
    }

    // ================= MARK AS PAID =================

    private void markAsPaid() {

        Parking selected = getSelected();

        if (selected == null) {
            return;
        }

        String error = model.markAsPaid(selected);

        if (error != null) {
            view.showError(error);
            return;
        }

        refreshTable();

        view.showMessage(
                "Payment successfully recorded!\n\n"
                + "License Plate: " + selected.getLicensePlate()
                + "\nAmount Paid: " + selected.getFeeText()
        );
    }

    // ================= DELETE =================

    private void delete() {

        Parking selected = getSelected();

        if (selected == null) {
            return;
        }

        String warning = selected.isParked()
                ? "This vehicle is still parked.\n"
                  + "Are you sure you want to delete its record?"
                : "Are you sure you want to delete this parking record?";

        if (!view.confirm(warning)) {
            return;
        }

        String error = model.delete(selected);

        if (error != null) {
            view.showError(error);
            return;
        }

        refreshTable();

        view.showMessage("Parking record successfully deleted!");
    }

    // ================= HELPERS =================

    private Parking getSelected() {

        JTable table = view.getTable();

        int selectedRow = table.getSelectedRow();

        if (selectedRow < 0) {
            view.showError("Please select a parking record first.");
            return null;
        }

        // Convert JTable view index to model index.
        int modelRow = table.convertRowIndexToModel(selectedRow);

        if (modelRow < 0 || modelRow >= displayed.size()) {
            view.showError("Invalid parking record selection.");
            return null;
        }

        return displayed.get(modelRow);
    }

    // ================= REFRESH TABLE =================

    private void refreshTable() {

        displayed = new ArrayList<>(
                model.searchByPlate(view.getSearchText())
        );

        view.refreshTable(displayed, model.isAscending());

        view.setSummary(
                model.getAll().size(),
                model.getActiveParking().size()
        );
    }
}
