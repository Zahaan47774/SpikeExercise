package com.example.spike_exercise.ui.maintenance;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LandlordMaintenanceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LandlordMaintenanceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the maintenance requests fragment.");
    }


    public boolean validateRequiredField(String fieldText) {
        return fieldText != null && !fieldText.isEmpty();
    }

    public boolean validateEmailField(String emailText) {
        return emailText != null && Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
    }

    public boolean validatePasswordField(String passwordText) {
        return passwordText != null && !passwordText.isEmpty();
    }

    public LiveData<String> getText() {
        return mText;
    }
}