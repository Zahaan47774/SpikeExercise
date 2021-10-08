package com.example.spike_exercise.ui.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentTenantMaintenanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TenantMaintenanceFragment extends Fragment implements OnCompleteListener<DocumentReference> {
    private FragmentTenantMaintenanceBinding binding;
    EditText ed3;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    String company;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TenantMaintenanceViewModel tenantMaintenanceViewModel = new ViewModelProvider(this).get(TenantMaintenanceViewModel.class);

        binding = FragmentTenantMaintenanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = binding.button;
         ed3 = binding.editTextTextPersonName2;
         Spinner spinner = binding.spinner;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
         userID = LoginRepository.getInstance().getCurrentUser().getUid();
        ArrayList<TenantInfo> list = new ArrayList<>();
        db.collection("users").whereEqualTo("accountType",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new TenantInfo(document.getId(),(String) document.get("companyName")));
                    }
                    ArrayAdapter<TenantInfo> adapter = new ArrayAdapter<TenantInfo>(getActivity(), android.R.layout.simple_spinner_dropdown_item,list);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long id) {
                            TenantInfo tenant = adapter.getItem(position);
                            company = tenant.getTenantID();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapter) {  }
                    });

                } else {
                    System.out.println("Error");
                }
            }
        });

    button.setOnClickListener(this::save);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void save(View v){
        Request request = new Request(userID,company,ed3.getText().toString());
        Task<DocumentReference> signupTask = db.collection("maintananence").add(request);
        signupTask.addOnCompleteListener(TenantMaintenanceFragment.this);
    }


    @Override
    public void onComplete(@NonNull Task<DocumentReference> task) {
        //loginViewModel.setBusyStatus(false);
        if(task.isSuccessful()) {
            System.out.println("Success");
        } else {
            String errorMessage = Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()).toLowerCase();
            System.out.println(errorMessage);
        }
    }
}