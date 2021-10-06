package com.example.spike_exercise.ui.apply;

public class Application
{
    public String userID;
    public String name;
    public String applyAddress;

    public Application(String newID,String newName, String newAddress){
        this.userID = newID;
        this.name = newName;
        this.applyAddress = newAddress;
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
    public String getUserID(){
        return this.userID;
    }
    public String getName(){
        return this.name;
    }
    public String getApplyAddress(){
        return this.applyAddress;
    }
}
