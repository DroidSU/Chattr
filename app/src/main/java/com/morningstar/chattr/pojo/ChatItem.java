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

public class ChatItem extends RealmObject {

    public static String ID = "id";
    public static String CHAT_BODY = "chatBody";
    public static String IS_GROUP = "isGroup";
    public static String SENDER_NUMBER = "senderNumber";
    public static String RECEIVER_NUMBER = "receiverNumber";
    public static String DATE = "date";

    @PrimaryKey
    private long id;

    private String chatBody;
    private String senderNumber;
    private String receiverNumber;
    private boolean isGroup;
    private RealmList<String> groupParticipants;
    private String date;

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

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public RealmList<String> getGroupParticipants() {
        return groupParticipants;
    }

    public void setGroupParticipants(RealmList<String> groupParticipants) {
        this.groupParticipants = groupParticipants;
    }
}
