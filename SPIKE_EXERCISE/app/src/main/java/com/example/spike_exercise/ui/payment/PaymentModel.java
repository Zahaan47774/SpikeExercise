package com.example.spike_exercise.ui.payment;

public class PaymentModel {
    public  String paymentID,name;
    int balance;
    public PaymentModel(int balance,String paymentID,String name){
        this.balance = balance;
        this.paymentID = paymentID;
        this.name = name;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String toString() {
        return name;
    }
}
