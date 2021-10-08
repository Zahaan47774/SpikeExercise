package com.example.spike_exercise.ui.payment;

public class PaymentModel {
    public  String name,userID,tenantID,paymentID;
    public int amount;
    public PaymentModel(int amount,String name,String tenantID,String userID,String paymentID){
        this.amount=amount;
        this.name=name;
        this.tenantID=tenantID;
        this.userID=userID;
        this.paymentID = paymentID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String toString() {
        return name;
    }
}
