/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ui;
import controller.TaskController;
import java.util.List;
import javax.swing.JOptionPane;
import model.Task;

/** 
 *
 * @author USER
 */
public class MainForm extends javax.swing.JFrame {

    
    
   public MainForm(TaskController controller) {
        this.controller = controller;
        this.tableModel = new TaskTableModel();
                
        initComponents();
        setUpDefaultDisplay();
        
        setupListTable();
        setUpRowSelectionListener();        
        
        refreshTaskList();
        
    }
   private void setUpDefaultDisplay(){
        //default labels and textfield visibility
        this.jLabelBrand.setVisible(true);
        this.jLabelPrice.setVisible(true);
        this.jTextFieldBrand.setVisible(true);
        this.jTextFieldPrice.setVisible(true);
        //default textfield text
        this.jTextFieldBrand.setText("");
        this.jTextFieldPrice.setText("");
        this.jTextFieldType.setText("");
        this.jTextFieldUnit.setText("");
        
        //default buttons
        this.jButtonDelete.setEnabled(false);
        //default action for save is add
        this.saveMode = "ADD";
    }
   
     private void setupListTable() {
        //set the modified tablemodel
        jTableListOfTask.setModel(tableModel);
        //This forces the JTable to use the names from taskTableModel.getColumnName(int)
        jTableListOfTask.getTableHeader().setReorderingAllowed(false);
        // IMPORTANT: Tell the JTable to use the model's column names
        jTableListOfTask.setAutoCreateColumnsFromModel(true);
        //need this line if the column count changed:
        jTableListOfTask.createDefaultColumnsFromModel();
        setupTableColumnWidths();
    }
     
