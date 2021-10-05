package com.example.spike_exercise.ui.maintenance;

public class Request {
    public int userID,tenantID;
    public String request;
    public Request(int userID,int tenantID,String request){
        this.userID = userID;
        this.tenantID = tenantID;
        this.request = request;
    }
    public Request(){

    }
    public int getTenantID() {
        return tenantID;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
