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
import com.example.spike_exercise.data.LoginRepository;
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
    private boolean highList;
    private Request requestModel;

    FirebaseFirestore db;
    FirebaseAuth auth;
    Request maintenanceRequest;

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

        // sort into high and low priorities
        ArrayList<Request> highPriority = new ArrayList<>();
        ArrayList<Request> lowPriority = new ArrayList<>();

        index = 0;
        highList = false;

        db.collection("maintananence").whereEqualTo("tenantID", LoginRepository.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // if request is priority sort into high list
                        if ((boolean)document.get("priority")) {
                            highPriority.add(new Request((String) document.get("tenantID"),(String) document.get("userID"),(String) document.get("request"),(boolean) document.get("priority")));
                        } else { // else sort into low list
                            lowPriority.add(new Request((String) document.get("tenantID"),(String) document.get("userID"),(String) document.get("request"),(boolean) document.get("priority")));
                        }
                    }
                    //check if list is empty
                    if (highPriority.isEmpty() && lowPriority.isEmpty()) {
                        textView1.setText("");
                        textView2.setText("No maintenance requests");
                    } else {
                        //textView2.setText("Click above to display requests");
                        // }
                        // keeps track of what list, 0 if no high priorities
                        if (!highPriority.isEmpty()) {
                            highList = true;
                        }
                        // displays new request on click
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (highList) { // if on highList
                                    if (index == highPriority.size()) {
                                        if (!lowPriority.isEmpty()) {
                                            highList = false; // move to low list
                                            index = 0; // restart index
                                        } else {
                                            index = 0; // keep cycling through high list and keep highList high
                                        }
                                    }
                                    // display user id within textview1
                                    textView1.setText(highPriority.get(index).getTenantID());
                                    // display maintenance request within textview2
                                    textView2.setText(highPriority.get(index).getRequest());
                                    textView6.setText("High");
                                    index++;
                                } else { // on lowList
                                    if (index == lowPriority.size()) {
                                        if (!highPriority.isEmpty()) {
                                            highList = true; // move to low list
                                            index = 0; // restart index
                                        } else {
                                            index = 0; // keep cycling through high list and keep highList high
                                        }
                                    }
                                    // display use id within textview1
                                    textView1.setText(lowPriority.get(index).getTenantID());
                                    // display maintenance request within textview2
                                    textView2.setText(lowPriority.get(index).getRequest());
                                    textView6.setText("Low");
                                    index++;
                                }
                            }
                        });
                    }
                    // sends response message on click
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // update requests with landlord response
                                if (highList && !highPriority.isEmpty()) {
                                    requestModel = highPriority.get(index);
                                    db.collection("maintananence").document(requestModel.requestID).update("response", editText1.getText().toString());
                                } else if (!highList && !lowPriority.isEmpty()) {
                                    requestModel = lowPriority.get(index);
                                    db.collection("maintananence").document(requestModel.requestID).update("response", editText1.getText().toString());
                                } else {
                                    editText1.setText(null); // do not update collection because both lists are empty
                                }

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