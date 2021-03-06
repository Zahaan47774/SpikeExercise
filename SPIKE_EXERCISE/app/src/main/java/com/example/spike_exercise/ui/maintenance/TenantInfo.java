package com.example.spike_exercise.ui.maintenance;

public class TenantInfo {
    public String tenantID,companyName,requestID;

    public TenantInfo(String tenantID, String companyName, String requestID){
        this.tenantID = tenantID;
        this.requestID = requestID;
        this.companyName = companyName;
    }
    public TenantInfo(String tenantID, String companyName){
        this.tenantID = tenantID;
        this.companyName = companyName;
    }

    public String getRequestID() {
        return requestID;
    }

    public String getTenantID() {
        return tenantID;
    }

    public void setTenantID(String tenantID) {
        this.tenantID = tenantID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    @Override
    public String toString() {
        return companyName;
    }
}
