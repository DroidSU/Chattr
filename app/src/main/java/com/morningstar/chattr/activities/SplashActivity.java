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
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.managers.ProfileManager;
import com.morningstar.chattr.models.UserModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //start user sign in process
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (NetworkManager.isUserOnline(SplashActivity.this)) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                                    ProfileManager.userName = userModel.getUserName();
                                    ProfileManager.userSurname = userModel.getUserSurname();
                                    ProfileManager.userMobile = userModel.getUserMobile();
                                    ProfileManager.userDPUrl = userModel.getUserDPUrl();
                                    ProfileManager.userEmail = userModel.getUserEmail();
                                    ProfileManager.userId = userModel.getUserId();
                                }
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Intent intent = new Intent(SplashActivity.this, RegisterUsingEmail.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, NoNetworkActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 200);
    }
}
