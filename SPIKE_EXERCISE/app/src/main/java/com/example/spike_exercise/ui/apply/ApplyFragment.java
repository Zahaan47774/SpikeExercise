package com.example.spike_exercise.ui.apply;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spike_exercise.R;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentApplyBinding;
import com.example.spike_exercise.ui.login.LoginViewModel;
import com.example.spike_exercise.ui.login.LoginViewModelFactory;
import com.example.spike_exercise.ui.maintenance.MaintenanceFragment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ApplyFragment extends Fragment {

    private ApplyViewModel       applyViewModel;
    private LoginViewModel       loginViewModel;
    private FragmentApplyBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        applyViewModel =
                new ViewModelProvider(this).get(ApplyViewModel.class);

        binding = FragmentApplyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /*
        final TextView textView = binding.textNotifications;
        applyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = LoginRepository.getInstance().getCurrentUser().getUid();

        final Button submit_button = binding.submitButton;
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Application newApplication = new Application(userID,
                        apply_nameEdittext.getText().toString(),
                        apply_applyaddressEditText.getText().toString());

                Task<DocumentReference> signupTask = db.collection("application").add(newApplication);
                //signupTask.addOnCompleteListener(ApplyFragment.this);

            }
        });


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
