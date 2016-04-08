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
public class Vehicle {
    private int engSerial, chassisNo;
    private String colour, make, model, regNo;
    private int year;
    private Date motCheckDate;

    public Vehicle(String regNo, int engSerial, int chassisNo, String colour, String make, String model, int year, Date motCheckDate) {
        this.regNo = regNo;
        this.engSerial = engSerial;
        this.chassisNo = chassisNo;
        this.colour = colour;
        this.make = make;
        this.model = model;
        this.year = year;
        this.motCheckDate = motCheckDate;
    }



    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public int getEngSerial() {
        return engSerial;
    }

    public void setEngSerial(int engSerial) {
        this.engSerial = engSerial;
    }

    public int getChassisNo() {
        return chassisNo;
    }

    public void setChassisNo(int chassisNo) {
        this.chassisNo = chassisNo;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getMotCheckDate() {
        return motCheckDate;
    }

    public void setMotCheckDate(Date motCheckDate) {
        this.motCheckDate = motCheckDate;
    }
    
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
