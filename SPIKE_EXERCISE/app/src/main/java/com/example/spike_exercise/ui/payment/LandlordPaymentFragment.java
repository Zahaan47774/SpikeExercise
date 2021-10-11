package com.example.spike_exercise.ui.payment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.spike_exercise.R;
import com.example.spike_exercise.data.DatabaseKeys;
import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.model.Account;
import com.example.spike_exercise.databinding.FragmentLandlordPaymentBinding;
import com.example.spike_exercise.databinding.FragmentTenantMaintenanceBinding;
import com.example.spike_exercise.ui.maintenance.Request;
import com.example.spike_exercise.ui.maintenance.TenantMaintenanceFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class LandlordPaymentFragment extends Fragment {

    private LandlordPaymentViewModel mViewModel;
    private FragmentLandlordPaymentBinding binding;
    PaymentModel userPayment;
    TextView text,landlord;
    FirebaseFirestore db;
    EditText ed1;
    int addAmount;
    public static LandlordPaymentFragment newInstance() {
        return new LandlordPaymentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLandlordPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        addAmount = 0;db = FirebaseFirestore.getInstance();
        ArrayList<PaymentModel> list = new ArrayList<>();
        text = binding.textView7;
        Spinner spinner = binding.spinner3;
        ed1 = binding.editTextTextPersonName;
        Button button = binding.button2;
        db.collection("users").whereEqualTo("landlordID", LoginRepository.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new PaymentModel(document.getLong("balance").intValue(),document.getId(),(String) document.get("firstName")));
                    }

                    ArrayAdapter<PaymentModel> adapter = new ArrayAdapter<PaymentModel>(getActivity(), android.R.layout.simple_spinner_dropdown_item,list);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view,
                                                   int position, long id) {
                            userPayment = adapter.getItem(position);
                            text.setText(String.valueOf(userPayment.getBalance()));
                            button.setOnClickListener(this::save);
                        }

                        private void save(View view) {
                            if(isParsable(ed1.getText().toString())){
                                addAmount = Integer.parseInt(ed1.getText().toString());
                            }
                             int total = addAmount+ userPayment.getBalance();
                            db.collection("users").document(userPayment.paymentID).update("balance",total).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    System.out.println("Success");
                                    getActivity().recreate();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapter) {  }
                    });

                } else {
                    System.out.println("No users available");
                }
            }
        });


        return root;
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

}