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
import com.example.spike_exercise.databinding.FragmentMaintenanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MaintenanceFragment extends Fragment implements OnCompleteListener<DocumentReference> {

    private MaintenanceViewModel      maintenanceViewModel;
    private FragmentMaintenanceBinding binding;
    EditText ed1,ed3;
    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        maintenanceViewModel =
                new ViewModelProvider(this).get(MaintenanceViewModel.class);

        binding = FragmentMaintenanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = binding.button;
         ed1 = binding.editTextNumber2;
         ed3 = binding.editTextTextPersonName2;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
         userID = auth.getUid();
        button.setOnClickListener(view -> save(view));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void save(View v){
        Request request = new Request(ed1.getText().toString(),userID,ed3.getText().toString());
        Task<DocumentReference> signupTask = db.collection("maintananence").add(request);
        signupTask.addOnCompleteListener(MaintenanceFragment.this);
    }

    private void navigateToMaintananceActivity() {
        Intent intent = new Intent(getContext(), MaintenanceFragment.class);
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

//  loginButton.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View view) {
//        if(validateTextInputs()) {
//        loginViewModel.setBusyStatus(true);
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        Task<AuthResult> signupTask = firebaseAuth.signInWithEmailAndPassword(emailInput.getEditText().getText().toString(), passwordInput.getEditText().getText().toString());
//        signupTask.addOnCompleteListener(LoginFragment.this);
//        }
//        }
//        });
