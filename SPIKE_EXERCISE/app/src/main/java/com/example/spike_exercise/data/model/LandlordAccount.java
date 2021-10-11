package com.example.spike_exercise.data.model;

import androidx.annotation.NonNull;

import com.example.spike_exercise.data.AccountType;

public class LandlordAccount extends Account {

    public LandlordAccount(String uid, String propertyManager, String firstName, String lastName) {
        super(uid, propertyManager, null, firstName, lastName, AccountType.LANDLORD);
    }

    @NonNull
    @Override
    public String toString() {
        return getPropertyManager();
    }
}
