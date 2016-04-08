/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;


import GUI.AdminGUI;
import GUI.Franchisee.FranchiseeGUI;
import GUI.Mechanic.MechanicGUI;
import GUI.Receptionist.ReceptionistGUI;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import Helpers.PasswordHashing;
import teamprojectprototype.TeamProjectPrototype;

/**
 *
 * @author Wolf
 */
public class LoginController {
    private boolean loginStatus; //used to check if login was succesful
    private Connection conn;
    private DatabaseConnection dbc;

    /**
     * Basic controller class, used to init the database connection, then setup
     * the GUI depending on the login.
     */
    public LoginController() {
        dbc = new DatabaseConnection();
        conn = dbc.getConnection();
        loginStatus = false;
    }

    /**
     * Login logic - This is called when the user tries to login through the GUI.
     * Using the information that user puts in, the password is hashed and query is sent
     * to the database to check if it's correct information. If the information is correct
     * initalizeGUI will be called, sending the type of user as a parameter.
     * @param username Retrieved from username section of login GUI
     * @param password Retrieved from the password section of login GUI
     */
    public void login(String username, String password){
        PasswordHashing ph = new PasswordHashing();
        try {
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM staffaccounts WHERE `username` = ? AND `password` = ? ");
            pstm.setString(1, username);
            byte[] hashpw = ph.hashPass(password);
            pstm.setBytes(2, hashpw);
            ResultSet rs = pstm.executeQuery(); //Check if account exists.
            if(!rs.isBeforeFirst()){
                loginStatus = false;
                JOptionPane.showMessageDialog(null,
                    "Username/password is invalid.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            }else{
                rs.next();
                String type = rs.getString("type");
                int staffID = rs.getInt("ID");
                loginStatus = true;
                initalizeGUI(type, staffID);
            }
            rs.close();
            pstm.close();
        } catch (SQLException ex) {
            Logger.getLogger(TeamProjectPrototype.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Calls specialized Controller class that will deal with the type of user 
     * that has logged in.
     * @param type Retrieved from the DB
     */
    public void initalizeGUI(String type, int staffID){
        switch(type.toUpperCase()){
            case "ADMIN":
                new AdminGUI(dbc);
                break;
            case "FRANCHISEE":
                new FranchiseeGUI(dbc);
                break;
            case "RECEPTIONIST":
                new ReceptionistGUI(dbc);
                break;
            case "MECHANIC":
                new MechanicGUI(dbc, staffID);
                break;
            default:
                System.out.println("GUI Error setting up");
                
        }
    }
    
    /**
     * Simple get method that is used to dispose the loginGUI if the user has logged in
     * successfully.
     * @return boolean
     */
    public boolean getLoginStatus() {
        return loginStatus;
    }

    
    
}
