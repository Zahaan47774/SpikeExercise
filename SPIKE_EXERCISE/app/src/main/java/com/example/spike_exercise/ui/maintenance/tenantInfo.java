package com.example.spike_exercise.ui.maintenance;

public class tenantInfo {
    public String tenantID,companyName;
    public tenantInfo(String tenantID,String companyName){
        this.tenantID = tenantID;
        this.companyName = companyName;
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