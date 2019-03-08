/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import android.util.Log;

import com.morningstar.chattr.pojo.ChatItem;
import com.morningstar.chattr.pojo.ChattrBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatManager {

    private static final String TAG = "ChatManager";
    private Socket socket;
    private String sender_username;
    private String receiver_username;
    private String chatBody;
    private String chatId;
    private ChattrBox chattrBox;
    private ChatItem chatItem;
    private RealmList<String> chatIdRealmList;

    private void getConnection() {
        socket = NetworkManager.getConnectedSocket();
    }

    public ChattrBox createChattrBox(String sender_username, String receiver_username) {
        this.sender_username = sender_username;
        this.receiver_username = receiver_username;

        try (Realm realm = Realm.getDefaultInstance()) {
            String chattrBoxId = PrimaryKeyManager.getObjectKeyForChattrBox(sender_username, receiver_username);
            chattrBox = realm.where(ChattrBox.class).equalTo(ChattrBox.CHATTRBOX_ID, chattrBoxId).findFirst();
            if (chattrBox == null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        chattrBox = realm.createObject(ChattrBox.class, chattrBoxId);
                        chattrBox.setReceiver_username(receiver_username);
                        chattrBox.setSender_username(sender_username);
                    }
                });
            }
            return chattrBox;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return null;
        }
    }

    public ChatItem createChatItemInChattrBox(String chattrBoxId, String chatBody, String date, boolean isGroup) {
        try (Realm realm = Realm.getDefaultInstance()) {
            ChattrBox chattrBox = realm.where(ChattrBox.class).equalTo(ChattrBox.CHATTRBOX_ID, chattrBoxId).findFirst();
            chatIdRealmList = new RealmList<>();
            if (chattrBox != null) {
                chatId = PrimaryKeyManager.getPrimaryKeyForChatItem(chattrBox.getSender_username());
            }

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    chatItem = realm.createObject(ChatItem.class, chatId);
                    chatItem.setChatBody(chatBody);
                    chatItem.setDate(date);
                    chatItem.setIsGroup(false);
                    chatItem.setChattrBoxId(chattrBoxId);
                }
            });

            if (chattrBox != null) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        chatIdRealmList = chattrBox.getChatIds();
                        chatIdRealmList.add(chatId);
                        chattrBox.setChatIds(chatIdRealmList);
                    }
                });
            }
        }

        return chatItem;
    }

    public void sendIndividualMessage(String chattrBoxId, String chatId, String chatBody, String sender_username, String receiver_username, String date) {
        getConnection();
        JSONObject chatObject = new JSONObject();
        try {
            chatObject.put("chattrBoxId", chattrBoxId);
            chatObject.put("chatId", chatId);
            chatObject.put("chatBody", chatBody);
            chatObject.put("sender_username", sender_username);
            chatObject.put("receiver_username", receiver_username);
            chatObject.put("date", date);

            if (socket == null) {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
                socket.connect();
            }
            socket.emit(ConstantManager.SEND_CHAT_MESSAGE_EVENT, chatObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
