package com.example.spike_exercise.ui.login;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.Result;
import com.example.spike_exercise.data.model.LoggedInUser;
import com.example.spike_exercise.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;
    private final MutableLiveData<Boolean> busyStatus;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        this.busyStatus = new MutableLiveData<>();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password, LoginRepository.AuthListener authListener) {
        LoginRepository.getInstance().login(email, password, authListener);
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