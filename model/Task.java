/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author USER
 */
public class Task implements Serializable {
    private String TaskId;
    private String Brand;
    private String Type;
    private String PRice;
    private String Unit;
    private String status;    
    private String dateCreated;
    private static final long serialVersionUID = 1L; //for compatibility
    
    public Task() {        
        this.status = "Pending"; //set default to pending
        setDateCreated();
}

    public String getTaskId() {
        return TaskId;
    }

    public void setTaskId(String TaskId) {
        this.TaskId = TaskId;
    }


   


    public String getBrand() {
        return Brand;
    }

    public void setBrand(String Brand) {
        this.Brand = Brand;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public String getPRice() {
        return PRice;
    }

    public void setPRice(String PRice) {
        this.PRice = PRice;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Task{" + "TaskId=" + TaskId + ", Brand=" + Brand + ", Type=" + Type + ", PRice=" + PRice + ", Unit=" + Unit + ", status=" + status + ", dateCreated=" + dateCreated + '}';
    }

   

   
    private void setDateCreated() {
         LocalDate now = LocalDate.now();
        this.dateCreated = now.toString();
   }
          }
 