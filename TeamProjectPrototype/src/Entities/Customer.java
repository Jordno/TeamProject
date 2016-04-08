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
public class Customer {
    private int cusID;
    private String name, telephone, mobile, email, address, street, locality, city, postcode, notes, type;

    public Customer(int cusID, String fname, String telephone, String mobile, String email, String address, String street, String locality, String city, String postcode, String notes, String type) {
        this.cusID = cusID;
        this.name = fname;
        this.telephone = telephone;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.street = street;
        this.locality = locality;
        this.city = city;
        this.postcode = postcode;
        this.notes = notes;
        this.type = type;
    }




    public int getCusID() {
        return cusID;
    }

    public String getFname() {
        return name;
    }

    public void setFname(String fname) {
        this.name = fname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
   
    

}
