package com.example.spike_exercise.data;

import com.example.spike_exercise.data.model.UserAccount;

public interface AuthListener {
    void onSuccess(UserAccount user);
    void onFailure(Exception e);
}
