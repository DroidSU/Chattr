/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.ProfileManager;
import com.rey.material.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterUsingEmail extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignUp;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    private String emailAddress;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_using_email);

        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.registerEmail);
        editTextPassword = findViewById(R.id.registerPassword);
        buttonSignUp = findViewById(R.id.registerConfirm);
        linearLayout = findViewById(R.id.registerUsingEmailRootLayout);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                if (!TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(password)) {
                    signUpNewUser();
                } else {
                    if (TextUtils.isEmpty(emailAddress))
                        editTextEmail.setError("Required");
                    if (TextUtils.isEmpty(password))
                        editTextPassword.setError("Required");
                }
            }
        });
    }

    private void signUpNewUser() {
        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(ConstantManager.PREF_TITLE_USER_EMAIL, emailAddress);
                            editor.putString(ConstantManager.PREF_TITLE_USER_ID, firebaseAuth.getUid());
                            editor.apply();
                            ProfileManager.userEmail = emailAddress;
                            ProfileManager.userId = firebaseAuth.getUid();

                            Intent intent = new Intent(RegisterUsingEmail.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(linearLayout, "Account could not be registered", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Toast.makeText(RegisterUsingEmail.this, "Account already registered", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterUsingEmail.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
