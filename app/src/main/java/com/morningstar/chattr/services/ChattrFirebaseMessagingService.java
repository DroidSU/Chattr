package com.morningstar.chattr.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.morningstar.chattr.R;
import com.morningstar.chattr.activities.ChatActivity;
import com.morningstar.chattr.events.NewChatReceivedEvent;
import com.morningstar.chattr.managers.ChatManager;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.pojo.ChatItem;

import org.greenrobot.eventbus.EventBus;

public class ChattrFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MessagingService";

    private String sender_username;
    private String receiver_username;
    private String chatBody;
    private String chatId;
    private String chattrBoxId;
    private String date;

    private ChatItem chatItem;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sender_username = remoteMessage.getData().get("sender");
        receiver_username = remoteMessage.getData().get("receiver");
        chatBody = remoteMessage.getData().get("message");
        chatId = remoteMessage.getData().get("chatId");
        chattrBoxId = remoteMessage.getData().get("chattrBoxId");
        date = remoteMessage.getData().get("date");

        createNewChatItem(sender_username, receiver_username, chatBody, chatId, chattrBoxId);
//        sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), sender_username, receiver_username);
        EventBus.getDefault().post(new NewChatReceivedEvent());
    }

    private void createNewChatItem(String sender_username, String receiver_username, String chatBody, String chatId, String chattrBoxId) {
        try {
            ChatManager chatManager = new ChatManager();
            chatItem = chatManager.createChatItemInChattrBox(chatId, chattrBoxId, chatBody, date, false, sender_username);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void sendNotification(String title, String body, String sender, String receiver) {
        //when the user clicks on the notification
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantManager.FRIEND_USERNAME, sender);
        bundle.putString(ConstantManager.INITIATOR_ACTIVITY, TAG);
        intent.putExtra(ConstantManager.BUNDLE_EXTRAS, bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //69 is the unique notification id
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 69, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            String id = getResources().getString(R.string.default_notification_channel_id);
//            String name = "Chattr";
//            String description = "Chattr Notifications";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//
//            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
//            notificationChannel.setDescription(description);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.GREEN);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setChannelId(getResources().getString(R.string.default_notification_channel_id))
                    .build();
        }

        notificationManager.notify("NewChat", 69, notification);
    }
}
