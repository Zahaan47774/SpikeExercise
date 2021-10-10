package com.example.spike_exercise.data.model;

import com.example.spike_exercise.data.AccountType;

public class TenantAccount extends Account {

    public TenantAccount(String uid, String propertyManager, String firstName, String lastName) {
        super(uid, propertyManager, firstName, lastName, AccountType.TENANT);
    }
}