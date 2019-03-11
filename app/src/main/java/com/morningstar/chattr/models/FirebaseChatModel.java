package com.morningstar.chattr.models;

import com.google.firebase.database.PropertyName;

public class FirebaseChatModel {

    @PropertyName("ChatId")
    private String chatId;
    @PropertyName("date")
    private String date;
    @PropertyName("message")
    private String message;
    @PropertyName("receiver_username")
    private String receiver_username;
    @PropertyName("sender_username")
    private String sender_username;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver_username() {
        return receiver_username;
    }

    public void setReceiver_username(String receiver_username) {
        this.receiver_username = receiver_username;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }
}
