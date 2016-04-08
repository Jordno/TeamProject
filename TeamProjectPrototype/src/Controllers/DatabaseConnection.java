/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import Helpers.PasswordHashing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import teamprojectprototype.TeamProjectPrototype;

/**
 *
 * @author Wolf
 */
public class DatabaseConnection {
    private String URL = "jdbc:mysql://localhost:3306/"; //connection to database
    private String USER = "root"; //database username
    private String PASS = "";   //database password
    private Connection conn;
    
    /**
     * Initlizes the connection, then makes sure that GartisDB exists, alongside
     * it's tables. It will also setup a default admin account if there isn't one currently
     */
    public DatabaseConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(URL, USER, PASS);
            createDB(); //creates DB if it doesnt exist
            conn.close(); //reset the conection to the database
            URL = URL+"GartisDB"; //goes into the GartisDB to setup the tables
            conn = DriverManager.getConnection(URL, USER, PASS);
            createTables(); //creates table if it doesnt exist
            setupDefaultAdmin();
            setupDefaultTaskList();
            setupDefaultBussinessTypes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to database, please make sure it is running.",
                    "Connection Error",
            JOptionPane.ERROR_MESSAGE);
            //used for debugging
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Creates the database if it does not exist.
     */
    public void createDB(){
        try {
            Statement stm = conn.createStatement();
            stm.execute("CREATE DATABASE IF NOT EXISTS GartisDB;");
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void dropDB(){
        try {
            Statement stm = conn.createStatement();
            stm.execute("DROP DATABASE gartisdb;");
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Creates the tables if they do not exist.
     */
    /*
CREATE TABLE `SparePartsStock`
(Code int(10) NOT NULL,
`Part Name` varchar(255) NOT NULL,
Manufacturer varchar(255) NOT NULL,
`Vehicle Type` varchar(255) NOT NULL,
Years date NOT NULL,
`Initial Cost` double NOT NULL,
`Stock Cost` double NOT NULL,
`Initial Stock Level` int(10) NOT NULL,
`New Stock Level` int(10) NOT NULL,
`Low Level Threshold` int(10) NOT NULL,
PRIMARY KEY (Code));
                        
                    + "Low_Level_Threshold int NOT NULL,"
                    + "Initial_Cost double NOT NULL,"
                    + "Stock_Cost double NOT NULL,"
                    + "Initial_Stock_Level int NOT NULL,"
                    + "New_Stock_Level int NOT NULL,"
                    + "Low_Level_Threshold int NOT NULL,"
    */
    private void createTables(){
        try {
            Statement stm = conn.createStatement();
            stm.execute("CREATE TABLE IF NOT EXISTS StaffAccounts("
                    + "ID int NOT NULL AUTO_INCREMENT,"
                    + "UserName varchar(255) NOT NULL,"
                    + "Password varbinary(512) NOT NULL,"
                    + "Type varchar(255) NOT NULL,"
                    + "Surname varchar(255) NOT NULL,"
                    + "Name varchar(255) NOT NULL,"
                    + "Date_creation date NOT NULL,"
                    + "Labour_cost double,"
                    + "PRIMARY KEY (ID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Discount("
                    + "Discount_ID int NOT NULL,"
                    + "Type_ID varchar(255) NOT NULL,"
                    + "PRIMARY KEY (Discount_ID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Customers("
                    + "CustomerID int NOT NULL AUTO_INCREMENT,"
                    + "Name varchar(255) NOT NULL,"
                    + "Telephone varchar(255) NOT NULL,"
                    + "Mobile varchar(255),"
                    + "Email varchar(255),"
                    + "Address varchar(255) NOT NULL,"
                    + "Street varchar(255) NOT NULL,"
                    + "Locality varchar(255) NOT NULL,"
                    + "City varchar(255) NOT NULL,"
                    + "PostCode varchar(255) NOT NULL,"
                    + "Additional_Notes varchar(255),"
                    + "CustomerType varchar(255) NOT NULL,"
                    + "Discount_ID int,"
                    + "FOREIGN KEY (Discount_ID) REFERENCES Discount(Discount_ID),"
                    + "PRIMARY KEY (CustomerID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Vehicles("
                    + "RegNo varchar(255) NOT NULL,"
                    + "CustomerID int NOT NULL,"
                    + "FOREIGN KEY(CustomerID) REFERENCES Customers(CustomerID),"
                    + "EngSerial int NOT NULL,"
                    + "Colour varchar(255),"
                    + "Make varchar(255)," 
                    + "Model varchar(255)," 
                    + "ChassisNo int NOT NULL,"
                    + "Year int NOT NULL," 
                    + "LastMoTCheck date," 
                    + "PRIMARY KEY (RegNo));");
            stm.execute("CREATE TABLE IF NOT EXISTS Fixed(" 
                    + "Fixed_ID int NOT NULL,"
                    + "Discount_ID int NOT NULL UNIQUE,"
                    + "Fixed_Rate decimal (4,2),"
                    + "PRIMARY KEY (Fixed_ID)," 
                    + "FOREIGN KEY (Discount_ID) REFERENCES Discount(Discount_ID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Jobs("
                    + "JobID int NOT NULL AUTO_INCREMENT,"
                    + "RegNo varchar(255) NOT NULL,"
                    + "FOREIGN KEY(RegNo) REFERENCES Vehicles(RegNo) ON UPDATE CASCADE,"
                    + "Work_Required varchar(255) NOT NULL,"
                    + "JobStatus varchar(1024) NOT NULL,"
                    + "BusinessType varchar(255) NOT NULL,"
                    + "Duration time NOT NULL,"
                    + "Date_creation date NOT NULL,"
                    + "PRIMARY KEY(JobID));");
            stm.execute("CREATE TABLE IF NOT EXISTS SparePartStock("
                    + "Code varchar(255) NOT NULL,"
                    + "Part_Name varchar(255) NOT NULL,"
                    + "Manufacturer varchar(255) NOT NULL,"
                    + "Vehicle_Type varchar(255) NOT NULL,"
                    + "Year varchar(255) NOT NULL,"
                    + "Price double NOT NULL,"
                    + "Cost_Per_Item double NOT NULL,"
                    + "Stock_Level int NOT NULL,"
                    + "Stock_Cost double NOT NULL,"
                    + "Threshold_Level int NOT NULL,"
                    + "Inital_Stock_Level int NOT NULL,"
                    + "Inital_Stock_Cost double NOT NULL,"
                    + "PRIMARY KEY (Code));");
            stm.execute("CREATE TABLE IF NOT EXISTS SparePartDelivery("
                    + "DeliveryID int NOT NULL AUTO_INCREMENT,"
                    + "Code varchar(255) NOT NULL,"
                    + "FOREIGN KEY(Code) REFERENCES SparePartStock(Code),"
                    + "Quantity int NOT NULL,"
                    + "Recieved boolean NOT NULL,"
                    + "Stock_Cost double NOT NULL,"
                    + "Date_Ordered date NOT NULL,"
                    + "PRIMARY KEY(DeliveryID));");
            stm.execute("CREATE TABLE IF NOT EXISTS SparePartUsed("
                    + "UsedID int NOT NULL AUTO_INCREMENT,"
                    + "Code varchar(255) NOT NULL,"
                    + "FOREIGN KEY(Code) REFERENCES SparePartStock(Code),"
                    + "JobID int NOT NULL,"
                    + "FOREIGN KEY(JobID) REFERENCES Jobs(JobID),"
                    + "Used int NOT NULL,"
                    + "DateUsed date NOT NULL,"
                    + "PRIMARY KEY(UsedID));");
            stm.execute("CREATE TABLE IF NOT EXISTS PendingJobs("
                    + "PendingJobID int NOT NULL AUTO_INCREMENT,"
                    + "JobID int NOT NULL,"
                    + "FOREIGN KEY(JobID) REFERENCES Jobs(JobID),"
                    + "StaffID int,"
                    + "FOREIGN KEY(StaffID) REFERENCES StaffAccounts(ID),"
                    + "PRIMARY KEY(PendingJobID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Tasks("
                    + "TaskID int NOT NULL Auto_INCREMENT,"
                    + "JobID int NOT NULL,"
                    + "FOREIGN KEY(JobID) REFERENCES Jobs(JobID),"
                    + "Description varchar(255) NOT NULL,"
                    + "Duration time NOT NULL,"
                    + "PRIMARY KEY(TaskID));");
            stm.execute("CREATE TABLE IF NOT EXISTS Invoices("
                    + "InvoiceID int NOT NULL AUTO_INCREMENT,"
                    + "JobID int NOT NULL,"
                    + "FOREIGN KEY(JobID) REFERENCES Jobs(JobID),"
                    + "StockAmount double,"
                    + "LabourCost double,"
                    + "Total double,"
                    + "VAT double(11,2),"
                    + "GrandTotal double(11,2) NOT NULL,"
                    + "Reminder int NOT NULL,"
                    + "initalDate date NOT NULL,"
                    + "lastDateReminder date NOT NULL,"
                    + "Paid boolean NOT NULL,"
                    + "PRIMARY KEY(InvoiceID));");
            stm.execute("CREATE TABLE IF NOT EXISTS config("
                    + "ConfigID int NOT NULL AUTO_INCREMENT,"
                    + "ConfigName varchar(255) NOT NULL,"
                    + "Value double NOT NULL,"
                    + "PRIMARY KEY(ConfigID));");
            stm.execute("CREATE TABLE IF NOT EXISTS TaskList("
                    + "ListID int NOT NULL AUTO_INCREMENT,"
                    + "Description varchar(255) NOT NULL,"
                    + "Duration time NOT NULL,"
                    + "PRIMARY KEY(ListID));");
            stm.execute("CREATE TABLE IF NOT EXISTS BusinessTypes("
                    + "BusID int NOT NULL AUTO_INCREMENT,"
                    + "Type varchar(255) NOT NULL,"
                    + "PRIMARY KEY(BusID));");
            stm.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Setups the default Admin account.
     * Username: admin
     * Password: password
     */
    private void setupDefaultAdmin(){
        PasswordHashing ph = new PasswordHashing();
        byte[] dapass = ph.hashPass("password"); //hash passwords to prevent plaintext
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM staffaccounts WHERE `TYPE` = \"ADMIN\" "); //check if an admin account already exists
            if(!rs.isBeforeFirst()){ //if no admin accounts make a default one
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO staffaccounts(`ID`, `UserName`, `Password`, `Type`, `surname`, `name`, `Date_creation`) "
                        + "VALUES (NULL,?,?,?,\"Default\",\"Account\",CURRENT_DATE)");
                pstm.setString(1, "admin");
                pstm.setBytes(2, dapass);
                pstm.setString(3, "ADMIN");
                pstm.executeUpdate();
                pstm.close();
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TeamProjectPrototype.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setupDefaultTaskList(){
        //Predefined Tasks, Oil Check, Engine Check, Tyre Replacement, Fender Replacement, Windscreen Replacement
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tasklist");
            if(!rs.isBeforeFirst()){
                HashMap<String,Time> taskList = new HashMap<>();
                taskList.put("Oil Check", new Time(00,45,00));
                taskList.put("Engine Check", new Time(01,00,00));
                taskList.put("Tyre Replacement", new Time(00,30,00));
                taskList.put("Fender Replacement", new Time(01,20,00));
                taskList.put("Windscreen Replacement", new Time(01,30,00));
                for (Map.Entry<String, Time> entry : taskList.entrySet()) {
                    PreparedStatement pstm = conn.prepareStatement("INSERT INTO `tasklist` "
                        + "(`ListID`, `Description`, `Duration`) VALUES "
                        + "(NULL, ?, ?)");
                    pstm.setString(1, entry.getKey());
                    pstm.setTime(2, entry.getValue());
                    pstm.executeUpdate();
                    pstm.close();
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TeamProjectPrototype.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setupDefaultBussinessTypes(){
        //Predefined Tasks, Oil Check, Engine Check, Tyre Replacement, Fender Replacement, Windscreen Replacement
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM `businesstypes`");
            if(!rs.isBeforeFirst()){
                ArrayList<String> busType = new ArrayList<>();
                busType.add("Repair");
                busType.add("MoT");
                busType.add("Annual Service");
                for (String s: busType) {
                    PreparedStatement pstm = conn.prepareStatement("INSERT INTO `businesstypes` "
                            + "(`BusID`, `Type`) VALUES (NULL, ?)");
                    pstm.setString(1, s);
                    pstm.executeUpdate();
                    pstm.close(); 
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(TeamProjectPrototype.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns the current database connection, used in controller classes in the
     * logic
     * @return Connection
     */
    public Connection getConnection(){
        return conn;
    }
}
