package com.example.spike_exercise.ui.signup;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.example.spike_exercise.AccountActivity;
import com.example.spike_exercise.R;
import com.example.spike_exercise.data.AccountType;
import com.example.spike_exercise.data.DatabaseKeys;
import com.example.spike_exercise.databinding.FragmentSignupBinding;
import com.example.spike_exercise.ui.TextInputValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class SignupFragment extends Fragment implements OnCompleteListener<Void> {

    private static final String LOCAL_FIELD_USERS_COMP_NAME = "localCompanyName";
    private static final String LOCAL_FIELD_USERS_EMAIL = "localEmail";

    private SignupViewModel mViewModel;
    private FragmentSignupBinding binding;

    private TextInputLayout firstNameInput, lastNameInput, propertyMgrInput, companyNameInput, emailInput, passwordInput;
    private CircularProgressButton signupButton;

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        firstNameInput          = binding.signupFirstNameInput;
        lastNameInput           = binding.signupLastNameInput;
        propertyMgrInput        = binding.signupPropertyMgrInput;
        companyNameInput        = binding.signupCompanyNameInput;
        emailInput              = binding.signupEmailAddressInput;
        passwordInput           = binding.signupPasswordInput;

        // Load saved text input states on resuming from a configuration change
        if(savedInstanceState != null) {
            firstNameInput.getEditText().setText(savedInstanceState.getString(DatabaseKeys.FIELD_USERS_FIRST_NAME));
            lastNameInput.getEditText().setText(savedInstanceState.getString(DatabaseKeys.FIELD_USERS_LAST_NAME));
            propertyMgrInput.getEditText().setText(savedInstanceState.getString(DatabaseKeys.FIELD_USERS_PROP_MGR));
            companyNameInput.getEditText().setText(savedInstanceState.getString(LOCAL_FIELD_USERS_COMP_NAME));
            emailInput.getEditText().setText(savedInstanceState.getString(LOCAL_FIELD_USERS_EMAIL));
            //passwordInput.getEditText().setText(savedInstanceState.getString(FIELD_PASSWORD));
        }

        ((AutoCompleteTextView)propertyMgrInput.getEditText()).setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[] {"None"}));


        // Set up dynamic UI that changes when the toggle button is switched between the "Tenant" and "LandlordAccount" items
        MaterialButtonToggleGroup accountTypeButtonGroup = binding.signupAccountTypeButtonGroup;

        // Set initial selected item
        mViewModel.setSelectedAccountType(accountTypeButtonGroup.getCheckedButtonId() == R.id.signup_tenant_button ? AccountType.TENANT : AccountType.LANDLORD);

        // Observe all future changes and show/hide UI elements accordingly
        mViewModel.observeSelectedAccountType(getViewLifecycleOwner(), accountType -> companyNameInput.setVisibility(accountType == AccountType.LANDLORD ? View.VISIBLE : View.GONE));

        // Sync data in view model any time the user selects a different item in the UI
        accountTypeButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked) mViewModel.setSelectedAccountType(checkedId == R.id.signup_tenant_button ? AccountType.TENANT : AccountType.LANDLORD);
        });


        // Set up navigation action back to login screen
        Button loginButton = binding.signupLoginButton;
        loginButton.setOnClickListener(this::navigateToLoginFragment);

        signupButton = binding.signupButton;
        signupButton.setOnClickListener(view -> {
            if(validateTextInputs()) {
                signupButton.startAnimation();
                mViewModel.createUser(
                        firstNameInput.getEditText().getText().toString(),
                        lastNameInput.getEditText().getText().toString(),
                        propertyMgrInput.getEditText().getText().toString(),
                        companyNameInput.getEditText().getText().toString(),
                        emailInput.getEditText().getText().toString(),
                        passwordInput.getEditText().getText().toString(),
                        SignupFragment.this
                );
            }
        });

        return root;
    }

    private boolean validateTextInputs() {
        boolean isEmailValid = TextInputValidator.validateEmailField(emailInput);
        boolean isPasswordValid = TextInputValidator.validatePasswordField(passwordInput, true);
        boolean isFirstNameValid = TextInputValidator.validateRequiredField(firstNameInput);
        boolean isLastNameValid = TextInputValidator.validateRequiredField(lastNameInput);
        boolean isPropertyMgrValid = propertyMgrInput.getVisibility() == View.GONE || TextInputValidator.validateRequiredField(propertyMgrInput);

        return isFirstNameValid && isLastNameValid && isPropertyMgrValid && isEmailValid && isPasswordValid;
    }

    private void navigateToLoginFragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_navigate_to_login);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DatabaseKeys.FIELD_USERS_FIRST_NAME, firstNameInput.getEditText().getText().toString());
        outState.putString(DatabaseKeys.FIELD_USERS_LAST_NAME, lastNameInput.getEditText().getText().toString());
        outState.putString(DatabaseKeys.FIELD_USERS_PROP_MGR, propertyMgrInput.getEditText().getText().toString());
        outState.putString(LOCAL_FIELD_USERS_COMP_NAME, companyNameInput.getEditText().getText().toString());
        outState.putString(LOCAL_FIELD_USERS_EMAIL, emailInput.getEditText().getText().toString());
        //outState.putString(FIELD_PASSWORD, passwordInput.getEditText().getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            signupButton.doneLoadingAnimation(getResources().getColor(R.color.madrentals_red_light), AccountActivity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_check_circle_outline_24, R.color.white));
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    navigateToLoginFragment(signupButton);
                }
            };
            new Handler().postDelayed(runnable, 500);
        } else {
            signupButton.revertAnimation();
        }
    }
}