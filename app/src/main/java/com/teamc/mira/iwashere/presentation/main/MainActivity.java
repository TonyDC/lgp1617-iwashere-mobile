package com.teamc.mira.iwashere.presentation.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.services.LocationService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // starting Location Service Manager for receiving location data
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment map = new MapFragment();
        final Fragment feed = new FeedFragment();
        final Fragment search = new SearchFragment();
        final Fragment account = new AccountFragment();

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
                            //// TODO: 11/04/2017 Start new activity with camera and image uploading
                            return true;
                        case R.id.action_search:
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, search).commit();
                            return true;
                        case R.id.action_account:
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, account).commit();
                            return true;
                    }
                    return true;
                }
            });
    }
}