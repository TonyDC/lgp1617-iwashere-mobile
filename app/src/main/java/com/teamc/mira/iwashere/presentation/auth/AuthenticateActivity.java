package com.teamc.mira.iwashere.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;

public class AuthenticateActivity extends AppCompatActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        // Buttons
        findViewById(R.id.main_sign_in_btn).setOnClickListener(this);
        findViewById(R.id.main_sign_up_btn).setOnClickListener(this);
        findViewById(R.id.main_test_slider).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.main_sign_in_btn) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (i == R.id.main_sign_up_btn) {
            startActivity(new Intent(this, SignupActivity.class));
        }else if (i == R.id.main_test_slider) {
            startActivity(new Intent(this, PoiDetailActivity.class));
        }
        finish();
    }
}
