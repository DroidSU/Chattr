package com.morningstar.chattr.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morningstar.chattr.R;
import com.morningstar.chattr.adapters.RecentChatsRecyclerAdapter;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.pojo.ChatItem;
import com.morningstar.chattr.pojo.ChattrBox;
import com.morningstar.chattr.pojo.Contacts;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class RecentChatsFragment extends Fragment {

    @BindView(R.id.simple_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.simple_no_item_textview)
    TextView textViewNoItem;
    private View view;
    private Context context;
    private RealmResults<ChattrBox> chattrBoxRealmResults;
    private ArrayList<ChatItem> chatItemArrayList;
    private ArrayList<String> senderNames;
    private Realm realm;
    private String myNum;
    private SharedPreferences sharedPreferences;
    private RecentChatsRecyclerAdapter recyclerAdapter;

    public RecentChatsFragment() {
        //Empty constructor
    }

    public static RecentChatsFragment newInstance(Context context) {
        RecentChatsFragment recentChatsFragment = new RecentChatsFragment();
        recentChatsFragment.context = context;
        return recentChatsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_simple_reusable_recycler, container, false);
        ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        myNum = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, null);
        senderNames = new ArrayList<>();
        chatItemArrayList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getAllRecentChats();

        if (senderNames.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            textViewNoItem.setVisibility(View.GONE);
            recyclerAdapter = new RecentChatsRecyclerAdapter(getContext(), senderNames, chatItemArrayList);
            recyclerView.setAdapter(recyclerAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            textViewNoItem.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void getAllRecentChats() {
        chattrBoxRealmResults = realm.where(ChattrBox.class)
                .isNotNull(ChattrBox.LAST_MESSAGE_ID)
                .or()
                .isNotEmpty(ChattrBox.LAST_MESSAGE_ID)
                .findAll();

        for (ChattrBox chattrBox : chattrBoxRealmResults) {
            String chatId = chattrBox.getLastMessageId();
            String username;
            ChatItem chatItem = realm.where(ChatItem.class).equalTo(ChatItem.ID, chatId).findFirst();
            chatItemArrayList.add(chatItem);
            if (chattrBox.getSender_username().equalsIgnoreCase(myNum))
                username = chattrBox.getReceiver_username();
            else
                username = chattrBox.getSender_username();

            Contacts contact = realm.where(Contacts.class).equalTo(Contacts.CONTACT_USERNAME, username).findFirst();
            if (contact != null) {
                if (contact.getContactName() != null)
                    senderNames.add(contact.getContactName());
                else
                    senderNames.add(username);
            } else
                senderNames.add(username);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null && !realm.isClosed())
            realm.close();
    }
}
