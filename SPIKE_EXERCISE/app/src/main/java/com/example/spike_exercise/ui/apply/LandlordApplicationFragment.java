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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.Objects;


public class LandlordApplicationFragment extends Fragment {
    FirebaseFirestore db;
    String tenetId = "";
    String landLordCompany;
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
        db.collection("application").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    list.add(new Application((String) document.get("userID"),(String) document.get("name"),
                            (String) document.get("applyAddress"), (String) document.get("company")));
                }
            } else {
                System.out.println("Error");
            }
        });
        if (list.isEmpty()) {
            addressName.setText("");
            tenetId = "";
            applyName.setText("No Applicants");
        }
        
        accept.setOnClickListener(view -> db.collection("application").whereEqualTo("company", landLordCompany).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    DocumentReference userRef = db.collection("users").document(tenetId);
                    userRef.update("landLordID",landLordCompany);
                    DocumentReference docRef = db.collection("application").document(document.getId());
                    docRef.delete();
                }
            } else {
                System.out.println("Error");
            }
        }));
        deny.setOnClickListener(view -> db.collection("application").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    DocumentReference docRef = db.collection("application").document(document.getId());
                    docRef.delete();
                }
            } else {
                System.out.println("Error");
            }
        }));

        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
                next.setOnClickListener(view -> {
                    applyName.setText(list.get(finalI).getName());
                    tenetId = list.get(finalI).getUserID();
                    addressName.setText(list.get(finalI).getApplyAddress());
            });
            if (i == list.size() - 1) {
                i = 0;
            }
        }
        return root;
    }
}