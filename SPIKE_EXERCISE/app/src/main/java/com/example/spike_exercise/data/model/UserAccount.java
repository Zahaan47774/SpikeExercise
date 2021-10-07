package com.example.spike_exercise.data.model;

import androidx.annotation.NonNull;

import com.example.spike_exercise.data.AccountType;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;

public class UserAccount {

    private final String uid;
    private final String emailAddress;
    private final long creationTimestamp;
    private final long lastSignInTimestamp;
    private final String companyName;
    private final String firstName;
    private final String lastName;
    private final AccountType accountType;

    private UserAccount(String uid, String emailAddress, long creationTimestamp, long lastSignInTimestamp, String companyName, String firstName, String lastName, AccountType accountType) {
        this.uid = uid;
        this.emailAddress = emailAddress;
        this.creationTimestamp = creationTimestamp;
        this.lastSignInTimestamp = lastSignInTimestamp;
        this.companyName = companyName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountType = accountType;
    }

    public String getUid() {
        return uid;
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

    public String getCompanyName() {
        return companyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    @NonNull
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserAccount{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", emailAddress='").append(emailAddress).append('\'');
        sb.append(", creationTimestamp=").append(creationTimestamp);
        sb.append(", lastSignInTimestamp=").append(lastSignInTimestamp);
        sb.append(", companyName='").append(companyName).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", accountType=").append(accountType);
        sb.append('}');
        return sb.toString();
    }

    public static UserAccount build(String uid, String emailAddress, long creationTimestamp, long lastSignInTimestamp, String companyName, String firstName, String lastName, AccountType accountType) {
        return new UserAccount(uid, emailAddress, creationTimestamp, lastSignInTimestamp, companyName, firstName, lastName, accountType);
    }
}
