package com.example.spike_exercise.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.spike_exercise.data.AccountType;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;

public abstract class Account {

    private final String uid;
    private final String propertyManager;
    private final String landlordID;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final AccountType accountType;

    protected Account(String uid, String propertyManager, String landlordID, String firstName, String lastName, AccountType accountType) {
        this.uid = uid;
        this.propertyManager = propertyManager;
        this.landlordID = landlordID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = String.format("%s %s", firstName, lastName);
        this.accountType = accountType;
    }

    public String getUid() {
        return uid;
    }

    public String getPropertyManager() {
        return propertyManager;
    }

    public String getLandlordID() {
        return landlordID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof Account)) return false;
        return ((Account) obj).uid.equals(this.uid);
    }
}
