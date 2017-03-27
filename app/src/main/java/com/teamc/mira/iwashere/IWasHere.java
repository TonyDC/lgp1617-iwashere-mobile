package com.teamc.mira.iwashere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.teamc.mira.iwashere.presentation.login.LoginActivity;
import com.teamc.mira.iwashere.presentation.main.MainActivity;

/*
 * This class is a Splash Screen
 */
public class IWasHere extends AppCompatActivity {

    public final static String TAG = IWasHere.class.getCanonicalName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent;


        if(getSharedPreferences("userInfo",MODE_PRIVATE).getString("token", null) != null){
            intent = new Intent(this, MainActivity.class);
        }else{
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();

    }
}
