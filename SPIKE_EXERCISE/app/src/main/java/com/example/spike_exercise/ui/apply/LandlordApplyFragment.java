package com.example.spike_exercise.ui.apply;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spike_exercise.R;

public class LandlordApplyFragment extends Fragment {

    private LandlordApplyViewModel mViewModel;

    public static LandlordApplyFragment newInstance() {
        return new LandlordApplyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_landlord_apply, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LandlordApplyViewModel.class);
        // TODO: Use the ViewModel
    }

}