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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.services.UserRegistrationService;

import androidx.appcompat.app.AppCompatActivity;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.subscriptions.CompositeSubscription;

public class RegisterUsingEmail extends AppCompatActivity {

    private static final String TAG = "RegisterUsingEmail";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextCOnfirmPassword;

    private ActionProcessButton buttonSignUp;
    private LinearLayout linearLayout;
    private TextView textViewSignIn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;

    private String emailAddress;
    private String password;

    private CompositeSubscription compositeSubscription;
    private Socket socket;
    private UserRegistrationService userRegistrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_using_email);

        firebaseAuth = FirebaseAuth.getInstance();
        compositeSubscription = new CompositeSubscription();                //setting up RX subscription

        editTextEmail = findViewById(R.id.registerEmail);
        editTextPassword = findViewById(R.id.registerPassword);
        buttonSignUp = findViewById(R.id.registerConfirm);
        linearLayout = findViewById(R.id.registerUsingEmailRootLayout);
        textViewSignIn = findViewById(R.id.signIn);
        editTextCOnfirmPassword = findViewById(R.id.registerPasswordConfirm);

        connectToServer();
        socket.on(ConstantManager.REGISTRATION_COMPLETED_EVENT, getRegistrationResponse());

        userRegistrationService = UserRegistrationService.newInstance();        //service to start the user registration

        buttonSignUp.setMode(ActionProcessButton.Mode.ENDLESS);
        buttonSignUp.setProgress(0);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                buttonSignUp.setProgress(99);
                buttonSignUp.setMode(ActionProcessButton.Mode.ENDLESS);

                compositeSubscription.add(userRegistrationService.sendRegistrationInfo(editTextEmail, editTextPassword, editTextCOnfirmPassword, buttonSignUp, socket));
            }
        });

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUsingEmail.this, LoginUsingEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private Emitter.Listener getRegistrationResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String responseMessage = (String) args[0];
                compositeSubscription.add(userRegistrationService.receiveRegistrationResponse(responseMessage, RegisterUsingEmail.this, buttonSignUp));
            }
        };
    }

    private void connectToServer() {
        try {
            socket = IO.socket(ConstantManager.IP_LOCALHOST);
        } catch (Exception e) {
            Log.i(TAG, "Connection failed: " + e.getMessage());
            Toast.makeText(this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
        }
        socket.connect();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        compositeSubscription.clear();
    }
}
