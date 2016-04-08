/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author Wolf
 */
public class StockItem {
    private String code, name, manufacturer, type, year;
    private double price, costItem;
    private int stockLvl;
    private double stockCost;
    private int threshold, initalStock;
    private double initalCost;

    public StockItem(String code, String name, String manufacturer, String type, String year, double price, double costItem, int stockLvl, double stockCost, int threshold, int initalStock, double initalCost) {
        this.code = code;
        this.name = name;
        this.manufacturer = manufacturer;
        this.type = type;
        this.year = year;
        this.price = price;
        this.costItem = costItem;
        this.stockLvl = stockLvl;
        this.stockCost = stockCost;
        this.threshold = threshold;
        this.initalStock = initalStock;
        this.initalCost = initalCost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostItem() {
        return costItem;
    }

    public void setCostItem(double costItem) {
        this.costItem = costItem;
    }

    public int getStockLvl() {
        return stockLvl;
    }

    public void setStockLvl(int stockLvl) {
        this.stockLvl = stockLvl;
    }

    public double getStockCost() {
        return stockCost;
    }

    public void setStockCost(double stockCost) {
        this.stockCost = stockCost;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getInitalStock() {
        return initalStock;
    }

    public void setInitalStock(int initalStock) {
        this.initalStock = initalStock;
    }

    public double getInitalCost() {
        return initalCost;
    }

    public void setInitalCost(double initalCost) {
        this.initalCost = initalCost;
    }



    
    
}
