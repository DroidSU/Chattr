/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.pojo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Friend extends RealmObject {

    public static String FRIEND_MOB_NUMBER = "friendMobNumber";
    public static String FRIEND_USERNAME = "friendUsername";

    @PrimaryKey
    private String friendMobNumber;
    private String friendUsername;

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getFriendMobNumber() {
        return friendMobNumber;
    }

    public void setFriendMobNumber(String friendMobNumber) {
        this.friendMobNumber = friendMobNumber;
    }
}
