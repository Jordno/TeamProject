/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wolf
 */
public abstract class ActorController {
    //private byte[] checkSum; //used to check if data is most recent
    //protected String checkSumTableName;

    /**
     * This function is used as apart of concurrency system. It will generate a checksum
     * value with the information in the database and store it. This can be used to do a check
     * against the most recent checksum to see if the table has been altered.
     */
    public byte[] setChecksum(Connection conn, String checkSumTableName){
        byte[] checkSum = null;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("CHECKSUM TABLE "+checkSumTableName+";");
            while(rs.next()){
                checkSum = rs.getBytes("Checksum");
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ActorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return checkSum;
    }
    
    /**
     * This function is part of the concurrency system. It will return the most recent checksum value,
     * this is then checked against the stored value to see if there has been change in the data
     * @return byte[] - New checksum value
     */
    public boolean getChecksum(Connection conn, String checkSumTableName, byte[] checkSum){
        byte[] newCheckSum = checkSum;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("CHECKSUM TABLE "+checkSumTableName+";");
            while(rs.next()){
                newCheckSum = rs.getBytes("Checksum");
            }
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(ActorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return newCheckSum;
        return Arrays.equals(checkSum,newCheckSum);
    }
}
