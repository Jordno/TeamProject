/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Entities.Staff;
import java.sql.Date;
import java.util.ArrayList;
//import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class StaffTableModel extends AbstractTableModel {
    private ArrayList<Staff> data;
    private String colNames[] = { "ID", "Username", "Type", "Surname", "Name", "Date creation", "Labour cost"};
    private Class<?> colClasses[] = { Integer.class, String.class, String.class, String.class, String.class, Date.class, Double.class };

    public StaffTableModel(ArrayList data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }
    
    @Override
    public String getColumnName(int columnIndex) {
        return colNames[columnIndex];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return colClasses[columnIndex];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return data.get(rowIndex).getID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getUsername();
        }
        if (columnIndex == 2) {
            return data.get(rowIndex).getType();
        }
        if (columnIndex == 3){
            return data.get(rowIndex).getSurname();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getName();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getDate();
        }
        if(columnIndex == 6){
            return data.get(rowIndex).getLabourCost();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
    
}
