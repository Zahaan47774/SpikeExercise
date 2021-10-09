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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private TextView textView1, textView2, textView10, textView11, textView6, textView12;
    private EditText editText1;
    private String userID;
    private Button button1, button2;
    private int index; // keeps track of what request is being shown
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
        textView6 = binding.textViewPriority;
        textView10 = binding.textView10; // "Tenant ID: "
        textView11 = binding.textView11; // "Priority: "
        textView12 = binding.textView12; // "Request: "
        editText1 = binding.editTextSendMessage;
        db = FirebaseFirestore.getInstance();

        // collection of requests
        ArrayList<Request> list = new ArrayList<>();

        /*
        db.collection("mainanantence").get().then(function(querySnapshot)) {
            querySnapshot.forEach(function(doc)) {
                list.add(new Request((String) document.get("tenantID"),(String) document.get("userID"),(String) document.get("request")));
            });
        });
        */
        index = 0;
        db.collection("maintananence").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new Request((String) document.get("tenantID"),(String) document.get("userID"),(String) document.get("request")));
                    }
                    //check if list is empty
                    if (list.isEmpty()) {
                        textView1.setText("");
                        textView2.setText("No maintenance requests");
                    } else {
                        //textView1.setText("");
                        textView2.setText("Click above to display requests");
                    }
                    // displays new request on click
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // display user id within textview1
                            if (index == list.size()) {
                                index = 0;
                            }
                            textView1.setText(list.get(index).getTenantID());
                            // display maintenance request within textview2
                            textView2.setText(list.get(index).getRequest());
                            if (list.get(index).isPriority()) {
                                textView6.setText("High");
                            } else {
                                textView6.setText("Low");
                            }
                            index++;
                        }
                    });
                    // sends response message on click
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // update requests with landlord response
                            list.get(index).setResponse(editText1.getText().toString());
                            save(view);
                        }

                        private void save(View view) {
                            db.collection("maintananence").document(list.get(index).tenantID).update("response", editText1.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("Suceess");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            });
                        }
                    });
                } else {
                    System.out.println("Error");
                }
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