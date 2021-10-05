package com.example.spike_exercise.ui.signup;

import android.util.Patterns;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.spike_exercise.data.AccountType;

public class SignupViewModel extends ViewModel {

    private final MutableLiveData<AccountType> selectedAccountType;
    private final MutableLiveData<Boolean> busyStatus;

    public SignupViewModel() {
        this.selectedAccountType = new MutableLiveData<>(AccountType.TENANT);
        this.busyStatus = new MutableLiveData<>(false);
    }

    public void setSelectedAccountType(AccountType accountType) {
        selectedAccountType.setValue(accountType);
    }

    public AccountType getSelectedAccountType() {
        return selectedAccountType.getValue();
    }

    public void observeSelectedAccountType(LifecycleOwner lifecycleOwner, Observer<? super AccountType> observer) {
        selectedAccountType.observe(lifecycleOwner, observer);
    }

    public void setBusyStatus(boolean isBusy) {
        busyStatus.setValue(isBusy);
    }

    public void observeBusyStatus(LifecycleOwner lifecycleOwner, Observer<? super Boolean> observer) {
        busyStatus.observe(lifecycleOwner, observer);
    }

    public boolean isBusy() {
        return busyStatus.getValue();
    }

    public boolean validateRequiredField(String fieldText) {
        return fieldText != null && !fieldText.isEmpty();
    }

    public boolean validateEmailField(String emailText) {
        return emailText != null && Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
    }

    public boolean validatePasswordField(String passwordText) {
        return passwordText != null && passwordText.length() >= 8;
    }
}