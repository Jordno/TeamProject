/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Customer;
import Entities.Job;
import Entities.StockDelivery;
import Entities.StockItem;
import Entities.Vehicle;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Wolf
 */
public class ReceptionistController extends ActorController {
    private Connection conn;
    private DatabaseConnection dbc;
    private ArrayList<Job> jobData;
    private ArrayList<Vehicle> vehData;
    private ArrayList<StockItem> stockData;
    private ArrayList<StockDelivery> deliveryData;
    private ArrayList<Customer> cusData;
    private byte[] jobChecksum;
    private byte[] vehChecksum;
    private byte[] stockChecksum;
    private byte[] deliveryChecksum;
    private byte[] customerChecksum;

    public ReceptionistController(DatabaseConnection dbc) {
        this.dbc = dbc;
        conn = dbc.getConnection();
        jobData = new ArrayList<>();
        vehData = new ArrayList<>();
        stockData = new ArrayList<>();
        deliveryData = new ArrayList<>();
        cusData = new ArrayList<>();
        jobChecksum = setChecksum(conn, "Jobs");
        vehChecksum = setChecksum(conn, "Vehicles");
        stockChecksum = setChecksum(conn, "SparePartStock");
        deliveryChecksum = setChecksum(conn, "SparePartDelivery");
        getJobInfo();
    }

