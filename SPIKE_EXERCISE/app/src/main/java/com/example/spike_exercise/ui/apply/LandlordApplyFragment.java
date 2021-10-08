package com.example.spike_exercise.ui.apply;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.spike_exercise.R;
import com.example.spike_exercise.databinding.FragmentLandlordApplyBinding;
import com.example.spike_exercise.databinding.FragmentTenantApplyBinding;
import com.example.spike_exercise.ui.login.LoginViewModel;
import com.example.spike_exercise.ui.login.LoginViewModelFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LandlordApplyFragment extends Fragment {

    private LandlordApplyViewModel mViewModel;
    private LoginViewModel       loginViewModel;
    private FragmentLandlordApplyBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String UserID = "";
    public static LandlordApplyFragment newInstance() {
        return new LandlordApplyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        mViewModel = new ViewModelProvider(this).get(LandlordApplyViewModel.class);
        View root = binding.getRoot();
        TextView applyName = binding.LandlordApplicationName;
        TextView addressName = binding.LandlordAddress;
        Button submit = binding.AcceptApplication;
        Button deny = binding.DenyButton;
        Button next = binding.NextApplicant;

        db = FirebaseFirestore.getInstance();
        ArrayList<Application> list = new ArrayList<>();
        db.collection("application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new Application((String) document.get("userID"),(String) document.get("name"),
                                (String) document.get("applyAddress"), (String) document.get("company")));
                    }
                } else {
                    System.out.println("Error");
                }
            }
        });
        if (list.isEmpty()) {
            addressName.setText("");
            UserID = "";
            applyName.setText("No Applicants");
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("application").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("userID").equals(UserID)) {
                                    //REmove
                                    //Task<DocumentReference> signupTask = db.collection("application").child("name")
                                }
                            }
                        } else {
                            System.out.println("Error");
                        }
                    }
                });
            }
        });
        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    applyName.setText(list.get(finalI).getName());
                    UserID = list.get(finalI).getUserID();
                    addressName.setText(list.get(finalI).getApplyAddress());
                }
            });
            if (i == list.size() - 1) {
                i = 0;
            }
        }
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LandlordApplyViewModel.class);
        // TODO: Use the ViewModel
    }

}