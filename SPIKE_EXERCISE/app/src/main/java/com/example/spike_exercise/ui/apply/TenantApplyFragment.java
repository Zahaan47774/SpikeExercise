package com.example.spike_exercise.ui.apply;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentTenantApplyBinding;
import com.example.spike_exercise.ui.maintenance.Request;
import com.example.spike_exercise.ui.maintenance.TenantInfo;
import com.example.spike_exercise.ui.maintenance.TenantMaintenanceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TenantApplyFragment extends Fragment {

    private FragmentTenantApplyBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    String company;
    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {

        binding = FragmentTenantApplyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner = binding.spinner2;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = LoginRepository.getInstance().getCurrentUser().getUid();

        final Button submit_button = binding.submitButton;
        TextInputLayout address = binding.applicationApplyAddress;
        EditText fullName = binding.applicationName;
        ArrayList<TenantInfo> list = new ArrayList<>();
        db.collection("users").whereEqualTo("accountType",1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    list.add(new TenantInfo(document.getId(),(String) document.get("companyName")));
                }
                ArrayAdapter<TenantInfo> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view1,
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
        });
        submit_button.setOnClickListener(view12 -> {
            Application applicant = new Application(userID,fullName.getText().toString(), 
                    address.getText().toString(),company);
            Task<DocumentReference> applyTask = db.collection("application").add(applicant);
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
