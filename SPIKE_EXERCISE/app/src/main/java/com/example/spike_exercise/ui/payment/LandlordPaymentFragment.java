package com.example.spike_exercise.ui.payment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.spike_exercise.AccountActivity;
import com.example.spike_exercise.R;
import com.example.spike_exercise.data.DatabaseKeys;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.model.Account;
import com.example.spike_exercise.databinding.FragmentLandlordPaymentBinding;
import com.example.spike_exercise.databinding.FragmentTenantMaintenanceBinding;
import com.example.spike_exercise.ui.TextInputValidator;
import com.example.spike_exercise.ui.maintenance.Request;
import com.example.spike_exercise.ui.maintenance.TenantMaintenanceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class LandlordPaymentFragment extends Fragment implements OnCompleteListener<Void>, EventListener<DocumentSnapshot> {

    private LandlordPaymentViewModel mViewModel;
    private FragmentLandlordPaymentBinding binding;
    PaymentModel userPayment;
    TextView balanceText;
    FirebaseFirestore db;
    EditText ed1;
    float rentAmount, feeAmount;
    ArrayList<PaymentModel> tenantsList;
    Query tenantQuery;
    ListenerRegistration balanceObserver;

    public static LandlordPaymentFragment newInstance() {
        return new LandlordPaymentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLandlordPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        db = FirebaseFirestore.getInstance();
        tenantsList = new ArrayList<>();
        balanceText = binding.balance;
        //text = binding.textView7;
        AutoCompleteTextView spinner = (AutoCompleteTextView) binding.paymentTenantSelection.getEditText();
        //ed1 = binding.editTextTextPersonName;
        //Button button = binding.button2;

        binding.paymentAmountInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                rentAmount = text.isEmpty() ? 0.0f :Float.parseFloat(text);
                updateRentAmountField();
            }
        });

        binding.paymentFeeInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                feeAmount = text.isEmpty() ? 0.0f :Float.parseFloat(text);
                updateRentAmountField();
            }
        });

        binding.paymentTenantFilter.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if(checkedId != R.id.single_tenant_button) {
                    binding.paymentTenantSelection.setVisibility(View.GONE);
                    binding.title.setVisibility(View.GONE);
                    binding.balanceLayout.setVisibility(View.GONE);
                } else {
                    binding.paymentTenantSelection.setVisibility(View.VISIBLE);
                    binding.title.setVisibility(View.VISIBLE);
                    binding.balanceLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        tenantQuery = db.collection("users").whereEqualTo("landlordID", LoginRepository.getInstance().getCurrentUser().getUid());
        tenantQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value == null) return;
                tenantsList.clear();
                for (DocumentSnapshot document : value.getDocuments()) {
                    Float balance = document.get(DatabaseKeys.FIELD_USERS_BALANCE, Float.class);
                    String firstName = document.get(DatabaseKeys.FIELD_USERS_FIRST_NAME, String.class);
                    String lastName = document.get(DatabaseKeys.FIELD_USERS_LAST_NAME, String.class);
                    if(balance != null && firstName != null && lastName != null) {
                        tenantsList.add(new PaymentModel(balance, document.getId(), firstName, lastName));
                    }
                }

                ArrayAdapter<PaymentModel> adapter = new ArrayAdapter<PaymentModel>(getActivity(), android.R.layout.simple_spinner_dropdown_item, tenantsList);
                spinner.setAdapter(adapter);
                spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        updateSelectedTenant(adapter.getItem(position));
                        balanceText.setText(String.format("%.2f", userPayment.getBalance()));
                        binding.title.setText(String.format("%s's Current Balance", userPayment.firstName));
                        //button.setOnClickListener(this::save);
                    }
                });
            }
        });
        tenantQuery.get();

        binding.paymentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextInputValidator.validateRequiredField(binding.paymentAmountInput)) {
                    binding.paymentConfirmButton.startAnimation();
                    save();
                }
            }
        });

        return root;
    }

    private void updateSelectedTenant(PaymentModel selectedTenant) {
        if(userPayment != null && balanceObserver != null) {
            balanceObserver.remove();
        }
        userPayment = selectedTenant;
        balanceObserver = db.collection("users").document(userPayment.paymentID).addSnapshotListener(this);
    }

    private void updateRentAmountField() {
        binding.amount.setText(String.format("%.2f", (rentAmount + feeAmount)));
    }

    private void clearAllFields() {
        binding.paymentAmountInput.getEditText().setText("");
        binding.paymentFeeInput.getEditText().setText("");
        updateRentAmountField();
    }

    private void save() {
        if(binding.paymentTenantFilter.getCheckedButtonId() == R.id.single_tenant_button) {
            db.collection("users").document(userPayment.paymentID).update("balance", FieldValue.increment(rentAmount + feeAmount))
                    .addOnCompleteListener(this);
        } else {
            WriteBatch batch = db.batch();
            CollectionReference usersCollection = db.collection("users");
            for(PaymentModel paymentModel : tenantsList) {
                batch.update(usersCollection.document(paymentModel.paymentID), "balance", FieldValue.increment(rentAmount + feeAmount));
            }
            batch.commit().addOnCompleteListener(this);
        }
    }

    public static boolean isParsable(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LandlordPaymentViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if(task.isSuccessful()) {
            clearAllFields();
            binding.paymentConfirmButton.doneLoadingAnimation(getResources().getColor(R.color.madrentals_red_light), AccountActivity.getBitmapFromVectorDrawable(getContext(), R.drawable.ic_baseline_check_circle_outline_24, R.color.white));
            tenantQuery.get();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    binding.paymentConfirmButton.revertAnimation();
                }
            };
            new Handler().postDelayed(runnable, 2000);
        } else {
            binding.paymentConfirmButton.revertAnimation();
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
        if(value != null) {
            Float balance = value.get(DatabaseKeys.FIELD_USERS_BALANCE, Float.class);
            if(balance != null) binding.balance.setText(String.format("%.2f", balance));
        }
    }
}