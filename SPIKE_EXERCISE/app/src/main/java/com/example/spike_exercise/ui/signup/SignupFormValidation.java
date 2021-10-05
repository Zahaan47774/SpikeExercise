package com.example.spike_exercise.ui.signup;

public class SignupFormValidation {

    public enum Error {
        FIRST_NAME_EMPTY, LAST_NAME_EMPTY,
    }

    private boolean formValid;

    public boolean isFormValid() {
        return formValid;
    }
}
