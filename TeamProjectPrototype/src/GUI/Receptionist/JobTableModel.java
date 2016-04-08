/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Receptionist;

import Entities.Job;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class JobTableModel extends AbstractTableModel {
    private ArrayList<Job> data;
    private String colNames[] = { "Job ID", "Registration Number", "Job Status", "Business Type", "Duration", "Created on"};
    private Class<?> colClasses[] = { Integer.class, Integer.class, String.class, String.class, String.class, Date.class };

    public JobTableModel(ArrayList<Job> data) {
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
            return data.get(rowIndex).getJobID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getRegNo();
        }
        if(columnIndex == 2){
            return data.get(rowIndex).getJobStatus();
        }
        if(columnIndex == 3){
            return data.get(rowIndex).getBussinessType();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getDuration().toString();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getCreated();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
}
