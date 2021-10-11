package com.example.spike_exercise.data;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.spike_exercise.data.model.LandlordAccount;
import com.example.spike_exercise.data.model.UserAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository implements EventListener<DocumentSnapshot> {

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private       UserAccount            currentUser;
    private final MutableLiveData<Float> balance;

    // private constructor : singleton access
    private LoginRepository() {
        currentUser = null;
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        this.balance = new MutableLiveData<>(0.0f);
    }

    public static synchronized LoginRepository getInstance() {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public UserAccount getCurrentUser() {
        return currentUser;
    }

    public float getCurrentUserBalance() {
        return balance.getValue();
    }

    public void observeCurrentUserBalance(LifecycleOwner lifecycleOwner, Observer<Float> observer) {
        balance.observe(lifecycleOwner, observer);
    }

    public void removeCurrentUserBalanceObserver(Observer<Float> observer) {
        balance.removeObserver(observer);
    }

    public void login(String email, String password, AuthListener authListener) {
        Task<AuthResult> authTask = firebaseAuth.signInWithEmailAndPassword(email, password);
        Task<DocumentSnapshot> userDataTask = authTask.continueWithTask(task -> {
            if(task.isSuccessful() && task.getResult() != null) {
                DocumentReference userDataDoc = firestore.collection("users").document(task.getResult().getUser().getUid());
                userDataDoc.addSnapshotListener(LoginRepository.this);
                return userDataDoc.get();
            }
            return null;
        });

        userDataTask.addOnCompleteListener(task -> {
            if(!authTask.isSuccessful()) {
                authListener.onFailure(authTask.getException());
                return;
            } else if(!userDataTask.isSuccessful()) {
                authListener.onFailure(userDataTask.getException());
                return;
            }
            AuthResult authResult = authTask.getResult();
            DocumentSnapshot userData = task.getResult();
            if(userData != null && authResult != null) {
                try {
                    currentUser = parseUserFromFirebase(authResult, userData);
                    Float balance = userData.get(DatabaseKeys.FIELD_USERS_BALANCE, Float.class);
                    if(balance == null) throw new IllegalStateException("User account balance invalid!");
                    this.balance.postValue(balance);
                    if(authListener != null) authListener.onSuccess(currentUser);
                } catch (Exception e) {
                    if(authListener != null) authListener.onFailure(e);
                }

            } else {
                if(authListener != null) authListener.onFailure(new IllegalStateException("Failed to retrieve user data! Please try again later."));
            }
        });
    }

    private static UserAccount parseUserFromFirebase(AuthResult authResult, DocumentSnapshot userData) throws InvalidParameterException {
        FirebaseUser user = authResult.getUser();
        if(user == null || user.getMetadata() == null) throw new InvalidParameterException("Invalid AuthResult for user");

        String  uid = user.getUid();
        String  email = user.getEmail();
        long    creationTimestamp = user.getMetadata().getCreationTimestamp();
        long    lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
        Integer accountTypeFirestore = userData.get("accountType", Integer.class);
        String  companyName = userData.get("companyName", String.class);
        String  landlordID = userData.get("landlordID", String.class);
        String  firstName = userData.get("firstName", String.class);
        String  lastName = userData.get("lastName", String.class);

        if(email == null) throw new InvalidParameterException("Invalid AuthResult for user");
        if(accountTypeFirestore == null || companyName == null || firstName == null || lastName == null) throw new InvalidParameterException("Invalid DocumentSnapshot for user");

        return new UserAccount(
                uid,
                email,
                creationTimestamp,
                lastSignInTimestamp,
                companyName,
                landlordID,
                firstName,
                lastName,
                AccountType.fromFirestore(accountTypeFirestore)
        );
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        if(value != null && currentUser != null) {
            Log.i("LoginRepository", "Update balance");
            Float balanceFirestore = value.get(DatabaseKeys.FIELD_USERS_BALANCE, Float.class);
            balance.postValue(balanceFirestore != null ? balanceFirestore : 0.0f); // no null balance
        } else if(error != null) {
            Log.i("LandlordRepository", "onUpdate error: "+error.getMessage());
        } else {
            Log.i("LandlordRepository", "onUpdate: an unknown error has occurred");
        }
    }
}