/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Mechanic;

import java.sql.Time;

/**
 *
 * @author Wolf
 */
public class JobTask {
    private int taskID, jobID;
    private String description;
    private Time duration;

    public JobTask(int taskID, int jobID, String description, Time duration) {
        this.taskID = taskID;
        this.jobID = jobID;
        this.description = description;
        this.duration = duration;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }
    
}
