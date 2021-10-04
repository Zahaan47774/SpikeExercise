package com.example.spike_exercise.ui.maintenance;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MaintenanceViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MaintenanceViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the maintenance requests fragment.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}