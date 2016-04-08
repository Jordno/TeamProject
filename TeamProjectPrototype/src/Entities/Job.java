/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.sql.Date;
import java.sql.Time;

/**
 *
 * @author Wolf
 */
public class Job {
    private int jobID;
    private String regNo;
    private String workReq, jobStatus, bussinessType;
    private Time duration;
    private Date created;

    public Job(int jobID, String regNo, String workReq, String jobStatus, String bussinessType, Time duration, Date created) {
        this.jobID = jobID;
        this.regNo = regNo;
        this.workReq = workReq;
        this.jobStatus = jobStatus;
        this.bussinessType = bussinessType;
        this.duration = duration;
        this.created = created;
    }


    public int getJobID() {
        return jobID;
    }

    public void setJobID(int jobID) {
        this.jobID = jobID;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getWorkReq() {
        return workReq;
    }

    public void setWorkReq(String workReq) {
        this.workReq = workReq;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getBussinessType() {
        return bussinessType;
    }

    public void setBussinessType(String bussinessType) {
        this.bussinessType = bussinessType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

}
