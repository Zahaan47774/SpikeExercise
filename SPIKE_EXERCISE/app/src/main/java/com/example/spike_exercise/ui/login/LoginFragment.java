package com.example.spike_exercise.ui.login;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spike_exercise.AccountActivity;
import com.example.spike_exercise.MainActivity;
import com.example.spike_exercise.data.AuthListener;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.model.UserAccount;
import com.example.spike_exercise.databinding.FragmentLoginBinding;

import com.example.spike_exercise.R;
import com.example.spike_exercise.ui.signup.SignupFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class LoginFragment extends Fragment implements AuthListener {

    private LoginViewModel       loginViewModel;
    private FragmentLoginBinding binding;

    private CircularProgressButton loginButton;
    private TextInputLayout        emailInput, passwordInput;
    private CircularProgressIndicator loginProgressIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final Button signupButton = binding.loginSignupButton;
        loginButton = binding.loginButton;
        loginProgressIndicator = binding.loginProgressIndicator;

        emailInput = binding.loginEmailAddressInput;
        passwordInput = binding.loginPasswordInput;

        signupButton.setOnClickListener(this::navigateToSignupFragment);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateTextInputs()) {
                    loginButton.startAnimation();
                    loginViewModel.setBusyStatus(true);
                    loginViewModel.login(emailInput.getEditText().getText().toString(), passwordInput.getEditText().getText().toString(), LoginFragment.this);
                }
            }
        });

        loginViewModel.observeBusyStatus(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean busy) {
                if(busy) {
                    //loginButton.setText("");
                    //loginButton.setEnabled(false);
                    //loginProgressIndicator.setVisibility(View.VISIBLE);
                } else {
                    //loginButton.setText("LOG IN");
                    //loginButton.setEnabled(true);
                    //loginProgressIndicator.setVisibility(View.GONE);
                }
            }
        });
    }

    private boolean validateTextInputs() {
        if(emailInput == null || passwordInput == null) return false;
        boolean isEmailValid = loginViewModel.validateRequiredField(emailInput.getEditText().getText().toString());
        boolean isPasswordValid = loginViewModel.validateRequiredField(passwordInput.getEditText().getText().toString());

        if(!isEmailValid) {
            emailInput.setError("Required");
        } else {
            emailInput.setErrorEnabled(false);
        }
        if(!isPasswordValid) {
            passwordInput.setError("Required");
        } else {
            passwordInput.setErrorEnabled(false);
        }

        return isEmailValid && isPasswordValid;
    }

    private void navigateToSignupFragment(View view) {
        Navigation.findNavController(view).navigate(R.id.action_navigate_to_signup);
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSuccess(UserAccount user) {
        loginButton.doneLoadingAnimation(getResources().getColor(R.color.madrentals_red_light), AccountActivity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_check_circle_outline_24, R.color.white));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //loginButton.revertAnimation();
                navigateToMainActivity();
            }
        };
        new Handler().postDelayed(runnable, 500);
    }

    @Override
    public void onFailure(Exception e) {
        loginButton.revertAnimation();
        loginViewModel.setBusyStatus(false);
        String errorMessage = e.getMessage().toLowerCase();
        if(errorMessage.contains("email") || errorMessage.contains("password") || errorMessage.contains("no user")) {
            passwordInput.setError("Oops! The email address or password you entered was incorrect.");
        } else {
            passwordInput.setError(e.getMessage());
        }
    }
}