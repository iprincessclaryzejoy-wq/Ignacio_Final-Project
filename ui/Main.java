package ui;

import controller.Controller;
import java.nio.file.Paths;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.ParkingManagement;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Look and Feel error: " + e.getMessage());
            }

            // records are saved to parking_records.txt in the project folder
            ParkingManagement model =
                    new ParkingManagement(Paths.get("parking_records.txt"));

            Mainform view = new Mainform();
            new Controller(model, view);

            view.setLocationRelativeTo(null);
            view.setVisible(true);
        });
    }
}
