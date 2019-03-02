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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.material.snackbar.Snackbar;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.managers.UtilityManager;
import com.morningstar.chattr.services.UserRegistrationService;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.subscriptions.CompositeSubscription;

public class RegisterUsingEmail extends AppCompatActivity {

    private static final String TAG = "RegisterUsingEmail";

    @BindView(R.id.registerEmail)
    EditText editTextEmail;
    @BindView(R.id.registerPassword)
    EditText editTextPassword;
    @BindView(R.id.registerUsername)
    EditText editTextUsername;
    @BindView(R.id.registerMobileNumber)
    EditText editTextUserMobile;
    @BindView(R.id.registerConfirm)
    ActionProcessButton buttonSignUp;
    @BindView(R.id.signIn)
    TextView textViewSignIn;
    @BindView(R.id.registerUsingEmailRootLayout)
    LinearLayout rootLayout;

    private CompositeSubscription compositeSubscription;
    private Socket socket;
    private UserRegistrationService userRegistrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_using_email);
        ButterKnife.bind(this);

        compositeSubscription = new CompositeSubscription();                //setting up RX subscription

        connectToServer();
        socket.on(ConstantManager.REGISTRATION_COMPLETED_EVENT, getRegistrationResponse());

        userRegistrationService = UserRegistrationService.newInstance();        //service to start the user registration

        buttonSignUp.setProgress(0);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonSignUp.setProgress(99);
                buttonSignUp.setMode(ActionProcessButton.Mode.ENDLESS);

                if (NetworkManager.hasInternetAccess()) {
                    boolean isUserNameTaken = UtilityManager.isUserNameTaken(editTextUsername.getText().toString());
                    boolean isPhoneNumberRegistered = UtilityManager.isMobileNumberAlreadyRegistered(editTextUserMobile.getText().toString());

                    if (!isUserNameTaken && !isPhoneNumberRegistered)
                        compositeSubscription.add(userRegistrationService.sendRegistrationInfo(editTextEmail, editTextPassword, editTextUsername, editTextUserMobile, buttonSignUp, socket));
                    else {
                        if (isUserNameTaken)
                            editTextUsername.setError("Username not available");
                        if (isPhoneNumberRegistered)
                            editTextUserMobile.setError("This phone number has already been registered");
                        buttonSignUp.setProgress(0);
                    }
                } else {
                    Snackbar.make(rootLayout, "No internet connection", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @OnClick(R.id.signIn)
    public void goToSignInActivity() {
        startActivity(new Intent(RegisterUsingEmail.this, LoginUsingEmailActivity.class));
        finish();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null)
            socket.disconnect();
        compositeSubscription.clear();
    }
}
