/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Entities.Task;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class TaskListTableModel extends AbstractTableModel{
    private ArrayList<Task> data;
    private String colNames[] = { "List ID", "Description", "Duration"};
    private Class<?> colClasses[] = { Integer.class, String.class, String.class };

    public TaskListTableModel(ArrayList data) {
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
            return data.get(rowIndex).getListID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getDesc();
        }
        if (columnIndex == 2) {
            return data.get(rowIndex).getDuration().toString();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
}
