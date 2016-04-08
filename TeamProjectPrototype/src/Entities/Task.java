/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.sql.Time;

/**
 *
 * @author Wolf
 */
public class Task {
    private int listID;
    private String desc;
    private Time duration;

    public Task(int listID, String desc, Time duration) {
        this.listID = listID;
        this.desc = desc;
        this.duration = duration;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }
    
}
