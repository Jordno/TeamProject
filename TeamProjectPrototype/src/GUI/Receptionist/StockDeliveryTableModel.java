/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Receptionist;


import Entities.StockDelivery;
import java.sql.Date;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class StockDeliveryTableModel extends AbstractTableModel {
    private ArrayList<StockDelivery> data;
    private String colNames[] = { "Delivery ID","Code", "Quantity", "Recieved", "Delivery Cost", "Date Ordered"};
    private Class<?> colClasses[] = { Integer.class, String.class, Integer.class, Boolean.class, Double.class, Date.class};

    public StockDeliveryTableModel(ArrayList<StockDelivery> data) {
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
        //    private String colNames[] = { "Code", "Quantity", "Recieved", "Stock_Cost", "Date Ordered"};

        if(columnIndex == 0){
            return data.get(rowIndex).getDeliveryID();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getCode();
        }
        if (columnIndex == 2) {
            return data.get(rowIndex).getQuantity();
        }
        if (columnIndex == 3){
            return data.get(rowIndex).isRecieved();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getStockCost();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getOrdered();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
    
}
