/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.auth.FirebaseAuth;
import com.morningstar.chattr.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginUsingEmailActivity extends AppCompatActivity {

    private final String TAG = "LoginUsingEMail";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewNewAccount;
    private ActionProcessButton buttonVerfiy;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_using_email);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.loginEmailAddress);
        editTextPassword = findViewById(R.id.loginPassword);
        buttonVerfiy = findViewById(R.id.loginUsingEmailVerifyButton);
        textViewNewAccount = findViewById(R.id.signUp);
        linearLayout = findViewById(R.id.loginUsingEmailRootLayout);
        buttonVerfiy.setProgress(0);
        buttonVerfiy.setMode(ActionProcessButton.Mode.ENDLESS);


        buttonVerfiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonVerfiy.setProgress(99);
                buttonVerfiy.setMode(ActionProcessButton.Mode.ENDLESS);
            }
        });

        textViewNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginUsingEmailActivity.this, RegisterUsingEmail.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
