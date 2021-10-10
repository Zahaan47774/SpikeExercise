package com.example.spike_exercise.data;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.spike_exercise.data.model.LandlordAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LandlordRepository implements EventListener<QuerySnapshot> {

    private static volatile LandlordRepository instance;

    private final Map<String, LandlordAccount>           landlordsMap;
    private final MutableLiveData<List<LandlordAccount>> landlordsList;

    private LandlordRepository() {
        landlordsMap = new HashMap<>();
        landlordsList = new MutableLiveData<>();
        Query query = FirebaseFirestore.getInstance().collection(DatabaseKeys.DOC_USERS)
                .whereEqualTo(DatabaseKeys.FIELD_USERS_ACCOUNT_TYPE, AccountType.LANDLORD.ordinal())
                .orderBy(DatabaseKeys.FIELD_USERS_PROP_MGR);
        query.addSnapshotListener(this);
        query.get();
    }


    public static void init() {
        getInstance();
    }

    public static synchronized LandlordRepository getInstance() {
        if (instance == null) {
            instance = new LandlordRepository();
        }
        return instance;
    }

    public LandlordAccount getLandlordByProperty(String propertyManager) {
        return landlordsMap.get(propertyManager);
    }

    public List<LandlordAccount> getLandlords() {
        return landlordsList.getValue();
    }

    public void observeLandlords(LifecycleOwner lifecycleOwner, Observer<List<LandlordAccount>> observer) {
        landlordsList.observe(lifecycleOwner, observer);
    }

    public void removeLandlordsObserver(Observer<List<LandlordAccount>> observer) {
        landlordsList.removeObserver(observer);
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
        if(value != null) {
            Log.i("LandlordRepository", "onUpdate");
            List<LandlordAccount> landlordAccounts = new ArrayList<>();
            landlordsMap.clear();
            for(DocumentSnapshot landlordDoc : value.getDocuments()) {
                String propertyMgr = landlordDoc.get(DatabaseKeys.FIELD_USERS_PROP_MGR, String.class);
                LandlordAccount landlordAccount = new LandlordAccount(
                        landlordDoc.getId(),
                        propertyMgr,
                        landlordDoc.get(DatabaseKeys.FIELD_USERS_FIRST_NAME, String.class),
                        landlordDoc.get(DatabaseKeys.FIELD_USERS_LAST_NAME, String.class)
                );
                landlordAccounts.add(landlordAccount);
                landlordsMap.put(propertyMgr, landlordAccount);
            }
            landlordsList.postValue(Collections.unmodifiableList(landlordAccounts));
        } else if(error != null) {
            Log.i("LandlordRepository", "onUpdate error: "+error.getMessage());
        } else {
            Log.i("LandlordRepository", "onUpdate: an unknown error has occurred");
        }
    }
}
