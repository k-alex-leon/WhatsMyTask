package com.example.whatsmytask.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.whatsmytask.R;
import com.example.whatsmytask.fragments.DashboardFragment;
import com.example.whatsmytask.fragments.HomeFragment;
import com.example.whatsmytask.fragments.OurTasksFragment;
import com.example.whatsmytask.providers.AuthProvider;
import com.example.whatsmytask.providers.TokenProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        openFragment(HomeFragment.newInstance("", ""));

        //metodo para generar un token de forma auto
        createToken();

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.itemOurTasks:
                            openFragment(OurTasksFragment.newInstance("", ""));
                            return true;
                        case R.id.itemHome:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.itemDashboard:
                            openFragment(DashboardFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };


    private void createToken(){
        mTokenProvider.create(mAuthProvider.getUid());
    }


}
