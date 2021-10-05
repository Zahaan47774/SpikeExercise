package com.example.spike_exercise.data.model;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class UserAccount {

    private final FirebaseUser firebaseUser;

    public UserAccount(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public String getEmailAddress() {
        return firebaseUser.getEmail();
    }

    public static UserAccount fromFirebaseAuth(AuthResult authResult) {
        return new UserAccount(authResult.getUser());
    }
}
