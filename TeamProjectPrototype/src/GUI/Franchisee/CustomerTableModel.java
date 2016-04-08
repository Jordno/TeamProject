/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Franchisee;

import Entities.Customer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class CustomerTableModel extends AbstractTableModel {
    private ArrayList<Customer> data;
    private String colNames[] = { "Customer ID", "Name", "Telephone", "Mobile", "Email", "Address", "Street", "Locality", "City", "Post code", "Notes", "Type" };
    private Class<?> colClasses[] = { Integer.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class };

    public CustomerTableModel(ArrayList data) {
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
    //private String colNames[] = { "Customer ID", "First Name", "Surname", "Telephone", "Mobile", "Email", "Address", "Street", "Locality", "City", "Post code", "Customer Type", "Notes" };

        if (columnIndex == 0) {
            return data.get(rowIndex).getCusID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getFname();
        }
        if (columnIndex == 2){
            return data.get(rowIndex).getTelephone();
        }
       if(columnIndex == 3){
            return data.get(rowIndex).getMobile();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getEmail();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getAddress();
        }
        if(columnIndex == 6){
            return data.get(rowIndex).getStreet();
        }
        if(columnIndex == 7){
            return data.get(rowIndex).getLocality();
        }
        if(columnIndex == 8){
            return data.get(rowIndex).getCity();
        }
        if(columnIndex == 9){
            return data.get(rowIndex).getPostcode();
        }
        if(columnIndex == 10){
            return data.get(rowIndex).getNotes();
        }
        if(columnIndex == 11){
            return data.get(rowIndex).getType();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
}
