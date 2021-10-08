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

import com.example.spike_exercise.databinding.FragmentLandlordApplicationBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import java.util.ArrayList;
import java.util.Objects;


public class LandlordApplicationFragment extends Fragment {
    FirebaseFirestore db;
    String UserID = "";
    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {

        com.example.spike_exercise.databinding.FragmentLandlordApplicationBinding binding = FragmentLandlordApplicationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView applyName = binding.LandlordApplicationName;
        TextView addressName = binding.LandlordAddress;
        Button submit = binding.AcceptApplication;
        Button next = binding.NextApplicant;
        Button deny = binding.DenyButton;
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
            UserID = "";
            applyName.setText("No Applicants");
        }
        submit.setOnClickListener(view -> db.collection("application").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    DocumentReference docref = db.collection("application").document(document.getId());
                    docref.delete();
                }
            } else {
                System.out.println("Error");
            }
        }));
        deny.setOnClickListener(view -> db.collection("application").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    DocumentReference docref = db.collection("application").document(document.getId());
                    docref.delete();
                }
            } else {
                System.out.println("Error");
            }
        }));
        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
            next.setOnClickListener(view -> {
                applyName.setText(list.get(finalI).getName());
                UserID = list.get(finalI).getUserID();
                addressName.setText(list.get(finalI).getApplyAddress());
            });
            if (i == list.size() - 1) {
                i = 0;
            }
        }
        return root;
    }
}