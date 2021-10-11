package com.example.spike_exercise.ui.apply;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spike_exercise.AccountActivity;
import com.example.spike_exercise.R;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentTenantApplyBinding;
import com.example.spike_exercise.ui.TextInputValidator;
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

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class TenantApplyFragment extends Fragment implements OnCompleteListener<DocumentReference> {

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


        AutoCompleteTextView spinner = (AutoCompleteTextView) binding.applicationPropertyManager.getEditText();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = LoginRepository.getInstance().getCurrentUser().getUid();

        final CircularProgressButton submit_button = binding.submitButton;
        TextInputLayout address = binding.applicationApplyAddress;
        ArrayList<TenantInfo> list = new ArrayList<>();
        db.collection("users").whereEqualTo("accountType",1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    list.add(new TenantInfo(document.getId(),(String) document.get("companyName")));
                }
                ArrayAdapter<TenantInfo> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spinner.setAdapter(adapter);
                spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        TenantInfo tenant = adapter.getItem(position);
                        company = tenant.getTenantID();
                    }
                });
            } else {
                System.out.println("Error");
            }
        });
        submit_button.setOnClickListener(view12 -> {
            if(TextInputValidator.validateRequiredField(binding.applicationPropertyManager) && TextInputValidator.validateRequiredField(address)) {
                submit_button.startAnimation();
                Application applicant = new Application(userID, LoginRepository.getInstance().getCurrentUser().getFullName(),
                        address.getEditText().getText().toString(), company);
                Task<DocumentReference> applyTask = db.collection("application").add(applicant);
                applyTask.addOnCompleteListener(TenantApplyFragment.this);
            }
        });
    }

    @Override
    public void onComplete(@NonNull Task<DocumentReference> task) {
        if(task.isSuccessful()) {
            binding.applicationErrorText.setVisibility(View.GONE);
            binding.submitButton.doneLoadingAnimation(getResources().getColor(R.color.madrentals_red_light), AccountActivity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_check_circle_outline_24, R.color.white));
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    binding.submitButton.revertAnimation();
                }
            };
            new Handler().postDelayed(runnable, 2000);
        } else {
            binding.applicationErrorText.setText(task.getException().getMessage());
            binding.applicationErrorText.setVisibility(View.VISIBLE);
            binding.submitButton.revertAnimation();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
