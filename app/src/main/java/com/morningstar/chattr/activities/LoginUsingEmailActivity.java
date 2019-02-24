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
import com.morningstar.chattr.services.UserRegistrationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import rx.subscriptions.CompositeSubscription;

public class LoginUsingEmailActivity extends AppCompatActivity {

    private final String TAG = "LoginUsingEmail";

    @BindView(R.id.loginEmailAddress)
    EditText editTextEmail;
    @BindView(R.id.loginPassword)
    EditText editTextPassword;
    @BindView(R.id.loginUsingEmailVerifyButton)
    ActionProcessButton buttonVerifiy;
    @BindView(R.id.signUp)
    TextView textViewSignUp;
    @BindView(R.id.loginUsingEmailRootLayout)
    LinearLayout rootLayout;

    private UserRegistrationService userRegistrationService;
    private CompositeSubscription compositeSubscription;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_using_email);
        ButterKnife.bind(this);

        buttonVerifiy.setProgress(0);
        buttonVerifiy.setMode(ActionProcessButton.Mode.ENDLESS);

        userRegistrationService = UserRegistrationService.newInstance();

        connectToServer();
        socket.on(ConstantManager.FIREBASE_AUTH_TOKEN_GENERATED, tokenListener());

        compositeSubscription = new CompositeSubscription();


        buttonVerifiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonVerifiy.setProgress(99);
                buttonVerifiy.setMode(ActionProcessButton.Mode.ENDLESS);
                if (NetworkManager.hasInternetAccess())
                    compositeSubscription.add(userRegistrationService.sendLoginInfo(editTextEmail, editTextPassword, socket, LoginUsingEmailActivity.this, buttonVerifiy));
                else
                    Snackbar.make(rootLayout, "No internet connection", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.signUp)
    public void goToSignUpActivity() {
        startActivity(new Intent(LoginUsingEmailActivity.this, RegisterUsingEmail.class));
        finish();
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

    //Listens for the token sent from the socket
    private Emitter.Listener tokenListener() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i(TAG, args[0].toString());
                JSONObject jsonObject = (JSONObject) args[0];
                String token = null;
                String email = null;
                String displayName = null;
                try {
                    token = jsonObject.getString("authToken");
                    displayName = jsonObject.getString("displayName");
                    email = jsonObject.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<String> userDetails = new ArrayList<>();
                userDetails.add(token);
                userDetails.add(email);
                userDetails.add(displayName);
                compositeSubscription.add(userRegistrationService.getAuthToken(userDetails, LoginUsingEmailActivity.this, buttonVerifiy));
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
