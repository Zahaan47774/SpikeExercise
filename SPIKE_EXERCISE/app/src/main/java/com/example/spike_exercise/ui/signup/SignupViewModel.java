package com.example.spike_exercise.ui.signup;

import android.os.Handler;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.spike_exercise.data.AccountType;
import com.example.spike_exercise.data.DatabaseKeys;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class SignupViewModel extends ViewModel implements OnCompleteListener<Void> {

    private FirebaseFirestore firestore;

    private final MutableLiveData<AccountType> selectedAccountType;
    private final MutableLiveData<Boolean> busyStatus;
    private final Map<String, Object> userData, userBalance;

    public SignupViewModel() {
        this.firestore = FirebaseFirestore.getInstance();
        this.selectedAccountType = new MutableLiveData<>(AccountType.TENANT);
        this.busyStatus = new MutableLiveData<>(false);
        this.userData = new HashMap<>();
        this.userBalance = new HashMap<>();
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

    public void createUser(String firstName, String lastName, String companyName, String emailAddress, String password, OnCompleteListener<Void> onCompleteListener) {
        busyStatus.setValue(true);
        if(selectedAccountType.getValue() == AccountType.LANDLORD && (companyName == null || companyName.isEmpty())) {
            companyName = String.format("%s %s", firstName, lastName);
        }
        userData.put(DatabaseKeys.FIELD_USERS_ACCOUNT_TYPE, selectedAccountType.getValue().ordinal());
        userData.put(DatabaseKeys.FIELD_USERS_FIRST_NAME  , firstName);
        userData.put(DatabaseKeys.FIELD_USERS_LAST_NAME   , lastName);
        userData.put(DatabaseKeys.FIELD_USERS_PROP_MGR    , companyName);
        userData.put(DatabaseKeys.FIELD_USERS_LANDLORD_ID , "");
        userData.put(DatabaseKeys.FIELD_USERS_BALANCE     , 0);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Task<AuthResult> authTask = firebaseAuth.createUserWithEmailAndPassword(emailAddress, password);
        Task<Void> signupTask = authTask.continueWithTask(task -> {
            if(task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                FirebaseUser newUser = task.getResult().getUser();
                WriteBatch batch = firestore.batch();

                DocumentReference newUserData = firestore.collection("users").document(newUser.getUid());
                //DocumentReference newUserBalance = firestore.collection("payments").document(newUser.getUid());

                batch.set(newUserData, userData);
                //batch.set(newUserBalance, userBalance);

                return batch.commit();
            }
            return null;
        });
        signupTask.addOnCompleteListener(this);
        signupTask.addOnCompleteListener(onCompleteListener);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        busyStatus.setValue(false);
    }
}