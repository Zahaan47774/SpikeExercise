package com.example.spike_exercise.ui.maintenance;

public class Request {
    public String userID,tenantID;
    public String request;
    public Request(String userID,String tenantID,String request){
        this.userID = userID;
        this.tenantID = tenantID;
        this.request = request;
    }
    public Request(){

    }
    public String getTenantID() {
        return tenantID;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
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
}
