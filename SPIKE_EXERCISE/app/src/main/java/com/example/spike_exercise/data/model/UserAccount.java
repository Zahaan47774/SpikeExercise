package com.example.spike_exercise.data.model;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.spike_exercise.data.AccountType;

public class UserAccount extends Account {

    private final String emailAddress;
    private final long creationTimestamp;
    private final long lastSignInTimestamp;

    public UserAccount(String uid, String emailAddress, long creationTimestamp, long lastSignInTimestamp, String propertyManager, String landlordID, String firstName, String lastName, AccountType accountType) {
        super(uid, propertyManager, landlordID, firstName, lastName, accountType);
        this.emailAddress = emailAddress;
        this.creationTimestamp = creationTimestamp;
        this.lastSignInTimestamp = lastSignInTimestamp;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public long getLastSignInTimestamp() {
        return lastSignInTimestamp;
    }
}
