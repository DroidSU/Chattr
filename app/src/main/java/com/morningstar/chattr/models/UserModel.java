/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.models;

public class UserModel {

    private String userUserName;
    private String userName;
    private String userSurname;
    private String userEmail;
    private String userMobile;
    private String userDPUrl;
    private String userId;

    public UserModel() {
        //Empty constructor
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserModel(String userUserName, String userName, String userSurname, String userEmail, String userMobile, String userDPUrl) {
        this.userUserName = userUserName;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
        this.userDPUrl = userDPUrl;
    }

    public String getUserUserName() {
        return userUserName;
    }

    public void setUserUserName(String userUserName) {
        this.userUserName = userUserName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserDPUrl() {
        return userDPUrl;
    }

    public void setUserDPUrl(String userDPUrl) {
        this.userDPUrl = userDPUrl;
    }
}
