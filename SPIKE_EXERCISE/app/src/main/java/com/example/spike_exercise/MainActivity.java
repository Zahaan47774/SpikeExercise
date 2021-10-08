package com.example.spike_exercise;

import android.os.Bundle;
import android.util.Log;

import com.example.spike_exercise.data.LoginRepository;
import com.example.spike_exercise.data.model.UserAccount;
import com.example.spike_exercise.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.spike_exercise.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_maintenance, R.id.navigation_payment, R.id.navigation_apply)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavGraph navGraph = null;
        UserAccount user = LoginRepository.getInstance().getCurrentUser();
        // Change navigation layout depending on whether a tenant or landlord is signed in
        switch(user.getAccountType()) {
            case TENANT:
                navGraph = navController.getNavInflater().inflate(R.navigation.navigation_main_tenant);
                break;
            case LANDLORD:
                navGraph = navController.getNavInflater().inflate(R.navigation.navigation_main_landlord);
                break;
        }
        navController.setGraph(navGraph);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Log.i("MainActivity", "Logged in as: "+ LoginRepository.getInstance().getCurrentUser().toString());
    }

}