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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.pojo.ChatItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChatActivity extends AppCompatActivity {

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
    private String user_Name;
    private String my_user_Name;
    private String user_number;
    private String my_number;

    private RealmResults<ChatItem> chatItemRealmResults;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        isConnectedToSocket = NetworkManager.isConnectToSocket();
        realm = Realm.getDefaultInstance();

        getIntentExtras();
        getValueFromPrefs();
        updateUi();

        textViewUserName.setText(user_Name);
    }

    private void getValueFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        my_number = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, "");
        my_user_Name = "Me";
    }

    private void updateUi() {
        chatItemRealmResults = realm.where(ChatItem.class)
                .beginGroup()
                .beginGroup()
                .equalTo(ChatItem.SENDER_NUMBER, user_number)
                .and()
                .equalTo(ChatItem.RECEIVER_NUMBER, my_number)
                .endGroup()
                .or()
                .beginGroup()
                .equalTo(ChatItem.RECEIVER_NUMBER, user_number)
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
        user_Name = bundle.getString(ConstantManager.CONTACT_NAME);
        user_number = bundle.getString(ConstantManager.CONTACT_NUMBER);
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
        if (realm != null && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }
}
