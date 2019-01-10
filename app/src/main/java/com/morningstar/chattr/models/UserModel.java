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

    public UserModel(String userUserName, String userName, String userSurname, String userEmail, String userMobile, String userDPUrl) {
        this.userUserName = userUserName;
        this.userName = userName;
        this.userSurname = userSurname;
        this.userEmail = userEmail;
        this.userMobile = userMobile;
        this.userDPUrl = userDPUrl;
    }
}
