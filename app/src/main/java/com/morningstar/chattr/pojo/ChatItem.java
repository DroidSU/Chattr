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

public class ChatItem extends RealmObject {

    public static String ID = "id";
    public static String CHAT_BODY = "chatBody";
    public static String IS_GROUP = "isGroup";
    public static String SENDER_NUMBER = "senderNumber";
    public static String RECEIVER_NUMBER = "receiverNumber";
    public static String DATE = "date";
    public static String CHATTR_BOX_ID = "chattrBoxId";

    @PrimaryKey
    private long id;

    private String chatBody;
    private boolean isGroup;
    private String date;
    private String chattrBoxId;

    public String getChattrBoxId() {
        return chattrBoxId;
    }

    public void setChattrBoxId(String chattrBoxId) {
        this.chattrBoxId = chattrBoxId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChatBody() {
        return chatBody;
    }

    public void setChatBody(String chatBody) {
        this.chatBody = chatBody;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        isGroup = group;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
