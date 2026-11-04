/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import util.Snumber;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 *
 * @author USE
 */
public class TaskManager {
    private HashMap<String, Task> tasks; //list of tasks
    private final String FILE_PATH = "taskslist.ser";

    public TaskManager() {
        tasks = new HashMap();
    }

    /**
     * addTask(Task task)     *
     * @param task Adds new task to the list
     * 
     */
    public void addTask(Task task) {
        //get the year part of task created date
        String s = task.getDateCreated().substring(0, 4);
        //generate random number
        String id = Snumber.SnumberId(Integer.parseInt(s));
        //regenerate id if id is already on the list
        if (tasks != null) {
            while (tasks.containsKey(id)) {
                id = Snumber.SnumberId(Integer.parseInt(s));
            }
        }
        task.setTaskId(id);
        //add task to list
        tasks.put(id, task);
    }

    /**
     * deleteTask(String id)
     *
     * @param id
     * @return task Remove a task from the list
     */
    public Task deleteTask(String id) {
        //delete if task is on the list
        return tasks.remove(id);
    }

    /**
     * findTask(String id)
     *
     * @param id
     * @return Task Find a task from the list based on ID
     */
    public Task findTask(String id) {
        return tasks.get(id);
    }

    /**
     * findAll()     *
     * @return HashMap<String, Task>
     * Return the list of tasks
     */
    public HashMap<String, Task> findAll() {
        return tasks;
    }

    /**
     * setTask()
     * @param task
     * Update the task if some of its details are changed
     */
    public void setTask(Task task){
        //get the task id
        tasks.replace(task.getTaskId(), task);
    }
    
    /**
     * loadTasks()
     * Get the list of tasks from the file
     */
    public void loadTasks() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                Object readObj = ois.readObject();
                this.tasks = (HashMap<String, Task>) readObj;
            }
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * saveTasks()
     * @return int
     * Save the list of tasks to file. If returns 0-success; 1-error.
     */
    public int saveTasks() {
        FileOutputStream fos = null;
        int errorCode = 0; //0-success; 1-error
        try {
            fos = new FileOutputStream(this.FILE_PATH);
            //create object output stream                    
            try ( ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                //write the objects to file
                oos.writeObject(tasks);
            }
            fos.close();
        } catch (IOException e) {
            errorCode = 1;
            Logger.getLogger(TaskManager.class.getName()).log(Level.SEVERE, null, e);
        }
        return errorCode;
    }   
}
