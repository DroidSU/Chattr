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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.models.UserModel;
import com.morningstar.chattr.models.UserStatusModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginUsingEmailActivity extends AppCompatActivity {

    private final String TAG = "LoginUsingEMail";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewNewAccount;
    private ActionProcessButton buttonVerfiy;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private DatabaseReference databaseReference;

    private String emailAddress;
    private String password;
    private String mobileNumber;

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
                emailAddress = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                buttonVerfiy.setProgress(99);
                buttonVerfiy.setMode(ActionProcessButton.Mode.ENDLESS);
                if (!TextUtils.isEmpty(emailAddress) && !TextUtils.isEmpty(password)) {
                    signUpExistingUser();
                } else {
                    if (TextUtils.isEmpty(emailAddress))
                        editTextEmail.setError("Required");
                    if (TextUtils.isEmpty(password))
                        editTextPassword.setError("Required");
                    buttonVerfiy.setProgress(0);
                }
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

    private void signUpExistingUser() {
        firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            buttonVerfiy.setProgress(100);
                            sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String username = firebaseUser.getDisplayName();
                            if (username != null) {
                                databaseReference = FirebaseDatabase.getInstance().getReference(ConstantManager.FIREBASE_USERS_TABLE).child(username);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(ConstantManager.PREF_TITLE_USER_NAME, userModel.getUserName());
                                        editor.putString(ConstantManager.PREF_TITLE_USER_SURNAME, userModel.getUserSurname());
                                        editor.putString(ConstantManager.PREF_TITLE_USER_MOBILE, userModel.getUserMobile());
                                        editor.putString(ConstantManager.PREF_TITLE_USER_EMAIL, userModel.getUserEmail());
                                        editor.putString(ConstantManager.PREF_TITLE_USER_DP_URL, userModel.getUserDPUrl());
                                        editor.apply();

                                        mobileNumber = userModel.getUserMobile();
                                        updateUserStatus();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "Signing In failed" + e.getMessage());
                        Snackbar snackbar = Snackbar.make(linearLayout, "Please Check Login Credentials", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        buttonVerfiy.setProgress(-1);
                    }
                });
    }

    private void updateUserStatus() {
        databaseReference = FirebaseDatabase.getInstance().getReference(ConstantManager.FIREBASE_PHONE_NUMBERS_TABLE).child(mobileNumber);
        UserStatusModel userStatusModel = new UserStatusModel(true, true, null);
        databaseReference.setValue(userStatusModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(LoginUsingEmailActivity.this, LoadingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }
}
