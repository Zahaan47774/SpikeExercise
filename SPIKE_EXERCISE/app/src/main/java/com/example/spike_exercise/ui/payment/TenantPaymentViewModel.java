package com.example.spike_exercise.ui.payment;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TenantPaymentViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private Button addFee;

    public TenantPaymentViewModel() {
        mText = new MutableLiveData<>();

    }

    public LiveData<String> getText() {
        return mText;
    }
}
