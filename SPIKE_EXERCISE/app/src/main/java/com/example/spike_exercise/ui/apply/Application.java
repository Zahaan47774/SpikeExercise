package com.example.spike_exercise.ui.apply;

public class Application
{
    public String userID;
    public String name;
    public String applyAddress;
    public String company;
    public String applicationID;
    public Application(String newID,String newName, String newAddress, String newCompany){
        this.userID = newID;
        this.name = newName;
        this.applyAddress = newAddress;
        this.company = newCompany;
    }
    public Application(String newID,String newName, String newAddress, String newCompany,String applicationID){
        this.userID = newID;
        this.name = newName;
        this.applyAddress = newAddress;
        this.company = newCompany;
        this.company = applicationID;
    }
    public void setName(String newName){
        this.name = newName;
    }
    public void setUserID(String newID){
        this.userID = newID;
    }
    public void setApplyAddress(String newAddress){
        this.applyAddress = newAddress;
    }
    public void setCompany(String newCompany) { this.company = newCompany;}
    public String getUserID(){
        return this.userID;
    }
    public String getName(){
        return this.name;
    }
    public String getApplyAddress(){
        return this.applyAddress;
    }
    public String getCompany() {return this.company;}
}
