package com.example.spike_exercise.ui.maintenance;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.spike_exercise.R;
import com.example.spike_exercise.databinding.FragmentMaintenanceBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class MaintenanceFragment extends Fragment {

    private MaintenanceViewModel      maintenanceViewModel;
    private FragmentMaintenanceBinding binding;
    EditText ed1, ed2,ed3;
    FirebaseFirestore db;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        maintenanceViewModel =
                new ViewModelProvider(this).get(MaintenanceViewModel.class);

        binding = FragmentMaintenanceBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button button = binding.button;
        EditText ed1 = binding.editTextNumber;
        EditText ed2 = binding.editTextNumber2;
        EditText ed3 = binding.editTextTextPersonName2;
        db = FirebaseFirestore.getInstance();
        button.setOnClickListener(view -> save(view));
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void save(View v){
        Request request = new Request(Integer.parseInt(ed1.toString()),Integer.parseInt(ed2.toString()),ed3.getText().toString());
        db.collection("app_data").document("maintanence").set(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    System.out.print("Success");
                }
                else{
                    System.out.print("Fail");
                }
            }
        });
    }
}