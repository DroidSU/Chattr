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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.morningstar.chattr.R;
import com.morningstar.chattr.adapters.ChatActivityRecyclerAdapter;
import com.morningstar.chattr.events.FriendDetailsFetchedEvent;
import com.morningstar.chattr.managers.ChatManager;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.managers.PrimaryKeyManager;
import com.morningstar.chattr.pojo.ChatItem;
import com.morningstar.chattr.pojo.ChattrBox;
import com.morningstar.chattr.pojo.Friend;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
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
    private ArrayList<ChatItem> chatItemArrayList;

    private Realm mRealm;
    private Friend friend;
    private ChattrBox chattrBox;
    private ChatItem chatItem;
    private String chattrboxid;
    private ChatActivityRecyclerAdapter chatActivityRecyclerAdapter;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        isConnectedToSocket = NetworkManager.isConnectToSocket();
        mRealm = Realm.getDefaultInstance();
        chatItemArrayList = new ArrayList<>();
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

        setUpRecycler();
    }

    private void setUpDatabaseListener() {
        FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_CHATS_DB).child(chattrboxid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ChatItem newChatItem = dataSnapshot.getValue(ChatItem.class);
                        if (newChatItem != null) {
                            String chat_body = newChatItem.getChatBody();
                            String date = newChatItem.getDate();
                            boolean isGroup = newChatItem.isGroup();
                            String senderUsername = newChatItem.getSenderUsername();
                            if (senderUsername.equalsIgnoreCase(friend_user_name))
                                saveValuesToRealm(chat_body, date, isGroup, senderUsername);
                        } else {
                            Toast.makeText(ChatActivity.this, "Could not save message", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void saveValuesToRealm(String chat_body, String date, boolean isGroup, String senderUsername) {
        ChatManager chatManager = new ChatManager();
        chatManager.createChatItemInChattrBox(chattrboxid, chat_body, date, isGroup, senderUsername);
        recyclerView.invalidate();
        setUpRecycler();
    }

    private void setUpRecycler() {
        textViewNoMessages.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAllChatItems();
        chatActivityRecyclerAdapter = new ChatActivityRecyclerAdapter(this, chatItemArrayList, my_user_Name, friend_user_name);
        recyclerView.setAdapter(chatActivityRecyclerAdapter);
    }

    private void getAllChatItems() {
        chatItemRealmResults = mRealm.where(ChatItem.class).equalTo(ChatItem.CHATTR_BOX_ID, chattrboxid).findAll();
        chatItemArrayList.addAll(chatItemRealmResults);
    }


    @OnClick(R.id.imageView_send_message)
    public void sendChatMessage() {
        if (TextUtils.isEmpty(editTextMessageArea.getText().toString())) {
            Toast.makeText(this, "Empty message cannot be sent", Toast.LENGTH_SHORT).show();
        } else {
            String chatBody = editTextMessageArea.getText().toString();
            if (NetworkManager.hasInternetAccess()) {
                ChatManager chatManager = new ChatManager();
                chattrBox = chatManager.createChattrBox(my_user_Name, friend_user_name);
                chatItem = chatManager.createChatItemInChattrBox(chattrBox.getChattrBoxId(), chatBody, "01/01/01", false, friend_user_name);
                chatManager.sendIndividualMessage(chattrBox.getChattrBoxId(), chatItem.getId(), chatBody, my_user_Name, friend_user_name, "01/01/01");
                editTextMessageArea.setText("");
            } else {
                Toast.makeText(this, "Not online!", Toast.LENGTH_SHORT).show();
            }
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
                    setUpDatabaseListener();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void getValueFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        my_number = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, "");
        my_user_Name = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_USERNAME, "");
    }

    private void updateUi() {
        chattrboxid = PrimaryKeyManager.getObjectKeyForChattrBox(my_user_Name, friend_user_name);

        chattrBox = mRealm.where(ChattrBox.class).equalTo(ChattrBox.CHATTRBOX_ID, chattrboxid).findFirst();

        if (chattrBox != null) {
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
