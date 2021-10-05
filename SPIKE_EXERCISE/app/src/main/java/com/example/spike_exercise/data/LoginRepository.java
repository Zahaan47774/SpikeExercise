package com.example.spike_exercise.data;

import androidx.annotation.NonNull;

import com.example.spike_exercise.data.model.LoggedInUser;
import com.example.spike_exercise.data.model.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private final FirebaseAuth firebaseAuth;
    private UserAccount currentUser;

    // private constructor : singleton access
    private LoginRepository() {
        currentUser = null;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository();
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
    }

    public void login(String email, String password, AuthListener authListener) {
        Task<AuthResult> authTask = firebaseAuth.signInWithEmailAndPassword(email, password);
        authTask.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                AuthResult authResult = task.getResult();
                if(authListener != null) {
                    if(task.isSuccessful() && authResult != null) {
                        UserAccount currentUser = UserAccount.fromFirebaseAuth(authResult);
                        LoginRepository.this.currentUser = currentUser;
                        authListener.onSuccess(currentUser);
                    } else {
                        authListener.onFailure(task.getException());
                    }
                }
            }
        });
    }

    public interface AuthListener {
        void onSuccess(UserAccount user);
        void onFailure(Exception e);
    }
}