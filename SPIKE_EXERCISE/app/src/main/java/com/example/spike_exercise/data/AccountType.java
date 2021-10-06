package com.example.spike_exercise.data;

public enum AccountType {
    TENANT, LANDLORD;

    private static AccountType[] cachedValues = null;

    public static AccountType fromFirestore(int firestoreValue) {
        if(cachedValues == null) cachedValues = values(); // cache the values array because values() constructs a new array each time which is wasteful
        return cachedValues[firestoreValue];
    }
}
