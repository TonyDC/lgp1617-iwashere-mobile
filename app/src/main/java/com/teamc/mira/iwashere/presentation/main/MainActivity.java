package com.teamc.mira.iwashere.presentation.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.presentation.auth.LoginActivity;
import com.teamc.mira.iwashere.presentation.auth.SignupActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment map = new MapFragment();
        final Fragment feed = new FeedFragment();
        final Fragment search = new SearchFragment();
        final Fragment accountSettings = new AccountSettingsFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                    findViewById(R.id.bottom_navigation);


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, map).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_map:
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, map).commit();
                            return true;
                        case R.id.action_feed:
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, feed).commit();
                            return true;
                        case R.id.action_camera:

                        case R.id.action_search:
                            fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.flContainer, )
                        case R.id.action_account_settings:

                    }
                    return true;
                }
            });
    }
}