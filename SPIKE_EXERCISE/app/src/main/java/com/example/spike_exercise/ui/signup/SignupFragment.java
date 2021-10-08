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
import android.widget.Button;
import android.widget.TextView;

import com.example.spike_exercise.R;
import com.example.spike_exercise.data.AccountType;
import com.example.spike_exercise.databinding.FragmentSignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

public class SignupFragment extends Fragment implements OnCompleteListener<Void> {

    private SignupViewModel mViewModel;
    private FragmentSignupBinding binding;

    private static final String FIELD_FIRST_NAME   = "field_first_name";
    private static final String FIELD_LAST_NAME    = "field_last_name";
    private static final String FIELD_COMPANY_NAME = "field_company_name";
    private static final String FIELD_EMAIL        = "field_email";
    private static final String FIELD_PASSWORD     = "field_password";

    private TextInputLayout firstNameInput, lastNameInput, companyNameInput, emailInput, passwordInput;
    private TextView passwordReqText;
    private Button signupButton, signupSuccessButton;
    private CircularProgressIndicator signupProgressIndicator;

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
        companyNameInput        = binding.signupCompanyNameInput;
        emailInput              = binding.signupEmailAddressInput;
        passwordInput           = binding.signupPasswordInput;

        passwordReqText         = binding.signupPasswordReqText;

        signupSuccessButton     = binding.signupSuccessButton;
        signupProgressIndicator = binding.signupProgressIndicator;

        // Load saved text input states on resuming from a configuration change
        if(savedInstanceState != null) {
            firstNameInput.getEditText().setText(savedInstanceState.getString(FIELD_FIRST_NAME));
            lastNameInput.getEditText().setText(savedInstanceState.getString(FIELD_LAST_NAME));
            companyNameInput.getEditText().setText(savedInstanceState.getString(FIELD_COMPANY_NAME));
            emailInput.getEditText().setText(savedInstanceState.getString(FIELD_EMAIL));
            //passwordInput.getEditText().setText(savedInstanceState.getString(FIELD_PASSWORD));
        }


        // Set up dynamic UI that changes when the toggle button is switched between the "Tenant" and "Landlord" items
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

        signupButton = binding.signupButton;;
        signupButton.setOnClickListener(view -> {
            if(validateTextInputs()) {
                mViewModel.createUser(
                        firstNameInput.getEditText().getText().toString(),
                        lastNameInput.getEditText().getText().toString(),
                        companyNameInput.getEditText().getText().toString(),
                        emailInput.getEditText().getText().toString(),
                        passwordInput.getEditText().getText().toString(),
                        SignupFragment.this
                );
            }
        });

        mViewModel.observeBusyStatus(getViewLifecycleOwner(), busy -> {
            if(busy) {
                signupButton.setText("");
                signupButton.setEnabled(false);
                signupProgressIndicator.setVisibility(View.VISIBLE);
            } else {
                signupButton.setText("SIGN UP");
                signupButton.setEnabled(true);
                signupProgressIndicator.setVisibility(View.GONE);
            }
        });

        return root;
    }

    private boolean validateTextInputs() {
        if(emailInput == null || passwordInput == null) return false;
        boolean isEmailValid = mViewModel.validateEmailField(emailInput.getEditText().getText().toString());
        boolean isPasswordValid = mViewModel.validatePasswordField(passwordInput.getEditText().getText().toString());
        boolean isFirstNameValid = mViewModel.validateRequiredField(firstNameInput.getEditText().getText().toString());
        boolean isLastNameValid = mViewModel.validateRequiredField(lastNameInput.getEditText().getText().toString());

        if(!isEmailValid) {
            emailInput.setError("Invalid email address");
        } else {
            emailInput.setErrorEnabled(false);
        }
        if(!isPasswordValid) {
            passwordInput.setError("Password must be at least 8 characters long");
            passwordReqText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            passwordInput.setErrorEnabled(false);
            passwordReqText.setTextColor(getResources().getColor(android.R.color.black));
        }
        if(!isFirstNameValid) {
            firstNameInput.setError("Required");
        } else {
            firstNameInput.setErrorEnabled(false);
        }
        if(!isLastNameValid) {
            lastNameInput.setError("Required");
        } else {
            lastNameInput.setErrorEnabled(false);
        }

        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid;
    }

    private void navigateToLoginFragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_navigate_to_login);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FIELD_FIRST_NAME, firstNameInput.getEditText().getText().toString());
        outState.putString(FIELD_LAST_NAME, lastNameInput.getEditText().getText().toString());
        outState.putString(FIELD_COMPANY_NAME, companyNameInput.getEditText().getText().toString());
        outState.putString(FIELD_EMAIL, emailInput.getEditText().getText().toString());
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
            signupButton.setVisibility(View.INVISIBLE);
            signupSuccessButton.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    navigateToLoginFragment(signupButton);
                }
            }, 500);
        }
    }
}