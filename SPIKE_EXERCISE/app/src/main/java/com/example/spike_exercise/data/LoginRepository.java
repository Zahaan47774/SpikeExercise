package com.example.spike_exercise.data;

import androidx.annotation.NonNull;

import com.example.spike_exercise.data.model.LoggedInUser;
import com.example.spike_exercise.data.model.UserAccount;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore firestore;
    private UserAccount currentUser;

    // private constructor : singleton access
    private LoginRepository() {
        currentUser = null;
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
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

    public void login(String email, String password, AuthListener authListener) {
        Task<AuthResult> authTask = firebaseAuth.signInWithEmailAndPassword(email, password);
        Task<DocumentSnapshot> userDataTask = authTask.continueWithTask(new Continuation<AuthResult, Task<DocumentSnapshot>>() {
            @Override
            public Task<DocumentSnapshot> then(@NonNull Task<AuthResult> task) throws Exception {
                if(task.isSuccessful() && task.getResult() != null) {
                    return firestore.collection("users").document(task.getResult().getUser().getUid()).get();
                } else {
                    authListener.onFailure(task.getException());
                }
                return null;
            }
        });

        userDataTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                AuthResult authResult = authTask.getResult();
                DocumentSnapshot userData = task.getResult();
                if(task.isSuccessful() && userData != null && authResult != null) {
                    currentUser = parseUserFromFirebase(authResult, userData);
                    if(authListener != null) authListener.onSuccess(currentUser);
                } else {
                    if(authListener != null) authListener.onFailure(task.getException());
                }
            }
        });
    }

    private static UserAccount parseUserFromFirebase(AuthResult authResult, DocumentSnapshot userData) throws InvalidParameterException {
        FirebaseUser user = authResult.getUser();
        if(user == null || user.getMetadata() == null) throw new InvalidParameterException("Invalid AuthResult for user");
        String uid = user.getUid();
        String email = user.getEmail();
        long creationTimestamp = user.getMetadata().getCreationTimestamp();
        long lastSignInTimestamp = user.getMetadata().getLastSignInTimestamp();
        Integer accountTypeFirestore = userData.get("accountType", Integer.class);
        String companyName = userData.get("companyName", String.class);
        String firstName = userData.get("firstName", String.class);
        String lastName = userData.get("lastName", String.class);
        if(uid == null || email == null) throw new InvalidParameterException("Invalid AuthResult for user");
        if(accountTypeFirestore == null || companyName == null || firstName == null || lastName == null) throw new InvalidParameterException("Invalid DocumentSnapshot for user");
        AccountType accountType = AccountType.fromFirestore(accountTypeFirestore);
        return UserAccount.build(uid, email, creationTimestamp, lastSignInTimestamp, companyName, firstName, lastName, accountType);
    }

    public interface AuthListener {
        void onSuccess(UserAccount user);
        void onFailure(Exception e);
    }
}