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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.morningstar.chattr.R;
import com.morningstar.chattr.adapters.ChatActivityRecyclerAdapter;
import com.morningstar.chattr.adapters.ContactsRecyclerAdapter;
import com.morningstar.chattr.events.FriendDetailsFetchedEvent;
import com.morningstar.chattr.events.NewChatReceivedEvent;
import com.morningstar.chattr.managers.ChatManager;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.DateTimeManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.managers.PrimaryKeyManager;
import com.morningstar.chattr.pojo.ChatItem;
import com.morningstar.chattr.pojo.ChattrBox;
import com.morningstar.chattr.pojo.Friend;
import com.morningstar.chattr.services.ChattrFirebaseMessagingService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private String friend_username;
    private String my_user_Name;
    private String friend_user_number;
    private String my_number;
    private String currentDate;
    private String currentTime;
    private long chatTimeStamp;
    private Socket socket;

    private RealmResults<ChatItem> chatItemRealmResults;
    private ArrayList<ChatItem> chatItemArrayList;

    private Realm mRealm;
    private Friend friend;
    private ChattrBox chattrBox;
    private ChatItem chatItem;
    private String chattrboxid;
    private ChatActivityRecyclerAdapter chatActivityRecyclerAdapter;
    private String newChatId;
    private String initiator;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        isConnectedToSocket = NetworkManager.isConnectToSocket();
        mRealm = Realm.getDefaultInstance();
        chatItemArrayList = new ArrayList<>();
        EventBus.getDefault().register(this);
        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);

        getIntentExtras();
        getValueFromPrefs();

        currentDate = DateTimeManager.getCurrentDateAsString();
        chattrboxid = PrimaryKeyManager.getObjectKeyForChattrBox(my_user_Name, friend_username);

        editor = sharedPreferences.edit();
        editor.putString(ConstantManager.PREF_OPENED_CHAT_ID, chattrboxid);
        editor.apply();

        chattrBox = mRealm.where(ChattrBox.class).equalTo(ChattrBox.CHATTRBOX_ID, chattrboxid).findFirst();

        socket = NetworkManager.getConnectedSocket();
        getFriendObjectFromRealm();

        if (friend == null) {
            //creating friend object
            if (friend_user_number != null)
                NetworkManager.sendNumberForFriendDetails(friend_user_number);
            else
                NetworkManager.sendUsernameForFrientDetails(friend_username);

            socket.on(ConstantManager.FRIEND_CREATED, getFriendDetailsResponse());
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        setUpRecycler();
    }

    private void setUpRecycler() {
        getAllChatItems();
        if (chatItemArrayList.size() > 0) {
            textViewNoMessages.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            chatActivityRecyclerAdapter = new ChatActivityRecyclerAdapter(this, chatItemArrayList, my_user_Name, friend_username);
            recyclerView.setAdapter(chatActivityRecyclerAdapter);
//            chatActivityRecyclerAdapter.notifyDataSetChanged();
        } else {
            textViewNoMessages.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void getAllChatItems() {
        chatItemArrayList.clear();
        try (Realm realm = Realm.getDefaultInstance()) {
            chatItemRealmResults = realm.where(ChatItem.class).equalTo(ChatItem.CHATTR_BOX_ID, chattrboxid).sort(ChatItem.CHAT_TIMESTAMP, Sort.DESCENDING).findAll();
            chatItemArrayList.addAll(chatItemRealmResults);
        }
    }


    @OnClick(R.id.imageView_send_message)
    public void sendChatMessage() {
        if (TextUtils.isEmpty(editTextMessageArea.getText().toString())) {
            Toast.makeText(this, "Empty message cannot be sent", Toast.LENGTH_SHORT).show();
        } else {
            String chatBody = editTextMessageArea.getText().toString();
            currentTime = DateTimeManager.getCurrentTimeAsString();
            chatTimeStamp = DateTimeManager.getCurrentSystemDate();
            if (NetworkManager.hasInternetAccess()) {
                ChatManager chatManager = new ChatManager();
                chattrBox = chatManager.createChattrBox(my_user_Name, friend_username);
                //send -1 to get a new chat item
                chatItem = chatManager.createChatItemInChattrBox("-1", chattrBox.getChattrBoxId(), chatBody, currentTime, chatTimeStamp, false, my_user_Name);
                chatManager.sendIndividualMessage(chattrBox.getChattrBoxId(), chatItem.getId(), chatBody, my_user_Name, friend_username, currentTime, chatTimeStamp);
                recyclerView.invalidate();
                setUpRecycler();
                editTextMessageArea.setText("");
            } else {
                Toast.makeText(this, "Not online!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFriendObjectFromRealm() {
        if (friend_user_number != null)
            friend = mRealm.where(Friend.class).equalTo(Friend.FRIEND_MOB_NUMBER, friend_user_number).findFirst();
        else
            friend = mRealm.where(Friend.class).equalTo(Friend.FRIEND_USERNAME, friend_username).findFirst();
    }

    private Emitter.Listener getFriendDetailsResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    friend_user_number = jsonObject.getString("friendMobNumber");
                    friend_username = jsonObject.getString("friendUsername");

                    textViewUserName.post(new Runnable() {
                        @Override
                        public void run() {
                            textViewUserName.setText(friend_username);
                        }
                    });

                    EventBus.getDefault().post(new FriendDetailsFetchedEvent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void getValueFromPrefs() {
        my_number = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, "");
        my_user_Name = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_USERNAME, "");
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(ConstantManager.BUNDLE_EXTRAS);
        initiator = bundle.getString(ConstantManager.INITIATOR_ACTIVITY);
        if (initiator != null) {
            if (initiator.equalsIgnoreCase(ContactsRecyclerAdapter.TAG)) {
                friend_user_number = bundle.getString(ConstantManager.CONTACT_NUMBER);
                friend_username = bundle.getString(ConstantManager.CONTACT_USERNAME);
            } else if (initiator.equalsIgnoreCase(ChattrFirebaseMessagingService.TAG))
                friend_username = bundle.getString(ConstantManager.FRIEND_USERNAME);

            textViewUserName.setText(friend_username);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void createFriendObject(FriendDetailsFetchedEvent friendDetailsFetchedEvent) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                friend = mRealm.createObject(Friend.class, friend_user_number);
                friend.setFriendUsername(friend_username);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void newMessageReceived(NewChatReceivedEvent newChatReceivedEvent) {
//        setUpRecycler();
        recyclerView.invalidate();
        setUpRecycler();
    }

    @OnClick(R.id.imageView_go_back)
    public void goBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ChatActivity.this, AllContactsActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null && !mRealm.isClosed())
            mRealm.close();
        EventBus.getDefault().unregister(this);
        editor = sharedPreferences.edit();
        editor.putString(ConstantManager.PREF_OPENED_CHAT_ID, null);
        editor.apply();
        super.onDestroy();
    }
}
