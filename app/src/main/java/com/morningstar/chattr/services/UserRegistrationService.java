/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.services;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.morningstar.chattr.activities.AccountDetailsActivity;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.UtilityManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class UserRegistrationService {
    private static final String TAG = "UserRegistrationService";
    private static UserRegistrationService userRegistrationService;
    private final int USER_ERROR_EMPTY_PASSWORD = 1;
    private final int USER_ERROR_EMPTY_EMAIL = 2;
    private final int USER_ERROR_PASSWORD_NOT_MATCH = 3;
    private final int USER_ERROR_PASSWORD_SHORT = 4;
    private final int USER_ERROR_EMAIL_BAD_FORMAT = 5;
    private final int REGISTRATION_SUCCESS = 0;
    private final int REGISTRATION_FAILURE = -1;

    private String emailAddress;

    public static UserRegistrationService newInstance() {
        if (userRegistrationService == null) {
            userRegistrationService = new UserRegistrationService();
        }

        return userRegistrationService;
    }

    public Subscription sendRegistrationInfo(EditText editTextUserEmail, EditText editTextPassword,
                                             EditText editTextConfirmPassword, ActionProcessButton button, Socket socket) {

        ArrayList<String> userDetails = new ArrayList<>();
        userDetails.add(editTextUserEmail.getText().toString());
        userDetails.add(editTextPassword.getText().toString());
        userDetails.add(editTextConfirmPassword.getText().toString());

        Observable<ArrayList<String>> userDetailsObservable = Observable.just(userDetails);

        return userDetailsObservable
                .subscribeOn(Schedulers.io())
                .map(new Func1<ArrayList<String>, Integer>() {
                    @Override
                    public Integer call(ArrayList<String> strings) {
                        String userEmail = strings.get(0);
                        String userPassword = strings.get(1);
                        String userConfirmPassword = strings.get(2);

                        int RESULT_CODE = -1;

                        if (!userEmail.isEmpty() && !userPassword.isEmpty() && userPassword.length() >= 6
                                && !userConfirmPassword.isEmpty() && userPassword.equalsIgnoreCase(userConfirmPassword)) {
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put(ConstantManager.STRING_EMAIL, userEmail);
                                jsonObject.put(ConstantManager.STRING_PASSWORD, userPassword);

                                socket.emit("userData", jsonObject);

                                RESULT_CODE = REGISTRATION_SUCCESS;
                                emailAddress = userEmail;
                            } catch (JSONException e) {
                                Log.i(TAG, "JSON Exception: " + e.getMessage());
                                RESULT_CODE = REGISTRATION_FAILURE;
                            }
                        } else {
                            if (userEmail.isEmpty())
                                RESULT_CODE = USER_ERROR_EMPTY_EMAIL;
                            if (userPassword.isEmpty())
                                RESULT_CODE = USER_ERROR_EMPTY_PASSWORD;
                            if (!userConfirmPassword.equalsIgnoreCase(userPassword))
                                RESULT_CODE = USER_ERROR_PASSWORD_NOT_MATCH;
                            if (userPassword.length() < 6)
                                RESULT_CODE = USER_ERROR_PASSWORD_SHORT;
                            if (!UtilityManager.isEmailVaild(userEmail))
                                RESULT_CODE = USER_ERROR_EMAIL_BAD_FORMAT;
                        }

                        return RESULT_CODE;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (integer.equals(USER_ERROR_EMPTY_EMAIL)) {
                            editTextUserEmail.setError("Email cannot be empty");
                        } else if (integer.equals(USER_ERROR_EMPTY_PASSWORD)) {
                            editTextPassword.setError("Password cannot be empty");
                        } else if (integer.equals(USER_ERROR_PASSWORD_NOT_MATCH)) {
                            editTextConfirmPassword.setError("Password don't match");
                        } else if (integer.equals(USER_ERROR_PASSWORD_SHORT)) {
                            editTextPassword.setError("Password should be more than 6 letters");
                        } else if (integer.equals(USER_ERROR_EMAIL_BAD_FORMAT)) {
                            editTextUserEmail.setError("Please check email id");
                        } else if (integer.equals(REGISTRATION_FAILURE)) {
                            button.setProgress(-1);
                            Log.i(TAG, "Registration failed");
                        }
                    }
                });
    }

    //To get the response of registration from server
    public Subscription receiveRegistrationResponse(String response, Activity activity, ActionProcessButton actionProcessButton) {
        Observable<String> stringObservable = Observable.just(response);
        return stringObservable
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String response) {
                        if (response.equalsIgnoreCase(ConstantManager.REGISTRATION_SUCCESS_MESSAGE)) {
                            actionProcessButton.setProgress(100);
                            SharedPreferences sharedPreferences = activity.getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(ConstantManager.PREF_TITLE_USER_EMAIL, emailAddress);
                            editor.apply();
                            activity.startActivity(new Intent(activity, AccountDetailsActivity.class));
                            activity.finish();
                        } else {
                            actionProcessButton.setProgress(-1);
                            Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
