package com.example.spike_exercise.ui.apply;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ApplyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ApplyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the rental application fragment.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}