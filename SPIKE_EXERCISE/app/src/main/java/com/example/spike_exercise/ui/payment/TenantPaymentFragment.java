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

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.databinding.FragmentTenantPaymentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TenantPaymentFragment extends Fragment{

    private TenantPaymentViewModel tenantPaymentViewModel;
    private FragmentTenantPaymentBinding binding;

    FirebaseFirestore db;
    FirebaseAuth auth;
    String userID;
    ArrayList<PaymentModel> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        tenantPaymentViewModel =
                new ViewModelProvider(this).get(TenantPaymentViewModel.class);

        binding = FragmentTenantPaymentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;

        tenantPaymentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        final Button pay = binding.pay;
        final EditText payAmount = binding.payment;
        final TextView payView = binding.balance;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userID = LoginRepository.getInstance().getCurrentUser().getUid();


        db.collection("payments").whereEqualTo("tenantID", LoginRepository.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(new PaymentModel(Integer.parseInt((String) document.get("amount")), (String) document.get("name"), (String) document.get("tenantID"), (String) document.get("userID"), document.getId()));
                    }
                }

                pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int balance = Integer.parseInt(payView.getText().toString()) - Integer.parseInt(payAmount.getText().toString());
                        payView.setText("" + balance);
                        db.collection("payments").document(userID).update("amount",String.valueOf(balance)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                payAmount.getText().clear();
                                System.out.println("Success");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());
                            }
                        });
                    }
                });

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
