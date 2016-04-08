/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.sql.Date;

/**
 *
 * @author Wolf
 */
public class StockDelivery {
    String code;
    int quantity, deliveryID;
    boolean recieved;
    double stockCost;
    Date ordered;

    public StockDelivery(int deliveryID, String code, int qunatity, boolean recieved, double stockCost, Date ordered) {
        this.deliveryID = deliveryID;
        this.code = code;
        this.quantity = qunatity;
        this.recieved = recieved;
        this.stockCost = stockCost;
        this.ordered = ordered;
    }

    
    public int getDeliveryID() {
        return deliveryID;
    }

    public void setDeliveryID(int deliveryID) {
        this.deliveryID = deliveryID;
    }
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int qunatity) {
        this.quantity = qunatity;
    }

    public boolean isRecieved() {
        return recieved;
    }

    public void setRecieved(boolean recieved) {
        this.recieved = recieved;
    }

    public double getStockCost() {
        return stockCost;
    }

    public void setStockCost(double stockCost) {
        this.stockCost = stockCost;
    }

    public Date getOrdered() {
        return ordered;
    }

    public void setOrdered(Date ordered) {
        this.ordered = ordered;
    }
    
    
}
