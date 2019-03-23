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

    public ChatItem createChatItemInChattrBox(String chatId, String chattrBoxId, String chatBody, String time, long timeStamp, boolean isGroup, String senderUsername) {
        try (Realm realm = Realm.getDefaultInstance()) {
            ChattrBox chattrBox = realm.where(ChattrBox.class).equalTo(ChattrBox.CHATTRBOX_ID, chattrBoxId).findFirst();

            chatIdRealmList = new RealmList<>();
            if (chattrBox != null) {
                if (chatId.equals("-1"))
                    chatId = PrimaryKeyManager.getPrimaryKeyForChatItem(chattrBox.getSender_username());
            }

            String finalChatId = chatId;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    chatItem = realm.createObject(ChatItem.class, finalChatId);
                    chatItem.setChatBody(chatBody);
                    chatItem.setTime(time);
                    chatItem.setIsGroup(isGroup);
                    chatItem.setChattrBoxId(chattrBoxId);
                    chatItem.setSenderUsername(senderUsername);
                    chatItem.setChatTimeStamp(timeStamp);
                }
            });

            if (chattrBox != null) {
                try {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            chatIdRealmList = chattrBox.getChatIds();
                            RealmList<String> newList = new RealmList<>();
                            newList.addAll(chatIdRealmList);
                            newList.add(finalChatId);
                            chattrBox.setChatIds(newList);
                            chattrBox.setLastMessageId(finalChatId);
                            realm.copyToRealmOrUpdate(chattrBox);
                        }
                    });
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }
            } else {
                Log.i(TAG, "ELSE LOGGED");
            }


        }

        return chatItem;
    }

    public void sendIndividualMessage(String chattrBoxId, String chatId, String chatBody, String sender_username, String receiver_username, String time, long timeStamp) {
        getConnection();
        JSONObject chatObject = new JSONObject();
        try {
            chatObject.put("chattrBoxId", chattrBoxId);
            chatObject.put("chatId", chatId);
            chatObject.put("chatBody", chatBody);
            chatObject.put("sender_username", sender_username);
            chatObject.put("receiver_username", receiver_username);
            chatObject.put("time", time);
            chatObject.put("timeStamp", timeStamp);

            if (socket == null) {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
                socket.connect();
            }
            socket.emit(ConstantManager.SEND_CHAT_MESSAGE_EVENT, chatObject);
            Log.i(TAG, String.valueOf(timeStamp));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
