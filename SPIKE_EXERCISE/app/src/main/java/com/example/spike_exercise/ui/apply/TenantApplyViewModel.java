package com.example.spike_exercise.ui.apply;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TenantApplyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TenantApplyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the rental application fragment.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}