/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.models;

import com.google.firebase.database.PropertyName;

import androidx.annotation.Keep;

@Keep
public class UserModel {

    private String userName;
    private String userSurname;
    private String userEmail;
    private String userMobile;
    private String userDPUrl;

    public UserModel() {
        //Empty constructor
    }

    public UserModel(String userName, String userSurname, String userEmail, String userMobile, String userDPUrl) {
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
        this.userDPUrl = userDPUrl;
    }

    @PropertyName("Name")
    public String getUserName() {
        return userName;
    }

    @PropertyName("Name")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @PropertyName("Surname")
    public String getUserSurname() {
        return userSurname;
    }

    @PropertyName("Surname")
    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    @PropertyName("Email")
    public String getUserEmail() {
        return userEmail;
    }

    @PropertyName("Email")
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @PropertyName("Mobile Number")
    public String getUserMobile() {
        return userMobile;
    }

    @PropertyName("Mobile Number")
    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    @PropertyName("Dp Url")
    public String getUserDPUrl() {
        return userDPUrl;
    }

    @PropertyName("Dp Url")
    public void setUserDPUrl(String userDPUrl) {
        this.userDPUrl = userDPUrl;
    }
}
