package com.example.spike_exercise.ui.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.spike_exercise.MainActivity;
import com.example.spike_exercise.R;
import com.example.spike_exercise.databinding.LandlordMaintenanceFragmentBinding;
import com.example.spike_exercise.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LandlordMaintenanceFragment extends Fragment implements OnCompleteListener<DocumentReference> {

    private LandlordMaintenanceViewModel LandlordMaintenanceViewModel;
    private LandlordMaintenanceFragmentBinding binding;

    // buttons and text fields
    private TextView textview1, textView2;
    private String userID;
    private Button button1, button2;

    // firebase stuff
    FirebaseFirestore db;
    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LandlordMaintenanceViewModel =
                new ViewModelProvider(this).get(LandlordMaintenanceViewModel.class);

        binding = LandlordMaintenanceFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        button1 = binding.buttonDisplayRequest;
        button2 = binding.buttonSendMessage;
        textview1 = binding.textViewDisplayUser;
        textView2 = binding.textViewRequest;

        db = FirebaseFirestore.getInstance();
        // retrieves collection of requests
        ArrayList<Request> list = new ArrayList<>();


        // displays new request
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // need to retrieve data from database
                // display user id within textview1
                // display maintenance request within textview2
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // need to retrieve data from database
                // display user id
                // display maintenance request
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigateToMaintenanceActivity() {
        Intent intent = new Intent(getContext(), LandlordMaintenanceFragment.class);
        startActivity(intent);
    }

    @Override
    public void onComplete(@NonNull Task<DocumentReference> task) {
        //loginViewModel.setBusyStatus(false);
        if(task.isSuccessful()) {
            System.out.println("Success");
        } else {
            String errorMessage = task.getException().getMessage().toLowerCase();
        }
    }
}