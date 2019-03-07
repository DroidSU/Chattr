/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.morningstar.chattr.R;
import com.morningstar.chattr.events.FriendDetailsFetchedEvent;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.pojo.ChatItem;
import com.morningstar.chattr.pojo.Friend;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    @BindView(R.id.toolbar_user_dp)
    CircleImageView circleImageViewUserImage;
    @BindView(R.id.toolbar_user_name)
    TextView textViewUserName;
    @BindView(R.id.toolbar_user_online_status)
    CircleImageView circleImageViewOnlineStatus;
    @BindView(R.id.all_messages_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.imageView_send_message)
    ImageView imageViewSendMessage;
    @BindView(R.id.editText_message_area)
    EditText editTextMessageArea;
    @BindView(R.id.imageView_go_back)
    ImageView imageViewBackButton;
    @BindView(R.id.textView_no_messages)
    TextView textViewNoMessages;

    private boolean isConnectedToSocket;
    private String friend_user_name;
    private String my_user_Name;
    private String friend_user_number;
    private String my_number;
    private Socket socket;

    private RealmResults<ChatItem> chatItemRealmResults;
    private Realm mRealm;
    private Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        isConnectedToSocket = NetworkManager.isConnectToSocket();
        mRealm = Realm.getDefaultInstance();
        EventBus.getDefault().register(this);

        getIntentExtras();
        getValueFromPrefs();
        updateUi();
        socket = NetworkManager.getConnectedSocket();
        getFriendObjectFromRealm();

        if (friend == null) {
            //creating friend object
            NetworkManager.sendNumberForFriendDetails(friend_user_number);
            socket.on(ConstantManager.FRIEND_CREATED, getFriendDetailsResponse());
        } else {
            Log.i(TAG, "Friend object exists");
        }

        textViewUserName.setText(friend_user_name);
    }

    @OnClick(R.id.imageView_send_message)
    public void sendChatMessage() {
        if (TextUtils.isEmpty(editTextMessageArea.getText().toString())) {
            Toast.makeText(this, "Empty message cannot be sent", Toast.LENGTH_SHORT).show();
        } else {
            
        }
    }

    private void getFriendObjectFromRealm() {
        friend = mRealm.where(Friend.class).equalTo(Friend.FRIEND_MOB_NUMBER, friend_user_number).findFirst();
    }

    private Emitter.Listener getFriendDetailsResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    friend_user_number = jsonObject.getString("friendMobNumber");
                    friend_user_name = jsonObject.getString("friendUsername");

                    EventBus.getDefault().post(new FriendDetailsFetchedEvent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void getValueFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        my_number = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, "");
        my_user_Name = "Me";
    }

    private void updateUi() {
        chatItemRealmResults = mRealm.where(ChatItem.class)
                .beginGroup()
                .beginGroup()
                .equalTo(ChatItem.SENDER_NUMBER, friend_user_number)
                .and()
                .equalTo(ChatItem.RECEIVER_NUMBER, my_number)
                .endGroup()
                .or()
                .beginGroup()
                .equalTo(ChatItem.RECEIVER_NUMBER, friend_user_number)
                .and()
                .equalTo(ChatItem.SENDER_NUMBER, my_number)
                .endGroup()
                .endGroup()
                .sort(ChatItem.DATE, Sort.DESCENDING)
                .findAll();

        if (chatItemRealmResults.size() > 0) {
            textViewNoMessages.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            textViewNoMessages.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(ConstantManager.BUNDLE_EXTRAS);
        friend_user_name = bundle.getString(ConstantManager.CONTACT_NAME);
        friend_user_number = bundle.getString(ConstantManager.CONTACT_NUMBER);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createFriendObject(FriendDetailsFetchedEvent friendDetailsFetchedEvent) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                friend = mRealm.createObject(Friend.class, friend_user_number);
                friend.setFriendUsername(friend_user_name);
            }
        });
    }

    @OnClick(R.id.imageView_go_back)
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
