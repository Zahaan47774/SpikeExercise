package com.example.spike_exercise.ui.apply;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentTenantApplyBinding;
import com.example.spike_exercise.ui.login.LoginViewModel;
import com.example.spike_exercise.ui.login.LoginViewModelFactory;
import com.example.spike_exercise.ui.maintenance.TenantInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TenantApplyFragment extends Fragment {

    private TenantApplyViewModel tenantApplyViewModel;
    private LoginViewModel       loginViewModel;
    private FragmentTenantApplyBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    String company;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        tenantApplyViewModel =
                new ViewModelProvider(this).get(TenantApplyViewModel.class);

        binding = FragmentTenantApplyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /*
        final TextView textView = binding.textNotifications;
        tenantApplyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


         */
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText apply_nameEdittext = binding.applicationName;
        final EditText apply_applyaddressEditText = binding.applicationApplyAddress;
        Spinner spinner = binding.spinner2;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = LoginRepository.getInstance().getCurrentUser().getUid();

        final Button submit_button = binding.submitButton;

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
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application newApplication = new Application(userID,
                        apply_nameEdittext.getText().toString(),
                        apply_applyaddressEditText.getText().toString(),
                        company);

                Task<DocumentReference> signupTask = db.collection("application").add(newApplication);
                //signupTask.addOnCompleteListener(TenantApplyFragment.this);

            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
