package com.example.spike_exercise.ui.maintenance;

public class Request {

    public String userID,tenantID;
    public String request;
    public String landlordResponse;

    public Request(String userID,String tenantID,String request){
        this.userID = userID;
        this.tenantID = tenantID;
        this.request = request;
        this.landlordResponse = "";
    }
    public Request(){ }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLandlordResponse() { return landlordResponse; }

    public void setLandlordResponse(String landlordResponse) { this.landlordResponse = landlordResponse; }
}
