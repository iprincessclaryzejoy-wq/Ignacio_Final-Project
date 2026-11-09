package ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Task;

/** Table model for the motorcycle/product list. */
public class TaskTableModel extends AbstractTableModel {
    private List<Task> tasks = new ArrayList<>();
    private final String[] COLUMN_NAMES = {"Snum", "Brand", "Type", "Price", "Unit"};

    @Override
    public int getRowCount() { return tasks.size(); }

    @Override
    public int getColumnCount() { return COLUMN_NAMES.length; }

    @Override
    public String getColumnName(int column) { return COLUMN_NAMES[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> task.getTaskId();
            case 1 -> task.getBrand();
            case 2 -> task.getType();
            case 3 -> task.getPRice();
            case 4 -> task.getUnit();
            default -> null;
        };
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks == null ? new ArrayList<>() : tasks;
        fireTableDataChanged();
    }

    public String getTaskId(int rowIndex) { return tasks.get(rowIndex).getTaskId(); }
}
