package com.teamc.mira.iwashere;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.presentation.auth.AuthenticateActivity;
import com.teamc.mira.iwashere.presentation.auth.LoginActivity;
import com.teamc.mira.iwashere.presentation.main.MainActivity;
import com.teamc.mira.iwashere.presentation.auth.SignupActivity;

/*
 * This class is a Splash Screen
 */
public class IWasHereActivity extends AppCompatActivity {

    public final static String TAG = IWasHereActivity.class.getCanonicalName();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(IWasHereActivity.this, MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(IWasHereActivity.this, AuthenticateActivity.class));
            finish();
        }
    }


}