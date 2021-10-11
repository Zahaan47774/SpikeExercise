package com.example.spike_exercise.ui.login;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.spike_exercise.data.AuthListener;
import com.example.spike_exercise.data.LoginRepository;

public class LoginViewModel extends ViewModel {

    private final LoginRepository          loginRepository;
    private final MutableLiveData<Boolean> busyStatus;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.busyStatus = new MutableLiveData<>();
    }

    public void login(String email, String password, AuthListener authListener) {
        loginRepository.login(email, password, authListener);
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
        return passwordText != null && !passwordText.isEmpty();
    }
}