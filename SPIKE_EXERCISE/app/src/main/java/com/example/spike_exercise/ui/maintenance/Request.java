package com.example.spike_exercise.ui.maintenance;

public class Request {
    public String userID,tenantID, requestID;
    public String request,response;
    public boolean priority;
    public Request(String userID,String tenantID,String request,boolean priority, String requestID){
        this.userID = userID;
        this.tenantID = tenantID; // landlordID
        this.requestID = requestID;
        this.request = request;
        this.response = "";
        this.priority = priority;
    }
    public Request(String userID,String tenantID,String request,boolean priority){
        this.userID = userID;
        this.tenantID = tenantID; // landlordID
        this.request = request;
        this.response = "";
        this.priority = priority;
    }
    public Request(){

    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

    public boolean isPriority() {
        return priority;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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
