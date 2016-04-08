/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.Receptionist;

import Entities.StockItem;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Wolf
 */
public class StockTableModel extends AbstractTableModel {
    private ArrayList<StockItem> data;
    //String code, String name, String manufacturer, String type, String year, double price, double costItem, int stockLvl, double stockCost, int threshold, int initalStock, double initalCost) {

    private String colNames[] = { "Code", "Part Name", "Manufacturer", "Vehicle Type", "Year", "Price", "Cost per Item", "Stock Level", "Stock Cost", "Threshold", "Inital Stock", "Inital Cost"};
    private Class<?> colClasses[] = { String.class, String.class, String.class, String.class, String.class, Double.class, Double.class, Integer.class, Double.class, Integer.class, Integer.class, Double.class };

    public StockTableModel(ArrayList<StockItem> data) {
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
        // "Code", "Part Name", "Manufacturer", "Vehicle Type", "Year", "Price", "Cost per Item", "Stock Level", "Stock Cost", "Threshold", "Inital Stock", "Inital Cost"};


        if (columnIndex == 0) {
            return data.get(rowIndex).getCode();
        }
        if (columnIndex == 1) {
            return data.get(rowIndex).getName();
        }
        if (columnIndex == 2){
            return data.get(rowIndex).getManufacturer();
        }
        if(columnIndex == 3){
            return data.get(rowIndex).getType();
        }
        if(columnIndex == 4){
            return data.get(rowIndex).getYear();
        }
        if(columnIndex == 5){
            return data.get(rowIndex).getPrice();
        }
        if(columnIndex == 6){
            return data.get(rowIndex).getCostItem();
        }
        if(columnIndex == 7){
            return data.get(rowIndex).getStockLvl();
        }
        if(columnIndex == 8){
            return data.get(rowIndex).getStockCost();
        }
        if(columnIndex == 9){
            return data.get(rowIndex).getThreshold();
        }
        if(columnIndex == 10){
            return data.get(rowIndex).getInitalStock();
        }
        if(columnIndex == 11){
            return data.get(rowIndex).getInitalCost();
        }
        return null;
    }
    
    public void refresh(ArrayList data){
        this.data = data;
        fireTableDataChanged();
    }
}
