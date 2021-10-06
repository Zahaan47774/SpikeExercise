package com.example.spike_exercise.ui.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.spike_exercise.R;
import com.example.spike_exercise.databinding.FragmentPaymentBinding;

public class PaymentFragment extends Fragment{

    private PaymentViewModel       paymentViewModel;
    private FragmentPaymentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        paymentViewModel =
                new ViewModelProvider(this).get(PaymentViewModel.class);

        binding = FragmentPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;

        paymentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final Button pay = binding.pay;
        final Button addFee = binding.addFee;
        final EditText payAmount = binding.payment;
        final EditText addAmount = binding.addAmount;
        final TextView payView = binding.balance;

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int balance = Integer.parseInt(payView.getText().toString()) - Integer.parseInt(payAmount.getText().toString());
                payView.setText("" + balance);
            }
        });

        addFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int balance = Integer.parseInt(payView.getText().toString()) + Integer.parseInt(addAmount.getText().toString());
                payView.setText("" + balance);
            }
        });

        payAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payAmount.getText().clear();
            }
        });

        addAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAmount.getText().clear();
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