    private void getJobInfo() {
        jobData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Jobs");
            while(rs.next()){
                jobData.add(new Job(rs.getInt("JobID"), rs.getString("RegNo"), 
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
    
    public ArrayList getJobData(){
        jobChecksum = setChecksum(conn, "Jobs");
        getJobInfo();
        return jobData;
    }
    
    private void getVehInfo(){
        vehData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Vehicles");
            while(rs.next()){
                vehData.add(new Vehicle(rs.getString("RegNo"), rs.getInt("EngSerial"), 
                        rs.getInt("ChassisNo"),rs.getString("Colour"), 
                        rs.getString("Make"), rs.getString("Model"), rs.getInt("Year"),
                        rs.getDate("LastMoTCheck")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getVehData(){
        vehChecksum = setChecksum(conn, "Vehicles");
        getVehInfo();
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
    
    private void getLowStockInfo(){
        stockData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM `sparepartstock` WHERE `Stock_Level` < `Threshold_Level`");
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
    
    public ArrayList getLowStockData(){
        //stockChecksum = setChecksum(conn, "SparePartStock");
        getLowStockInfo();
        return stockData;
    }
    
    private void getDeliveryInfo(String code){
        deliveryData.clear();
        try {
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM `SparePartDelivery` WHERE Code = ?");
            pstm.setString(1, code);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                deliveryData.add(new StockDelivery(rs.getInt("deliveryID"),rs.getString("code"), rs.getInt("Quantity"), 
                        rs.getBoolean("Recieved"), rs.getDouble("Stock_Cost"), rs.getDate("Date_Ordered")));
            }
            rs.close();
            pstm.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }    
    }
    
    public ArrayList getDeliveryData(String code){
        deliveryChecksum = setChecksum(conn, "SparePartDelivery");
        getDeliveryInfo(code);
        return deliveryData;
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
    
    public ArrayList getCusData(){
        customerChecksum = setChecksum(conn,"Customers"); //set the newest checksum
        getCusInfo(); //get most updated version of table
        return cusData;
    }
    /**
     * Get all vehicle information from a single customer.
     * @param cusID 
     */
    public void getCusVehInfo(int cusID){
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
    
    public ArrayList getCusVehData(int cusID){
        //vehicleCheckSum = setChecksum(conn,"Vehicles");
        getCusVehInfo(cusID);
        return vehData;
    }
    
    //UPDATE `jobs` SET `Duration` = ADDTIME(`Duration`,'00:25:30') WHERE `jobs`.`JobID` = 1 
    public void createJob(String regNo, String workReq, String bussType, int h, int m, int s){
        if(!getChecksum(conn,"Vehicles",vehChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                Time t = new Time(h,m,s);
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `Jobs` "
                        + "(`JobID`, `RegNo`, `Work_required`, `JobStatus`, `BusinessType`, `Duration`, `Date_Creation`) "
                        + "VALUES (NULL, ?, ?, \"IDLE\", ?, ?, CURRENT_DATE());");
                pstm.setString(1, regNo);
                pstm.setString(2, workReq);
                pstm.setString(3, bussType);
                pstm.setTime(4, t);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Job for "+regNo+" has been added!",
                    "Job created",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Job has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void alterJob(int jobID,  String workReq, String status, String bussType, int h, int m, int s){
            if(!getChecksum(conn,"Jobs",jobChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                Time t = new Time(h,m,s);
                PreparedStatement pstm = conn.prepareStatement("UPDATE `jobs` "
                        + "SET `Work_Required` = ?, `JobStatus` = ?, `BusinessType` = ?, `Duration` = ? "
                        + "WHERE `jobs`.`JobID` = ?");
                pstm.setString(1, workReq);
                pstm.setString(2, status);
                pstm.setString(3, bussType);
                pstm.setTime(4, t);
                pstm.setInt(5, jobID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Job has been altered!",
                    "Job altered",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Job has not been altered!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    
    }
    
    public void createStockItem(String code, String name, String manu, String type, String year, double price, int stock, int threshold, double cost){
        if(!getChecksum(conn,"SparePartStock",stockChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `sparepartstock` "
                        + "(`Code`, `Part_Name`, `Manufacturer`, `Vehicle_Type`, `Year`, `Price`, `Cost_Per_Item`, `Stock_Level`, `Stock_Cost`, `Threshold_Level`, `Inital_Stock_Level`, `Inital_Stock_Cost`) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                pstm.setString(1, code);
                pstm.setString(2, name);
                pstm.setString(3, manu);
                pstm.setString(4, type);
                pstm.setString(5, year);
                pstm.setDouble(6, price);
                pstm.setDouble(7, cost);
                pstm.setInt(8, stock);
                pstm.setDouble(9, cost*stock);
                pstm.setInt(10, threshold);
                pstm.setInt(11, stock);
                pstm.setDouble(12, cost*stock);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Stock Item for "+name+" has been added!",
                    "Stock created",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch(MySQLIntegrityConstraintViolationException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock code already exists!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock Item has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void alterStockItem(String code, String name, String manu, String type, String year, double price, int stock, int threshold, double cost, int istock, String stockID){
        if(!getChecksum(conn,"SparePartStock",stockChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `sparepartstock` "
                        + "SET `Code` = ?, `Part_Name` = ?, `Manufacturer` = ?, `Vehicle_Type` = ?,"
                        + "`Year` = ?, `Price` = ?, `Cost_Per_Item` = ?, `Stock_Level` = ?, `Stock_Cost` = ?, "
                        + "`Threshold_Level` = ?, `Inital_Stock_Level` = ?, `Inital_Stock_Cost` = ? "
                        + "WHERE `sparepartstock`.`Code` = ? ");
                pstm.setString(1, code);
                pstm.setString(2, name);
                pstm.setString(3, manu);
                pstm.setString(4, type);
                pstm.setString(5, year);
                pstm.setDouble(6, price);
                pstm.setDouble(7, cost);
                pstm.setInt(8, stock);
                pstm.setDouble(9, cost*stock);
                pstm.setInt(10, threshold);
                pstm.setInt(11, istock);
                pstm.setDouble(12, istock*cost);
                pstm.setString(13, stockID);
                pstm.executeUpdate();
                pstm.close();
                updateDeliveryCost(code, cost);
                JOptionPane.showMessageDialog(null,
                    "Stock Item for "+name+" has been altered!",
                    "Stock Altered",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch(MySQLIntegrityConstraintViolationException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock code already exists!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock Item has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void orderItem(String code, int quantity, double costItem){
        if(!getChecksum(conn,"SparePartDelivery",deliveryChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `sparepartdelivery` "
                        + "(`DeliveryID`, `Code`, `Quantity`, `Recieved`, `Stock_Cost`, `Date_Ordered`) "
                        + "VALUES (NULL, ?, ?, 0, ?, CURRENT_DATE())");
                pstm.setString(1, code);
                pstm.setInt(2, quantity);
                pstm.setDouble(3, quantity*costItem);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "An order for "+code+" has been made!",
                    "Stock Ordered",
                    JOptionPane.INFORMATION_MESSAGE);
            }catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock Item has not been ordered!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    public void recieveDelivery(int delID, int delStock, String code){
        if(!getChecksum(conn,"SparePartDelivery",deliveryChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `sparepartdelivery` "
                        + "SET `Recieved` = '1' WHERE `sparepartdelivery`.`DeliveryID` = ?");
                pstm.setInt(1, delID);
                pstm.executeUpdate();
                pstm = conn.prepareStatement("UPDATE `sparepartstock` "
                        + "SET `Stock_Level` = `Stock_Level` + ?, `Stock_Cost` = `Cost_Per_Item` * `Stock_Level` WHERE `sparepartstock`.`Code` = ? ");
                pstm.setInt(1,delStock);
                pstm.setString(2, code);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "The order has been recieved and stock has been updated!",
                    "Stock Ordered",
                    JOptionPane.INFORMATION_MESSAGE);
            }catch (SQLException ex) {
                ex.printStackTrace();
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Stock Item has not been ordered!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void deleteDelivery(int delID){
        if(!getChecksum(conn,"SparePartDelivery",deliveryChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("DELETE FROM SparePartDelivery WHERE DeliveryID=?;");
                pstm.setInt(1, delID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                    "Delivery has been deleted!",
                    "Delivery Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,
                        "Vehicle has not been deleted!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    
    /**
     * Update delivery cost when altered for items not recieved.
     * @param code
     * @param cost 
     */
    private void updateDeliveryCost(String code, double cost){
        try {
            PreparedStatement pstm = conn.prepareStatement("UPDATE `sparepartdelivery` "
                    + "SET `Stock_Cost` = `Quantity` * ? WHERE `sparepartdelivery`.`Code` = ? AND `Recieved` = 0");
            pstm.setDouble(1, cost);
            pstm.setString(2, code);
            pstm.executeUpdate();
            pstm.close();
        }catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public int lowStockAmount(){
        int amount = 0;
        try {
            Statement stm = conn.createStatement();
            //ResultSet rs = stm.executeQuery("SELECT * FROM `vehicles` WHERE `LastMoTCheck` < date_sub(now(), interval 1 month)");
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM `sparepartstock` WHERE `Stock_Level` < `Threshold_Level`");
            while(rs.next()){
                amount = rs.getInt("COUNT(*)");
            }
            System.out.println(amount);
        } catch (SQLException ex) {
            Logger.getLogger(FranchiseeController.class.getName()).log(Level.SEVERE, null, ex);
        }
           return amount;
           
    }
    
    public ArrayList searchJobByName(String name){
    //SELECT * FROM `jobs` INNER JOIN vehicles ON jobs.RegNo=vehicles.RegNo INNER JOIN customers ON vehicles.CustomerID=customers.CustomerID WHERE 1
        jobData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM `jobs` INNER JOIN vehicles ON jobs.RegNo=vehicles.RegNo INNER JOIN customers ON vehicles.CustomerID=customers.CustomerID WHERE name LIKE \"%"+name+"%\" ");
            while(rs.next()){
                jobData.add(new Job(rs.getInt("JobID"), rs.getString("RegNo"), 
                        rs.getString("Work_required"), rs.getString("JobStatus"), 
                        rs.getString("BusinessType"),
                        rs.getTime("Duration"), rs.getDate("Date_creation")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } 
        return jobData;
    }
    
    public void generateInvoice(int jobID, String regNo){
        double stockCost = 0;
        double labourCost = 0;
        double total;
        double VAT = 0;
        double grandTotal;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT Sum(sparepartstock.price*sparepartused.used) AS `stockCost` "
                    + "FROM `sparepartused` INNER JOIN sparepartstock "
                    + "ON sparepartused.Code=sparepartstock.Code "
                    + "INNER JOIN jobs "
                    + "ON sparepartused.JobID=jobs.JobID "
                    + "WHERE jobs.JobID ="+jobID);

            while(rs.next()){
                stockCost = rs.getDouble("stockCost");
            }
            
            rs = st.executeQuery("SELECT (labour_cost)*(Sum(TIME_TO_SEC(Duration)/3600)) AS `staffCost` "
                    + "FROM `tasks` "
                    + "INNER JOIN pendingjobs "
                    + "ON pendingjobs.JobID = tasks.JobID "
                    + "INNER JOIN staffaccounts "
                    + "ON pendingjobs.StaffID = staffaccounts.ID "
                    + "WHERE tasks.JobID ="+jobID);
            
            while(rs.next()){
                labourCost = rs.getDouble("staffCost");
            }
            total = labourCost+stockCost;
            
            rs = st.executeQuery("SELECT * FROM `config` WHERE `ConfigName` = \"VAT\"");
            
            while(rs.next()){
                VAT = rs.getDouble("Value");
            }
            
            VAT = VAT * total;
            grandTotal = VAT + total;
            rs.close();
            st.close();
            
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO `invoices` "
                    + "(`InvoiceID`, `JobID`, `StockAmount`, `LabourCost`, `Total`,`VAT`, `GrandTotal`, `Reminder`, `initalDate`, `lastDateReminder`, `paid`) "
                    + "VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?,0)");
            pstm.setInt(1, jobID);
            pstm.setDouble(2, stockCost);
            pstm.setDouble(3, labourCost);
            pstm.setDouble(4, total);
            pstm.setDouble(5, VAT);
            pstm.setDouble(6, grandTotal);
            pstm.setInt(7, 1);
            Date date = new Date(new java.util.Date().getTime());
            pstm.setDate(8, date);
            pstm.setDate(9, date);
            
            JasperReport jasperMasterReport = JasperCompileManager.compileReport(System.getProperty("user.dir")+"\\reporttemplates\\Invoice.jrxml");
            Map paraMap = new HashMap();
            paraMap.put("jobID",jobID);
            paraMap.put("SUBREPORT_DIR",System.getProperty("user.dir")+"\\reporttemplates\\");
            String fileDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY"));
            JasperPrint jp = JasperFillManager.fillReport(jasperMasterReport, paraMap, conn);
            JasperExportManager.exportReportToPdfFile(jp,
                  System.getProperty("user.dir")+"\\invoices\\"+regNo+fileDate+".pdf");
            JOptionPane.showMessageDialog(null,
                    "Invoice generated file: "+regNo+fileDate+".pdf has been created",
                    "Invoice created",
                    JOptionPane.INFORMATION_MESSAGE);
            
            pstm.executeUpdate();
            pstm.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
