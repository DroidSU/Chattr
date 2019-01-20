/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.models;

import com.google.firebase.database.PropertyName;

public class UserStatusModel {

    private boolean isLoggedIn;
    private boolean isOnline;
    private String userStatus;

    public UserStatusModel(boolean isLoggedIn, boolean isOnline, String userStatus) {
        this.isLoggedIn = isLoggedIn;
        this.isOnline = isOnline;
        this.userStatus = userStatus;
    }

    @PropertyName("Status")
    public String getUserStatus() {
        return userStatus;
    }

    @PropertyName("Status")
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    @PropertyName("Is_LoggedIn")
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    @PropertyName("Is_LoggedIn")
    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    @PropertyName("Is_Online")
    public boolean isOnline() {
        return isOnline;
    }

    @PropertyName("Is_Online")
    public void setOnline(boolean online) {
        isOnline = online;
    }
}
