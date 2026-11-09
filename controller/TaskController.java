package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import model.Task;
import model.TaskManager;
import ui.MainForm;

public class TaskController {
    private final TaskManager model;
    private MainForm view;

    public TaskController(TaskManager model) { this.model = model; }
    public void setView(MainForm view) { this.view = view; }

    public boolean handleAddTask(String brand, String type, String price, String unit) {
        if (isBlank(brand) || isBlank(type) || isBlank(price) || isBlank(unit)) return false;
        try {
            if (Double.parseDouble(price.trim()) < 0) return false;
        } catch (NumberFormatException ex) { return false; }
        Task task = new Task();
        task.setBrand(brand.trim());
        task.setType(type.trim());
        task.setPRice(price.trim());
        task.setUnit(unit.trim());
        model.addTask(task);
        if (model.saveTasks() != 0) return false;
        refreshView();
        return true;
    }

    public boolean handleDeleteTask(String id) {
        Task task = model.deleteTask(id);
        if (task == null) return false;
        if (model.saveTasks() != 0) return false;
        System.out.println("Deleted record: " + task.getBrand());
        refreshView();
        return true;
    }

    public boolean handleUpdateTask(String id, String brand, String type, String price, String unit) {
        if (isBlank(brand) || isBlank(type) || isBlank(price) || isBlank(unit)) return false;
        try {
            if (Double.parseDouble(price.trim()) < 0) return false;
        } catch (NumberFormatException ex) { return false; }
        Task task = model.findTask(id);
        if (task == null) return false;
        task.setBrand(brand.trim());
        task.setType(type.trim());
        task.setPRice(price.trim());
        task.setUnit(unit.trim());
        if (model.saveTasks() != 0) return false;
        refreshView();
        return true;
    }

    public List<Task> getAllTask() {
        Collection<Task> values = model.findAll().values();
        return new ArrayList<>(values);
    }
    public Task getTaskDetails(String id) { return model.findTask(id); }
    private boolean isBlank(String value) { return value == null || value.trim().isEmpty(); }
    private void refreshView() { if (view != null) view.refreshTaskList(); }
}
