package com.example.spike_exercise.ui.maintenance;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.spike_exercise.databinding.FragmentLandlordMaintenanceBinding;
import com.example.spike_exercise.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LandlordMaintenanceFragment extends Fragment implements OnCompleteListener<DocumentReference> {

    private LandlordMaintenanceViewModel LandlordMaintenanceViewModel;
    private FragmentLandlordMaintenanceBinding binding;

    // buttons and text fields
    private TextView textView1, textView2;
    private EditText editText1;
    private String userID;
    private Button button1, button2;

    // firebase stuff
    FirebaseFirestore db;
    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LandlordMaintenanceViewModel =
                new ViewModelProvider(this).get(LandlordMaintenanceViewModel.class);

        binding = FragmentLandlordMaintenanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        button1 = binding.buttonDisplayRequest;
        button2 = binding.buttonSendMessage;
        textView1 = binding.textViewDisplayUser;
        textView2 = binding.textViewRequest;
        editText1 = binding.editTextSendMessage;

        db = FirebaseFirestore.getInstance();
        // retrieves collection of requests
        ArrayList<Request> list = new ArrayList<>();
        // collects requests in list
        db.collection("maintananence").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new Request((String) document.get("tenantID"),(String) document.get("userID"),(String) document.get("request")));
                    }
                } else {
                    System.out.println("Error");
                }
            }
        });

        //check if list is empty
        if (list.isEmpty()) {
            textView1.setText("");
            textView2.setText("No Maintenance requests");
        }

        for (int i = 0; i < list.size(); i++) {
            // displays new request and user info
            int finalI = i; // necessary for inner class to access variable
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // display user id within textview1
                    textView1.setText(list.get(finalI).getTenantID());
                    // display maintenance request within textview2
                    textView2.setText(list.get(finalI).getRequest());
                }
            });
            // sends message to user
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // update requests with landlord response
                    list.get(finalI).setResponse(editText1.getText().toString());
                }
            });
            if (i == list.size() - 1) {
                i = 0;
            }
        }
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