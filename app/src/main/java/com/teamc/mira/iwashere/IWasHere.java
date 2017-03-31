package com.teamc.mira.iwashere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.presentation.login.LoginActivity;
import com.teamc.mira.iwashere.presentation.main.MainActivity;
import com.teamc.mira.iwashere.presentation.register.RegisterActivity;

/*
 * This class is a Splash Screen
 */
public class IWasHere extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = IWasHere.class.getCanonicalName();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iwashere);

        // Buttons
        findViewById(R.id.main_sign_in_btn).setOnClickListener(this);
        findViewById(R.id.main_sign_up_btn).setOnClickListener(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(IWasHere.this, MainActivity.class));
            finish();
        }

        /**
        Intent intent;

        if(getSharedPreferences("userInfo",MODE_PRIVATE).getString("token", null) != null){
            intent = new Intent(this, MainActivity.class);
        }else{
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();**/
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.main_sign_in_btn) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (i == R.id.main_sign_up_btn) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
        finish();
    }
}