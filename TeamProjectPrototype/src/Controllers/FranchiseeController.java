/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Customer;
import Entities.Staff;
import Entities.Vehicle;
import GUI.AdminGUI;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;

/**
 * Do the checksum thing for vehicle here 
 * @author Wolf
 */
public class FranchiseeController extends ActorController{
    private Connection conn;
    private DatabaseConnection dbc;
    private ArrayList<Customer> cusData;
    private ArrayList<Vehicle> vehData;
    private ArrayList<Staff> staffData;
    private ArrayList<String> busTypeData;
    private byte[] customerCheckSum;
    private byte[] vehicleCheckSum;
    public FranchiseeController(DatabaseConnection dbc) {
        this.dbc = dbc;
        conn = dbc.getConnection();
        cusData = new ArrayList<>();
        vehData = new ArrayList<>();
        staffData = new ArrayList<>();
        busTypeData = new ArrayList<>();
        customerCheckSum = setChecksum(conn,"Customers");
        vehicleCheckSum = setChecksum(conn, "Vehicles");
        getCusInfo();
    }

    private void getCusInfo(){
        cusData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM customers");

            while(rs.next()){
                cusData.add(new Customer(rs.getInt("CustomerID"), rs.getString("Name"), 
                        rs.getString("telephone"), rs.getString("mobile"), 
                        rs.getString("email"), rs.getString("address"), rs.getString("Street"), rs.getString("locality"),
                        rs.getString("city"), rs.getString("postcode"),rs.getString("Additional_Notes"), rs.getString("customerType")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public void getVehInfo(int cusID){
        vehData.clear();
        try {
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM `Vehicles` WHERE CustomerID = ?");
            pstm.setInt(1, cusID);
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
    
    private void getStaffInfo(){
        staffData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM staffAccounts WHERE `Type` = \"Mechanic\" OR Type = \"FOREPERSON\"");
            while(rs.next()){
                staffData.add(new Staff(rs.getInt("ID"), rs.getString("username"), 
                        rs.getBytes("password"), rs.getString("type"), 
                        rs.getString("surname"), rs.getString("name"),
                        rs.getDate("date_creation"), rs.getDouble("labour_cost")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList getStaffData(){
        getStaffInfo(); //get most updated version of table
        return staffData;
    }
    
    public ArrayList getCusData(){
        customerCheckSum = setChecksum(conn,"Customers"); //set the newest checksum
        getCusInfo(); //get most updated version of table
        return cusData;
    }
    
    public ArrayList getVehData(int cusID){
        vehicleCheckSum = setChecksum(conn,"Vehicles");
        getVehInfo(cusID);
        return vehData;
    }
    
    public void getBusInfo(){
        busTypeData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM businesstypes");
            while(rs.next()){
                busTypeData.add(rs.getString("Type"));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
        
    public ArrayList getBusData(){
        getBusInfo();
        return busTypeData;
    }
    
    
    public void createCustomer(String fname, String tele, String mobile, String email, String address, String street, String locality, String city, String pcode, String note, String type ){
        if(!getChecksum(conn,"Customers",customerCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `customers` "
                        + "(`CustomerID`, `Name`, `Telephone`, `Mobile`, `Email`, `Address`, `Street`, `Locality`, `"
                        + "City`, `PostCode`, `Additional_Notes`, `CustomerType`, `Discount_ID`) "
                        + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL)");
                pstm.setString(1, fname);
                pstm.setString(2, tele);
                pstm.setString(3, mobile);
                pstm.setString(4, email);
                pstm.setString(5, address);
                pstm.setString(6, street);
                pstm.setString(7, locality);
                pstm.setString(8, city);
                pstm.setString(9, pcode);
                pstm.setString(10, note);
                pstm.setString(11, type);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Customer "+fname+" added!",
                    "Customer created",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Customer has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void alterCustomer(String fname, String tele, String mobile, String email, String address, String street, String locality, String city, String pcode, String note, String type, int cusID){
        if(!getChecksum(conn,"Customers",customerCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `customers` SET "
                        + "`Name` = ?, `Telephone` = ?, `Mobile` = ?, `Email` = ?, `Address` = ?, `Street` = ?, "
                        + "`Locality` = ?, `City` = ?, `PostCode` = ?, `Additional_Notes` = ?, `CustomerType` = ? "
                        + "WHERE `customers`.`CustomerID` = ?");
                pstm.setString(1, fname);
                pstm.setString(2, tele);
                pstm.setString(3, mobile);
                pstm.setString(4, email);
                pstm.setString(5, address);
                pstm.setString(6, street);
                pstm.setString(7, locality);
                pstm.setString(8, city);
                pstm.setString(9, pcode);
                pstm.setString(10, note);
                pstm.setString(11, type);
                pstm.setInt(12, cusID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Customer "+fname+" Altered!",
                    "Customer Altered",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Customer has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void deleteCustomer(int cusID){
        if(!getChecksum(conn,"Customers",customerCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("DELETE FROM Customers WHERE CustomerID=?;");
                pstm.setInt(1, cusID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Customer has been deleted!",
                    "Customer Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Customer has not been deleted!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    
    public void addVehicle(String regNo, int cusID, int engNo, String colour, String make, String model, int chassNo, int year, Date motDate){
        if(!getChecksum(conn,"Vehicles",vehicleCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `vehicles` "
                        + "(`RegNo`, `CustomerID`, `EngSerial`, `Colour`, `Make`, `Model`, `ChassisNo`, `Year`, `LastMoTCheck`) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
                pstm.setString(1, regNo);
                pstm.setInt(2, cusID);
                pstm.setInt(3, engNo);
                pstm.setString(4, colour);
                pstm.setString(5, make);
                pstm.setString(6, model);
                pstm.setInt(7, chassNo);
                pstm.setInt(8, year);
                pstm.setDate(9, motDate);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Vehicle "+regNo+" added!",
                    "Vehicle created",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch(MySQLIntegrityConstraintViolationException e){
                    JOptionPane.showMessageDialog(null,
                        "Registration Number already exists",
                        "Not added",
                        JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Vehicle has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    
    public void alterVehicle(String regNo, String vehID, int engNo, String colour, String make, String model, int chassNo, int year, Date motDate){
        if(!getChecksum(conn,"Vehicles",vehicleCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `vehicles` "
                        + "SET `RegNo` = ?, `EngSerial` = ?, `Colour` = ?, `Make` = ?, `Model` = ?, `ChassisNo` = ?, "
                        + "`Year` = ?, `LastMoTCheck` = ? "
                        + "WHERE `vehicles`.`RegNo` = ?");
                pstm.setString(1, regNo);
                pstm.setInt(2, engNo);
                pstm.setString(3, colour);
                pstm.setString(4, make);
                pstm.setString(5, model);
                pstm.setInt(6, chassNo);
                pstm.setInt(7, year);
                pstm.setDate(8, motDate);
                pstm.setString(9, vehID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Vehicle "+regNo+" has been altered!",
                    "Vehicle Altered",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch(MySQLIntegrityConstraintViolationException e){
                    JOptionPane.showMessageDialog(null,
                        "Registration Number already exists",
                        "Not added",
                        JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Vehicle has not been altered!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void deleteVehicle(String vehID){
        if(!getChecksum(conn,"Vehicles",vehicleCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("DELETE FROM Vehicles WHERE RegNo=?;");
                pstm.setString(1, vehID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Vehicle has been deleted!",
                    "Vehicle Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Vehicle has not been deleted!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public int checkDueMot(){
        int amount = 0;
        try {
            Statement stm = conn.createStatement();
            //ResultSet rs = stm.executeQuery("SELECT * FROM `vehicles` WHERE `LastMoTCheck` < date_sub(now(), interval 1 month)");
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM `vehicles` WHERE `LastMoTCheck` < date_sub(now(), interval 1 month)");
            while(rs.next()){
                amount = rs.getInt("COUNT(*)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FranchiseeController.class.getName()).log(Level.SEVERE, null, ex);
        }
           return amount;
    }
    
    public void generateSpartPartReport(){
        try{
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(System.getProperty("user.dir")+"\\reporttemplates\\sparepartsreport.jrxml");
            String fileDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY"));
            JasperPrint jp = JasperFillManager.fillReport(jasperMasterReport, null, conn);
            JasperExportManager.exportReportToPdfFile(jp,
                  System.getProperty("user.dir")+"\\reports\\sparepartsreport-"+fileDate+".pdf");
            JOptionPane.showMessageDialog(null,
                    "Report generated file: sparepartsreport-"+fileDate+".pdf has been created",
                    "Report created",
                    JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generateAvgPriceReport(int staffID, String surname, String name){
        try{
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(System.getProperty("user.dir")+"\\reporttemplates\\avgJobPriceReport.jrxml");
            String fileDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY"));
            HashMap paraMap = new HashMap<>();
            paraMap.put("staffID",staffID);
            JasperPrint jp = JasperFillManager.fillReport(jasperMasterReport, paraMap, conn);
            JasperExportManager.exportReportToPdfFile(jp,
                  System.getProperty("user.dir")+"\\reports\\avgJobPriceReport-"+surname+name+"-"+fileDate+".pdf");
            JOptionPane.showMessageDialog(null,
                    "Report generated file: avgJobPriceReport-"+surname+name+"-"+fileDate+".pdf has been created",
                    "Report created",
                    JOptionPane.INFORMATION_MESSAGE);
        }catch(Exception e){
            e.printStackTrace();
        }
    
    
    }
 
}