      public void refreshTaskList(){
        String savedSelectedId = this.selectedTaskId; //selectedTaskId is the props
        System.out.println("Selected task  id:" + savedSelectedId);
                
        List<Task> tasks = controller.getAllTask();
        for (Task task : tasks) {
            System.out.println(task);
        }
        //2. Update the JTableModel to 
        this.tableModel.setTasks(tasks);
        
        if (savedSelectedId != null) {
            int newModelRow = -1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (savedSelectedId.equals(tableModel.getTaskId(i))) {
                    newModelRow = i;
                    break;
                }
            }
            if (newModelRow != -1) {
                int newTableRow = jTableListOfTask.convertRowIndexToView(newModelRow);
                jTableListOfTask.setRowSelectionInterval(newTableRow, newTableRow);                
            } else {
                this.selectedTaskId = null;
                setUpDefaultDisplay();
            }
        }
    }
      
      private void setupTableColumnWidths(){
        javax.swing.table.TableColumnModel columns = jTableListOfTask.getColumnModel();
        columns.getColumn(0).setPreferredWidth(85);  // ID
        columns.getColumn(1).setPreferredWidth(150); // Brand
        columns.getColumn(2).setPreferredWidth(150); // Type
        columns.getColumn(3).setPreferredWidth(100); // Price
        columns.getColumn(4).setPreferredWidth(100); // Unit
        jTableListOfTask.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
      }
      
      private void setUpRowSelectionListener(){
        jTableListOfTask.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jTableListOfTask.getSelectedRow();
                //if row is selected
                if (selectedRow != -1) {
                    int modelRow = jTableListOfTask.convertRowIndexToModel(selectedRow);
                    String taskId = this.tableModel.getTaskId(modelRow);
                    Task selectedTask = controller.getTaskDetails(taskId);
                    displaySelectedTask(selectedTask);
                }
            } else {
                setUpDefaultDisplay();
            }
        });        
    }
      
      private void displaySelectedTask(Task task){
        this.jTextFieldBrand.setText(task.getBrand());
        this.jTextFieldType.setText(task.getType());
        this.selectedTaskId = task.getTaskId();
        this.jTextFieldUnit.setText(task.getUnit());
        this.jTextFieldPrice.setText(task.getPRice());
        
        //make some fields visible for editing
        this.jLabelPrice.setVisible(true);
        this.jLabelBrand.setVisible(true);
        this.jTextFieldBrand.setVisible(true);
        this.jTextFieldPrice.setVisible(true);
        
        //enables the delete button
        this.jButtonDelete.setEnabled(true);
        //change save button to update
        this.saveMode = "UPDATE";
        System.out.println("SaveMode:" + saveMode);
    }
      
      private void addTask(){
        String brand = jTextFieldBrand.getText();
        String type = jTextFieldType.getText();
        String price = jTextFieldPrice.getText();
        String unit = jTextFieldUnit.getText();
        boolean success;

        if (brand.trim().isEmpty() || type.trim().isEmpty()
                || price.trim().isEmpty() || unit.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Brand, Type, Price, and Unit.",
                    "Required Fields", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (saveMode.equalsIgnoreCase("ADD")) {
            success = controller.handleAddTask(brand, type, price, unit);
            if (success) {
                JOptionPane.showMessageDialog(this, "Record successfully added.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not save. Enter a valid non-negative price and check the file permissions.",
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            success = controller.handleUpdateTask(selectedTaskId, brand, type, price, unit);
            if (success) {
                JOptionPane.showMessageDialog(this, "Record successfully updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not update. Check the values and try again.",
                        "Update Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (success) {
            jTableListOfTask.clearSelection();
            selectedTaskId = null;
            setUpDefaultDisplay();
        }
      }
      
        private void deleteTask(){
        if (this.selectedTaskId != null) {
            int confirmDeletion = JOptionPane.showConfirmDialog(
                    this, 
                    "Are you sure you want to delete this Task? This cannot be undone.",
                    "Confirm Task Deletion", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            //if response is yes
            if (confirmDeletion == JOptionPane.YES_OPTION) {
                boolean success = controller.handleDeleteTask(this.selectedTaskId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Task Successfully Deleted", "Task Deletion", 1);
                    //clear the display
                    this.jTableListOfTask.clearSelection();
                } else { //error message if deletion failed
                    JOptionPane.showMessageDialog(this, "An error occured while deleting the task", "Task Deletion", 0);
                }
            } else {
                //do nothing
            }
        }
    }
    
    
    

    /**
     * Creates new form NewJFrame
     */
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jLabelBrand = new javax.swing.JLabel();
        jTextFieldBrand = new javax.swing.JTextField();
        jLabelType = new javax.swing.JLabel();
        jTextFieldType = new javax.swing.JTextField();
        jLabelPrice = new javax.swing.JLabel();
        jTextFieldPrice = new javax.swing.JTextField();
        jLabelUnit = new javax.swing.JLabel();
        jTextFieldUnit = new javax.swing.JTextField();
        jButtonSave = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableListOfTask = new javax.swing.JTable();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();

        jMenu3.setText("File");
        jMenuBar2.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar2.add(jMenu4);

        jLabel1.setText("jLabel1");

        jScrollPane1.setViewportView(jTextPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelBrand.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelBrand.setText("Brand: ");

        jTextFieldBrand.addActionListener(this::jTextFieldBrandActionPerformed);

        jLabelType.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelType.setText("Type: ");

        jTextFieldType.addActionListener(this::jTextFieldTypeActionPerformed);

        jLabelPrice.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelPrice.setText("Price:");

        jLabelUnit.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabelUnit.setText("Unit:");

        jTextFieldUnit.addActionListener(this::jTextFieldUnitActionPerformed);

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(this::jButtonDeleteActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jTextFieldBrand))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(29, Short.MAX_VALUE)
                        .addComponent(jTextFieldType, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldUnit, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelType, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 93, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(72, 72, 72)
                .addComponent(jLabelBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldBrand, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelType)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldType, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelPrice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelUnit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Algerian", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Motor Brand");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jTableListOfTask.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(jTableListOfTask);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jMenu5.setText("File");
        jMenuBar3.add(jMenu5);

        jMenu6.setText("Edit");
        jMenuBar3.add(jMenu6);

        setJMenuBar(jMenuBar3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldTypeActionPerformed

    private void jTextFieldUnitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldUnitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldUnitActionPerformed

    private void jTextFieldBrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldBrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldBrandActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        System.out.println("Button Clicked");
        addTask();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        System.out.println("Delete is clicked!");
        deleteTask();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    /**
     * @param args the command line arguments
     */
    
    private final TaskController controller;
    private final TaskTableModel tableModel;
    public String selectedTaskId;
    public String saveMode;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelBrand;
    private javax.swing.JLabel jLabelPrice;
    private javax.swing.JLabel jLabelType;
    private javax.swing.JLabel jLabelUnit;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableListOfTask;
    private javax.swing.JTextField jTextFieldBrand;
    private javax.swing.JTextField jTextFieldPrice;
    private javax.swing.JTextField jTextFieldType;
    private javax.swing.JTextField jTextFieldUnit;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

  
    
}
