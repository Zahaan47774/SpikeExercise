package com.example.spike_exercise.ui;

import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class TextInputValidator {

    public static boolean validateRequiredField(TextInputLayout fieldLayout) {
        if(fieldLayout == null || fieldLayout.getEditText() == null) return false;
        boolean valid = validateRequiredField(fieldLayout.getEditText());
        if(!valid) {
            fieldLayout.setError("Required");
        } else {
            fieldLayout.setErrorEnabled(false);
        }
        return valid;
    }

    public static boolean validateEmailField(TextInputLayout fieldLayout) {
        if(fieldLayout == null || fieldLayout.getEditText() == null) return false;
        boolean valid = validateEmailField(fieldLayout.getEditText());
        if(!valid) {
            fieldLayout.setError("Invalid email address");
        } else {
            fieldLayout.setErrorEnabled(false);
        }
        return valid;
    }

    public static boolean validatePasswordField(TextInputLayout fieldLayout, boolean checkLength) {
        if(fieldLayout == null || fieldLayout.getEditText() == null) return false;
        boolean valid = validatePasswordField(fieldLayout.getEditText(), checkLength);
        if(!valid) {
            fieldLayout.setError("Password must be at least 8 characters long");
        } else {
            fieldLayout.setErrorEnabled(false);
        }
        return valid;
    }

    public static boolean validateRequiredField(EditText fieldEditText) {
        return fieldEditText != null && fieldEditText.getText() != null && validateRequiredField(fieldEditText.getText().toString());
    }

    public static boolean validateEmailField(EditText fieldEditText) {
        return fieldEditText != null && fieldEditText.getText() != null && validateEmailField(fieldEditText.getText().toString());
    }

    public static boolean validatePasswordField(EditText fieldEditText, boolean checkLength) {
        return fieldEditText != null && fieldEditText.getText() != null && validatePasswordField(fieldEditText.getText().toString(), checkLength);
    }

    public static boolean validateRequiredField(String fieldText) {
        return fieldText != null && !fieldText.isEmpty();
    }

    public static boolean validateEmailField(String emailText) {
        return emailText != null && Patterns.EMAIL_ADDRESS.matcher(emailText).matches();
    }

    public static boolean validatePasswordField(String passwordText, boolean checkLength) {
        return passwordText != null && (!checkLength || passwordText.length() >= 8);
    }
}
