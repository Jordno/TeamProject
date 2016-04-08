/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.sql.Date;

/**
 *
 * @author Wolf
 */
public class Staff {
    private int ID;
    private String username, type, surname, name;
    private byte[] password;
    private Date date;
    private double labourCost;

    public Staff(int ID, String username, byte[] password, String type, String surname, String name, Date date, double labourCost) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.type = type;
        this.surname = surname;
        this.name = name;
        this.date = date;
        this.labourCost = labourCost;
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public double getLabourCost() {
        return labourCost;
    }

    public void setLabourCost(double labourCost) {
        this.labourCost = labourCost;
    }

    public String getType() {
        return type;
    }

    public byte[] getPassword() {
        return password;
    }
    
    public Date getDate() {
        return date;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
