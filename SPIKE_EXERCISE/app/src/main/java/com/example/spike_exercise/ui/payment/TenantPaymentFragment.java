package com.example.spike_exercise.ui.payment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.spike_exercise.AccountActivity;
import com.example.spike_exercise.R;
import com.example.spike_exercise.data.DatabaseKeys;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.model.UserAccount;
import com.example.spike_exercise.databinding.FragmentTenantPaymentBinding;
import com.example.spike_exercise.ui.TextInputValidator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class TenantPaymentFragment extends Fragment implements OnCompleteListener<Void> {

    private TenantPaymentViewModel tenantPaymentViewModel;
    private FragmentTenantPaymentBinding binding;

    private CircularProgressButton payButton;

    FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        tenantPaymentViewModel =
                new ViewModelProvider(this).get(TenantPaymentViewModel.class);

        binding = FragmentTenantPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        payButton = binding.paymentConfirmButton;
        final TextView balanceText = binding.balance;
        final AutoCompleteTextView accountTypeInput = (AutoCompleteTextView) binding.paymentBankTypeInput.getEditText();
        db = FirebaseFirestore.getInstance();

        accountTypeInput.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[] {"Checking", "Savings"}));

        LoginRepository.getInstance().observeCurrentUserBalance(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float balance) {
                balanceText.setText(String.format(Locale.US, "%.2f", balance));
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validatePaymentInformation()) {
                    payButton.startAnimation();
                    UserAccount currentUser = LoginRepository.getInstance().getCurrentUser();
                    final float amount = Float.parseFloat(binding.paymentAmountInput.getEditText().getText().toString());
                    WriteBatch batch = db.batch();

                    batch.update(db.collection(DatabaseKeys.DOC_USERS).document(currentUser.getUid()), DatabaseKeys.FIELD_USERS_BALANCE, FieldValue.increment(-amount));

                    Map<String, Object> paymentData = new HashMap<>();
                    paymentData.put(DatabaseKeys.FIELD_PAYMENTS_AMOUNT, amount);
                    paymentData.put(DatabaseKeys.FIELD_PAYMENTS_BALANCE, 0);
                    paymentData.put(DatabaseKeys.FIELD_PAYMENTS_NAME, currentUser.getFullName());
                    paymentData.put(DatabaseKeys.FIELD_PAYMENTS_TIMESTAMP, new Date().getTime());
                    paymentData.put(DatabaseKeys.FIELD_PAYMENTS_TENANT_ID, currentUser.getUid());
                    batch.set(db.collection(DatabaseKeys.DOC_PAYMENTS).document(), paymentData);

                    Task<Void> paymentTask = batch.commit();
                    paymentTask.addOnCompleteListener(TenantPaymentFragment.this);
                }
            }
        });

        return root;
    }

    private boolean validatePaymentInformation() {
        boolean isAmountValid = TextInputValidator.validateRequiredField(binding.paymentAmountInput);
        boolean isAccountValid = TextInputValidator.validateRequiredField(binding.paymentBankAccountInput);
        boolean isNameValid = TextInputValidator.validateRequiredField(binding.paymentBankNameInput);
        boolean isRoutingValid = TextInputValidator.validateRequiredField(binding.paymentBankRoutingInput);
        boolean isTypeValid = TextInputValidator.validateRequiredField(binding.paymentBankTypeInput);

        return isAmountValid && isAccountValid && isNameValid && isRoutingValid && isTypeValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void clearAllFields() {
        binding.paymentAmountInput.getEditText().setText("");
        binding.paymentBankAccountInput.getEditText().setText("");
        binding.paymentBankNameInput.getEditText().setText("");
        binding.paymentBankRoutingInput.getEditText().setText("");
        binding.paymentBankTypeInput.getEditText().setText("");
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            clearAllFields();
            payButton.doneLoadingAnimation(getResources().getColor(R.color.madrentals_red_light), AccountActivity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_check_circle_outline_24, R.color.white));
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    payButton.revertAnimation();
                }
            };
            new Handler().postDelayed(runnable, 2000);
        } else {
            payButton.revertAnimation();
        }
    }
}
