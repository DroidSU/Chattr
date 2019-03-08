/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.pojo;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ChattrBox extends RealmObject {

    public static String CHATTRBOX_ID = "chattrBoxId";
    public static String SENDER_USERNAME = "sender_username";
    public static String RECEIVER_USERNAME = "receiver_username";
    public static String CHATD_IDS = "chatIds";

    @PrimaryKey
    private String chattrBoxId;
    private String sender_username;
    private String receiver_username;
    private RealmList<String> chatIds;

    public String getChattrBoxId() {
        return chattrBoxId;
    }

    public void setChattrBoxId(String chattrBoxId) {
        this.chattrBoxId = chattrBoxId;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public String getReceiver_username() {
        return receiver_username;
    }

    public void setReceiver_username(String receiver_username) {
        this.receiver_username = receiver_username;
    }

    public RealmList<String> getChatIds() {
        return chatIds;
    }

    public void setChatIds(RealmList<String> chatIds) {
        this.chatIds = chatIds;
    }
}
