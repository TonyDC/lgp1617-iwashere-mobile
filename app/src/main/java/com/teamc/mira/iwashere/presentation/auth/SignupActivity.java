package com.teamc.mira.iwashere.presentation.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.teamc.mira.iwashere.IWasHereActivity;
import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.data.source.remote.impl.UserRepositoryImpl;
import com.teamc.mira.iwashere.domain.executor.Executor;
import com.teamc.mira.iwashere.domain.executor.MainThread;
import com.teamc.mira.iwashere.domain.executor.impl.ThreadExecutor;
import com.teamc.mira.iwashere.domain.interactors.base.TemplateInteractor;
import com.teamc.mira.iwashere.domain.interactors.impl.SignupInteractorImpl;
import com.teamc.mira.iwashere.domain.repository.remote.UserRepository;
import com.teamc.mira.iwashere.threading.MainThreadImpl;

/**
 * Created by Duart on 27/03/2017.
 */

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SignupActivity.class.getSimpleName();
    private EditText inputEmail, inputUsername, inputPassword, inputConfirmPassword;
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
        inputUsername = (EditText) findViewById(R.id.confirm_password);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.confirm_password);
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
            String username = inputUsername.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!TextUtils.equals(password,confirmPassword)) {
                Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            //create user
            MainThread mainThread = MainThreadImpl.getInstance();
            Executor executor = ThreadExecutor.getInstance();
            UserRepository userRepository = new UserRepositoryImpl(this);
            TemplateInteractor.CallBack callback = new TemplateInteractor.CallBack() {
                @Override
                public void onSuccess(Object result) {
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Register Successful");
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onNetworkError() {

                }

                @Override
                public void onError(String code, String message) {
                    Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Failed to signup");
                    progressBar.setVisibility(View.GONE);
                }
            };
            SignupInteractorImpl signupInteractor = new SignupInteractorImpl(
                    executor,
                    mainThread,
                    callback,
                    userRepository,
                        email,username,password, confirmPassword);

            signupInteractor.execute();

        } else if (i == R.id.btn_sign_up_google) {
            startActivity(new Intent(this, GoogleActivity.class));
            finish();
        } else if (i == R.id.btn_sign_up_fb) {
            startActivity(new Intent(this, FacebookActivity.class));
            finish();
        }
    }
}
