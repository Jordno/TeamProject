/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Franchisee;

import Entities.Customer;
import Entities.Vehicle;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class VehicleTableModel extends AbstractTableModel {
    private ArrayList<Vehicle> data;
    private String colNames[] = { "Reg Number", "Engine Serial", "Chassis Number", "Colour", "Make", "Model", "Year", "Last MoT check"};
    private Class<?> colClasses[] = { String.class, Integer.class, Integer.class, String.class, String.class, String.class, Integer.class, java.sql.Date.class};

    public VehicleTableModel(ArrayList<Vehicle> data) {
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
            return data.get(rowIndex).getRegNo();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getEngSerial();
        }
        if (columnIndex == 2) {
            return data.get(rowIndex).getChassisNo();
        }
        if (columnIndex == 3){
            return data.get(rowIndex).getColour();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getMake();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getModel();
        }
        if(columnIndex == 6){
            return data.get(rowIndex).getYear();
        }
        if(columnIndex == 7){
            return data.get(rowIndex).getMotCheckDate();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
    
}
