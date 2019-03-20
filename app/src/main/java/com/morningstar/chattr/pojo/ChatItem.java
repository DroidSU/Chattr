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
    public static String TIME = "time";
    public static String CHATTR_BOX_ID = "chattrBoxId";
    public static String CHAT_SENDER = "sender";
    public static String CHAT_TIMESTAMP = "chatTimeStamp";

    @PrimaryKey
    private String id;

    private String chatBody;
    private boolean isGroup;
    private String time;
    private String chattrBoxId;
    private String senderUsername;
    private long chatTimeStamp;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getChatTimeStamp() {
        return chatTimeStamp;
    }

    public void setChatTimeStamp(long chatTimeStamp) {
        this.chatTimeStamp = chatTimeStamp;
    }

    public String getChattrBoxId() {
        return chattrBoxId;
    }

    public void setChattrBoxId(String chattrBoxId) {
        this.chattrBoxId = chattrBoxId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}
