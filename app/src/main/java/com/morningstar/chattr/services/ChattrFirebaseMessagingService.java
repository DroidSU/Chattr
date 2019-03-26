package com.morningstar.chattr.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
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

import java.util.Objects;

import androidx.core.app.NotificationCompat;

public class ChattrFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "MessagingService";

    private String sender_username;
    private String receiver_username;
    private String chatBody;
    private String chatId;
    private String chattrBoxId;
    private String time;
    private long timeStamp;

    private ChatItem chatItem;
    private SharedPreferences sharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sender_username = remoteMessage.getData().get("sender");
        receiver_username = remoteMessage.getData().get("receiver");
        chatBody = remoteMessage.getData().get("message");
        chatId = remoteMessage.getData().get("chatId");
        chattrBoxId = remoteMessage.getData().get("chattrBoxId");
        time = remoteMessage.getData().get("time");
        timeStamp = Long.parseLong(Objects.requireNonNull(remoteMessage.getData().get("timeStamp")));
        Log.i(TAG, "" + timeStamp);

        createNewChatItem(sender_username, receiver_username, chatBody, chatId, chattrBoxId);
        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        String openedChatId = sharedPreferences.getString(ConstantManager.PREF_OPENED_CHAT_ID, null);
        if (openedChatId == null || !openedChatId.equals(chattrBoxId))
            sendNotification(remoteMessage);
        EventBus.getDefault().post(new NewChatReceivedEvent());
    }

    private void createNewChatItem(String sender_username, String receiver_username, String chatBody, String chatId, String chattrBoxId) {
        try {
            ChatManager chatManager = new ChatManager();
            chatItem = chatManager.createChatItemInChattrBox(this, chatId, chattrBoxId, chatBody, time, timeStamp, false, sender_username);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        //when the user clicks on the notification
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ConstantManager.FRIEND_USERNAME, remoteMessage.getData().get("sender"));
        bundle.putString(ConstantManager.INITIATOR_ACTIVITY, TAG);
        intent.putExtra(ConstantManager.BUNDLE_EXTRAS, bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //69 is the unique notification id
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 69, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri tone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ChattrFirebaseMessagingService.this, getResources().getString(R.string.default_notification_channel_id));
        builder.setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(remoteMessage.getData().get("body"))
                .setContentText(remoteMessage.getData().get("message"))
                .setSound(tone)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String id = getResources().getString(R.string.default_notification_channel_id);
            String name = "New Message";
            String description = "Chattr Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.setDescription(description);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setChannelId("channel_id");
//        }

        notificationManager.notify(Integer.parseInt(getResources().getString(R.string.default_notification_channel_id)), builder.build());
    }
}
