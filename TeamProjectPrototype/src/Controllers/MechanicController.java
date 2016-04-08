/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Job;
import Entities.StockItem;
import Entities.Vehicle;
import GUI.Mechanic.JobTask;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author Wolf
 */
public class MechanicController extends ActorController  {
    private Connection conn;
    private DatabaseConnection dbc;
    private ArrayList<Job> pendingJobData;
    private ArrayList<JobTask> taskData;
    private ArrayList<StockItem> stockData;
    private ArrayList<Vehicle> vehData;
    private HashMap<String,Time> taskList;
    private int staffID;
    private byte[] pendingChecksum;
    private byte[] jobChecksum; //used for altering jobstatus
    private byte[] stockChecksum;

    public MechanicController(DatabaseConnection dbc, int staffID){
        this.dbc = dbc;
        conn = dbc.getConnection();
        this.staffID = staffID;
        pendingJobData = new ArrayList<>();
        taskData = new ArrayList<>();
        stockData = new ArrayList<>();
        vehData = new ArrayList<>();
        taskList = new HashMap<>();
        pendingChecksum = setChecksum(conn, "PendingJobs");
        jobChecksum = setChecksum(conn,"Jobs");
        stockChecksum = setChecksum(conn,"SparePartStock");
    }
    
    /**
     * Gets all pending jobs that do have not been assigned to anyone
     */
    private void getPendingJobInfo() {
        pendingJobData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Jobs.JobID, RegNo, Work_Required, JobStatus, BusinessType, Duration, Date_creation "
                    + "FROM `jobs` INNER JOIN pendingjobs ON jobs.JobID = pendingjobs.JobID AND `StaffID` IS NULL");
            while(rs.next()){
                pendingJobData.add(new Job(rs.getInt("JobID"), rs.getString("RegNo"), 
                        rs.getString("Work_required"), rs.getString("JobStatus"), 
                        rs.getString("BusinessType"),
                        rs.getTime("Duration"), rs.getDate("Date_creation")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getPendingJobData(){
        pendingChecksum = setChecksum(conn, "PendingJobs");
        getPendingJobInfo();
        return pendingJobData;
    }
    
    private void getSelectedPendingJobInfo() {
        pendingJobData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Jobs.JobID, RegNo, Work_Required, JobStatus, BusinessType, Duration, Date_creation "
                    + "FROM `jobs` INNER JOIN pendingjobs ON jobs.JobID = pendingjobs.JobID AND `StaffID` ="+staffID);
            while(rs.next()){
                pendingJobData.add(new Job(rs.getInt("JobID"), rs.getString("RegNo"), 
                        rs.getString("Work_required"), rs.getString("JobStatus"), 
                        rs.getString("BusinessType"),
                        rs.getTime("Duration"), rs.getDate("Date_creation")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getSelectedPendingJobData(){
        jobChecksum = setChecksum(conn,"Jobs"); //might need to change this tbh
        getSelectedPendingJobInfo();
        return pendingJobData;
    }
    
    public void getSelectedTaskInfo(int jobID){
        taskData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tasks WHERE JobID ="+jobID);
            while(rs.next()){
                taskData.add(new JobTask(rs.getInt("TaskID"),rs.getInt("JobID"),
                rs.getString("Description"),rs.getTime("Duration")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getSelectedTaskData(int jobID){
        getSelectedTaskInfo(jobID);
        return taskData;
    }
    
    public void getTaskListInfo(){
        taskList.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tasklist");
            while(rs.next()){
                taskList.put(rs.getString("Description"), rs.getTime("Duration"));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
        
    public HashMap getTaskList(){
        getTaskListInfo();
        return taskList;
    }
    
    /**
     * Get all vehicle information from a single customer.
     * @param cusID 
     */
    public void getCusVehInfo(String regNo){
        vehData.clear();
        try {
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM `Vehicles` WHERE RegNo = ?");
            pstm.setString(1, regNo);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                vehData.add(new Vehicle(rs.getString("RegNo"), rs.getInt("EngSerial"), 
                        rs.getInt("ChassisNo"),rs.getString("Colour"), 
                        rs.getString("Make"), rs.getString("Model"), rs.getInt("Year"),
                        rs.getDate("LastMoTCheck")));
            }
            rs.close();
            pstm.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getCusVehData(String regNo){
        //vehicleCheckSum = setChecksum(conn,"Vehicles");
        getCusVehInfo(regNo);
        return vehData;
    }
    
    private void getStockInfo(){
        stockData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM sparepartstock");
            while(rs.next()){
                stockData.add(new StockItem(rs.getString("Code"), rs.getString("part_name"), 
                        rs.getString("manufacturer"),rs.getString("Vehicle_Type"), 
                        rs.getString("Year"), rs.getDouble("Price"), rs.getDouble("Cost_Per_Item"),
                        rs.getInt("Stock_Level"),rs.getDouble("Stock_Cost"),rs.getInt("Threshold_Level"),
                        rs.getInt("Inital_Stock_Level"),rs.getDouble("Inital_Stock_Cost")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }   
    }
    
    public ArrayList getStockData(){
        stockChecksum = setChecksum(conn, "SparePartStock");
        getStockInfo();
        return stockData;
    }
    

    
    public void assignJob(int jobID){
        if(!getChecksum(conn,"PendingJobs",pendingChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `pendingjobs` "
                        + "SET `StaffID` = ? WHERE `pendingjobs`.`JobID` = ? ");
                pstm.setInt(1, staffID);
                pstm.setInt(2, jobID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Job has been assigned to you!",
                    "Job assigned",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Job has not been assigned!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void alterJobStatus(int jobID, String status){
        if(!getChecksum(conn,"Jobs",jobChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try { 
                if(status.equals("COMPLETED")){
                    calculateTaskTime(jobID);
                }
                PreparedStatement pstm = conn.prepareStatement("UPDATE `jobs` SET `JobStatus` = ?"
                        + " WHERE `jobs`.`JobID` = ?");
                pstm.setString(1, status);
                pstm.setInt(2, jobID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Status altered!",
                    "Job status changed",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Job has not been assigned!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void calculateTaskTime(int jobID){
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT SEC_TO_TIME( SUM( TIME_TO_SEC( `Duration` ) ) ) AS timeSum FROM tasks WHERE JobID ="+jobID);
            Time time = null;
            while(rs.next()){
                time = rs.getTime("timeSum");
            }
            rs.close();
            st.close();
            
            PreparedStatement pstm = conn.prepareStatement("UPDATE `jobs` SET `Duration` = ?"
                        + " WHERE `jobs`.`JobID` = ?");
            pstm.setTime(1, time);
            pstm.setInt(2, jobID);
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void addNewTask(int jobID, String desc, int h, int m, int s){
        try {
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO `tasks` "
                    + "(`TaskID`, `JobID`, `Description`, `Duration`) "
                    + "VALUES (NULL, ?, ?, ?)");
            pstm.setInt(1, jobID);
            pstm.setString(2, desc);
            Time time = new Time(h,m,s);
            pstm.setTime(3, time);
            pstm.executeUpdate();
            pstm.close();
            JOptionPane.showMessageDialog(null,
                "The task has been added for the Job ID: "+jobID+"!",
                "Task added",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException e){
                JOptionPane.showMessageDialog(null,
                    "Task has not been added!",
                    "Operation cancelled out",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void alterTask(int taskID, String desc, int h, int m, int s){
    //UPDATE `tasks` SET `Description` = 'Windshield ', `Duration` = '00:10:0' WHERE `tasks`.`TaskID` = 1 
        try {
            PreparedStatement pstm = conn.prepareStatement("UPDATE `tasks` "
                    + "SET `Description` = ?, `Duration` = ? "
                    + "WHERE `tasks`.`TaskID` = ? ");
            pstm.setString(1, desc);
            Time time = new Time(h,m,s);
            pstm.setTime(2, time);
            pstm.setInt(3, taskID);
            pstm.executeUpdate();
            pstm.close();
            JOptionPane.showMessageDialog(null,
                "The task has been modified!",
                "Task altered",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (NullPointerException e){
                JOptionPane.showMessageDialog(null,
                    "Task has not been added!",
                    "Operation cancelled out",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void deleteTask(int taskID){
        try {
            PreparedStatement pstm = conn.prepareStatement("DELETE FROM Tasks WHERE TaskID=?;");
            pstm.setInt(1, taskID);
            pstm.executeUpdate();
            pstm.close();
            JOptionPane.showMessageDialog(null,
                "The task has been deleted!",
                "Task deleted",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void useStockPart(String code, int amount, double price, int jobID, int used){
        if(!getChecksum(conn,"SparePartStock",stockChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `sparepartstock` "
                        + "SET `Stock_Level` = ?, `Stock_Cost` = ? "
                        + "WHERE `sparepartstock`.`Code` = ?  ");
                pstm.setInt(1, amount);
                pstm.setDouble(2, amount*price);
                pstm.setString(3, code);
                pstm.executeUpdate();
                pstm = conn.prepareStatement("INSERT INTO `sparepartused` "
                        + "(`UsedID`, `Code`, `JobID`, `Used`, `DateUsed`) VALUES (NULL, ?, ?, ?, CURRENT_DATE())");
                pstm.setString(1, code);
                pstm.setInt(2, jobID);
                pstm.setInt(3, used);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "The Stock part has been assigned to the job!",
                    "Stock part used",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Job has not been assigned!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }   
    

    
    
}
