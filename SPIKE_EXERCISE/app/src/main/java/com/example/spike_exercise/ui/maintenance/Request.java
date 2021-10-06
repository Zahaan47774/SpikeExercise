package com.example.spike_exercise.ui.maintenance;

public class Request {
    public String userID,tenantID;
    public String request,response;
    public Request(String userID,String tenantID,String request){
        this.userID = userID;
        this.tenantID = tenantID;
        this.request = request;
        this.response = "";
    }
    public Request(){

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
