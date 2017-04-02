package com.teamc.mira.iwashere.presentation.signup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.IWasHereActivity;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.presentation.main.MainActivity;

/**
 * Created by Duart on 27/03/2017.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Buttons
        findViewById(R.id.btn_sign_up).setOnClickListener(this);
        findViewById(R.id.btn_sign_up_fb).setOnClickListener(this);
        findViewById(R.id.btn_sign_up_google).setOnClickListener(this);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        // TODO other inputs
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, IWasHereActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_sign_up) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            // TODO other inputs
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            //create user
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(SignupActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        } else if (i == R.id.btn_sign_up_google) {
            startActivity(new Intent(this, GoogleActivity.class));
            finish();
        } else if (i == R.id.btn_sign_up_fb) {
            startActivity(new Intent(this, FacebookActivity.class));
            finish();
        }
    }
}
