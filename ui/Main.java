/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;


import controller.TaskController;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import model.TaskManager;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 *
 * @author USER
 */
public class Main {
     public static void main(String[] args) {
         // 1. Initialize the Look and Feel (L&F)
        try {
            // Set System Look and Feel for native OS appearance
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            // Log the error using a simpler logger for this entry point
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error setting System Look and Feel", ex);
        }

        // 2. Initialize the MVC components
        
        // A. Create the Model and load data
        TaskManager taskManager = new TaskManager();
        taskManager.loadTasks(); // Load persisted data on startup

        // B. Create the Controller, injecting the Model
        TaskController controller = new TaskController(taskManager);
        
        // 3. Launch the application (View) on the Event Dispatch Thread (EDT)
        java.awt.EventQueue.invokeLater(() -> {
            // C. Create the View (MainForm), injecting the Controller
            MainForm view = new MainForm(controller); 
            
            controller.setView(view);                                  
            // Add a window listener here to save data when the user closes the app
            view.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    taskManager.saveTasks();
                    System.exit(0);
                }
            });
            view.setVisible(true);
        });
        
        
        
        
    }
}
