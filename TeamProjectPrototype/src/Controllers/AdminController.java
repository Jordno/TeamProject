/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import GUI.AdminGUI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import Entities.Staff;
import Entities.Task;
import Helpers.PasswordHashing;
import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Wolf
 */
public class AdminController extends ActorController{
    private Connection conn;
    private DatabaseConnection dbc;
    private ArrayList<Staff> staffData; //handles the Staff data
    private ArrayList<Task> taskListData;
    private byte[] staffCheckSum; //used to check if data is most recent
    private byte[] taskListChecksum;
    /**
     * Retrieves the connection from the Controller class, and initalizes the staff
     * data to be used in the GUI. ArrayList is then populated, and then creates the GUI
     * @param conn Connection to the database
     */
    public AdminController(DatabaseConnection dbc){
        this.dbc = dbc;
        conn = dbc.getConnection();
        staffData = new ArrayList<>();
        taskListData = new ArrayList<>();
        staffCheckSum = setChecksum(conn,"staffAccounts");
        taskListChecksum = setChecksum(conn,"Tasklist");
        getStaffInfo();
    }
    
//    /**
//     * This function is used as apart of concurrency system. It will generate a checksum
//     * value with the information in the database and store it. This can be used to do a check
//     * against the most recent checksum to see if the table has been altered.
//     */
//    public void setChecksum(){
//        try {
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery("CHECKSUM TABLE staffAccounts;");
//            while(rs.next()){
//                checkSum = rs.getBytes("Checksum");
//            }
//            rs.close();
//            st.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    
//    /**
//     * This function is part of the concurrency system. It will return the most recent checksum value,
//     * this is then checked against the stored value to see if there has been change in the data
//     * @return byte[] - New checksum value
//     */
//    public boolean getChecksum(){
//        byte[] newCheckSum = checkSum;
//        try {
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery("CHECKSUM TABLE staffAccounts;");
//            while(rs.next()){
//                newCheckSum = rs.getBytes("Checksum");
//            }
//            rs.close();
//            st.close();
//        } catch (SQLException ex) {
//            Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        //return newCheckSum;
//        return Arrays.equals(checkSum,newCheckSum);
//    }
//    
    /**
     * Populates the ArrayList with staff data that is located in the staffAccounts table. 
     */
    private void getStaffInfo(){
        staffData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM staffAccounts");
            while(rs.next()){
                staffData.add(new Staff(rs.getInt("ID"), rs.getString("username"), 
                        rs.getBytes("password"), rs.getString("type"), 
                        rs.getString("surname"), rs.getString("name"),
                        rs.getDate("date_creation"), rs.getDouble("labour_cost")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Simple get method used to retrieve the staff information.
     * The method will call setChecksum and getInfo to make sure that the data returned
     * is the most recent.
     * Used in the AdminGUI, as it's needed by the StaffTableModel.
     * @return 
     */
    public ArrayList getStaffData(){
        staffCheckSum = setChecksum(conn,"staffAccounts"); //set the newest checksum
        getStaffInfo(); //get most updated version of table
        return staffData;
    }
    
        /**
     * Populates the ArrayList with staff data that is located in the staffAccounts table. 
     */
    private void getTaskListInfo(){
        taskListData.clear();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tasklist");
            while(rs.next()){
                taskListData.add(new Task(rs.getInt("ListID"), rs.getString("Description"), rs.getTime("Duration")));
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Simple get method used to retrieve the staff information.
     * The method will call setChecksum and getInfo to make sure that the data returned
     * is the most recent.
     * Used in the AdminGUI, as it's needed by the StaffTableModel.
     * @return 
     */
    public ArrayList getTaskListData(){
        taskListChecksum = setChecksum(conn,"tasklist"); //set the newest checksum
        getTaskListInfo(); //get most updated version of table
        return taskListData;
    }
    
    /**
     * This method is called when the Admin account tries to rename a username.
     * @param username 
     */
    public void changeUsername(String username){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            String newname;
            try {
                //Statement stmt = conn.createStatement();
                PreparedStatement pstm = conn.prepareStatement("UPDATE staffAccounts SET username=? WHERE username=?;");

                do{
                    newname = JOptionPane.showInputDialog(null, "Enter a new username: (must be at least 3 characters long)", "Please enter username", 
                            JOptionPane.QUESTION_MESSAGE);
                }while(newname.length() < 3);

                pstm.setString(1, newname);
                pstm.setString(2, username);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Username not changed!",
                        "Operation cancelled out",
                JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    /**
     * This method changes the selected user accounts password
     * @param username 
     */
    public void changePassword(String username){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            String newpassword;
            PasswordHashing ph = new PasswordHashing();
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE staffAccounts SET password=? WHERE username=?;");

                do{
                    //JPasswordField pwd = new JPasswordField();
                    newpassword = JOptionPane.showInputDialog(null,"Enter a new password for "+username
                            +": (must be at least 3 characters long)","Please enter password", JOptionPane.QUESTION_MESSAGE);
                }while(newpassword.length() < 3);
                
                byte[] hashedpw = ph.hashPass(newpassword);
                pstm.setBytes(1, hashedpw);
                pstm.setString(2, username);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Password has not been changed!",
                        "Operation cancelled out",
                JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    
    public void changeType(String username){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            String[] types = { "ADMIN","FRANCHISEE","FOREPERSON","MECHANIC","RECEPTIONIST" };
            String newType = null;
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE staffAccounts SET type=? WHERE username=?;");

                newType = (String) JOptionPane.showInputDialog(null, "Select a new account type for "+username+":","Changing account type",
                        JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
                if(newType != null){
                    pstm.setString(1, newType);
                    pstm.setString(2, username);
                    pstm.executeUpdate();
                    pstm.close();
                }else{
                JOptionPane.showMessageDialog(null,
                    "Account type has not been changed!",
                    "Operation cancelled out",
                    JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void deleteAccount(String username){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("DELETE FROM staffAccounts WHERE username=?;");
                pstm.setString(1, username);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    
    }
    
    
    public void addNewUser(String username, String password, String type, String surname, String name){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            PasswordHashing ph = new PasswordHashing();
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `staffaccounts` "
                        + "(`ID`, `UserName`, `Password`, `Type`, `Surname`, `Name`, `Date_creation`) "
                        + "VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_DATE());");
                byte[] hashedpw = ph.hashPass(password);
                pstm.setString(1, username);
                pstm.setBytes(2, hashedpw);
                pstm.setString(3, type);
                pstm.setString(4, surname);
                pstm.setString(5, name);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "User has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    //called if labour cost is needed eg. forperson/mech
    public void addNewUser(String username, String password, String type, String surname, String name, double labour){
        if(!getChecksum(conn,"staffAccounts",staffCheckSum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            PasswordHashing ph = new PasswordHashing();
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `staffaccounts` "
                        + "(`ID`, `UserName`, `Password`, `Type`, `Surname`, `Name`, `Date_creation`, `Labour_cost`) "
                        + "VALUES (NULL, ?, ?, ?, ?, ?, CURRENT_DATE(),?);");
                byte[] hashedpw = ph.hashPass(password);
                pstm.setString(1, username);
                pstm.setBytes(2, hashedpw);
                pstm.setString(3, type);
                pstm.setString(4, surname);
                pstm.setString(5, name);
                pstm.setDouble(6, labour);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "User has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
        
        
    private String SQLDirectoryChooser(){
        JFileChooser chooser = new JFileChooser("C:\\xampp");
        String mySQLDir = null;
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select MySQL folder");
        int returnVal = chooser.showOpenDialog(new javax.swing.JFrame());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
             mySQLDir = chooser.getSelectedFile().getAbsolutePath();
        }
        return mySQLDir;
    }
    
    public void backupDatabase(){
        String mySQLDir = SQLDirectoryChooser();
        if(mySQLDir == null){
                JOptionPane.showMessageDialog(null,
                    "Backup cancelled!",
                    "Operation has be cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
        }else{
            try {
                String folderPath =  System.getProperty("user.dir")+"\\backup";
                //create directory if it don't exist.
                new File(folderPath).mkdir();
                //Creates a cmd command to be used later on, uses mysqldump to backup
                String executeCmd = mySQLDir+"\\bin\\mysqldump -u root --password= --databases gartisdb -r " 
                        +folderPath+ "\\garitsDBBackup"
                        +LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-YYYY%HH-mm-ss"))
                        +".sql";
                //executes the command, and waits for the outcome.
                Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                int processComplete = runtimeProcess.waitFor();           
                //Tell the user if the outcome was succesful or not
                if (processComplete == 0) {
                    JOptionPane.showMessageDialog(null,
                        "Backup was succesful!",
                        "Backup completed",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Back up has failed, please try again",
                        "Backup has failed",
                        JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                        "An error has occured while backing up! "+ex.getMessage(),
                        "Backup has failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void restoreDatabase(){
        String mySQLDir = SQLDirectoryChooser();
        
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir")+"\\backup");
        chooser.setFileFilter(new FileNameExtensionFilter("SQL File", "sql"));
        chooser.setAcceptAllFileFilterUsed(false); //not all files only sql
        String backupPath = null;
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Select Backup file");
        int returnVal = chooser.showOpenDialog(new javax.swing.JFrame());
        if(returnVal == JFileChooser.APPROVE_OPTION) {
             backupPath = chooser.getSelectedFile().getAbsolutePath();
        }
        
        dbc.dropDB();
        dbc.createDB();
        
       // System.out.println(backupFile);
        try {
            File backupFile = new File(backupPath);
            String[] command = new String[]{mySQLDir+"\\bin\\mysql ", "-uroot", "--password=", "gartisdb"};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder = new ProcessBuilder(Arrays.asList(command));
            processBuilder.redirectError(Redirect.INHERIT);
            processBuilder.redirectInput(Redirect.from(backupFile));

            Process process = processBuilder.start();
            int processComplete = process.waitFor();
            
            //debug code
//            InputStream stdin = runtimeProcess.getInputStream();
//            InputStreamReader isr = new InputStreamReader(stdin);
//            BufferedReader br = new BufferedReader(isr);
//            String line = null;
//            System.out.println("<OUTPUT>");
//            while ( (line = br.readLine()) != null)
//                System.out.println(line);
//            System.out.println("</OUTPUT>");
//            int processComplete = runtimeProcess.waitFor();           
//            System.out.println("Process exitValue: " + processComplete);
//            //int processComplete = runtimeProcess.waitFor();
//
//            //Tell the user if the outcome was succesful or not
            if (processComplete == 0) {
                JOptionPane.showMessageDialog(null,
                    "Restore was succesful!",
                    "Backup completed",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Restore has failed, please try again",
                    "Backup has failed",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "An error has occured while backing up! "+ex.getMessage(),
                    "Backup has failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addTask(String desc, int hour, int min, int sec){
        if(!getChecksum(conn,"taskList",taskListChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO `tasklist` "
                        + "(`ListID`, `Description`, `Duration`) "
                        + "VALUES (NULL, ?, ?);");
                Time time = new Time(hour,min,sec);
                pstm.setString(1, desc);
                pstm.setTime(2, time);
                pstm.executeUpdate();
                pstm.close();
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Task has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    public void alterTask(int listID, String desc, int hour, int min, int sec){
        if(!getChecksum(conn,"taskList",taskListChecksum)){
            JOptionPane.showMessageDialog(null,
                    "The previous action has failed as the table contains old records."
                            + "\nThe data will now be updated!",
                    "Table has been altered!",
            JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                PreparedStatement pstm = conn.prepareStatement("UPDATE `tasklist` SET"
                        + " `Description` = ?, `Duration` = ? "
                        + "WHERE `tasklist`.`ListID` = ? ");
                Time time = new Time(hour,min,sec);
                pstm.setString(1, desc);
                pstm.setTime(2, time);
                pstm.setInt(3, listID);
                pstm.executeUpdate();
                pstm.close();
                JOptionPane.showMessageDialog(null,
                        "Task has been altered!",
                        "Task altered",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException e){
                    JOptionPane.showMessageDialog(null,
                        "Task has not been added!",
                        "Operation cancelled out",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    
    
    
    }
}
