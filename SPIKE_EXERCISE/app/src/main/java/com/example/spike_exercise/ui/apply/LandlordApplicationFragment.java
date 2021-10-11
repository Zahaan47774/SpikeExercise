package com.example.spike_exercise.ui.apply;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentLandlordApplicationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


public class LandlordApplicationFragment extends Fragment {
    FirebaseFirestore db;
    String tenetId = "";
    String landLordCompany;
    int theindex = 0;
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {

        com.example.spike_exercise.databinding.FragmentLandlordApplicationBinding binding = FragmentLandlordApplicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView applyName = binding.LandlordApplicationName;
        TextView addressName = binding.LandlordAddress;
        Button accept = binding.AcceptApplication;
        Button next = binding.NextApplicant;
        Button deny = binding.DenyButton;

        landLordCompany = LoginRepository.getInstance().getCurrentUser().getUid();
        
        db = FirebaseFirestore.getInstance();
        ArrayList<Application> list = new ArrayList<>();

        db.collection("application").whereEqualTo("company",landLordCompany).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new Application((String) document.get("userID"), (String) document.get("name"),
                                (String) document.get("applyAddress"), (String) document.get("company"), document.getId()));
                    }
                    if (list.isEmpty()) {
                        addressName.setText("");
                        tenetId = "";
                        applyName.setText("No Applicants");
                    }
                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DocumentReference userRef = db.collection("users").document(tenetId);
                            userRef.update("landlordID", landLordCompany);
                            DocumentReference docRef = db.collection("application").document(list.get(theindex).applicationID);
                            docRef.delete();
                        }
                    });
                    deny.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DocumentReference docRef = db.collection("application").document(list.get(theindex).applicationID);
                            docRef.delete();
                        }
                    });
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            theindex++;
                            if (theindex == list.size()) {
                                theindex = 0;
                            }
                            applyName.setText(list.get(theindex).getName());
                            tenetId = list.get(theindex).getUserID();
                            addressName.setText(list.get(theindex).getApplyAddress());
                        }
                    });
                } else {
                    System.out.println("Error");
                }
            }
        });
        return root;
    }
}
