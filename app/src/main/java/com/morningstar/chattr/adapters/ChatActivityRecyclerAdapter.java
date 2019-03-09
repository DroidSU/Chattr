/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.morningstar.chattr.R;
import com.morningstar.chattr.pojo.ChatItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivityRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int USER_CHAT = 0;
    private final int FRIEND_CHAT = 1;
    private final int HEADER = 2;
    private View view;
    private Context context;
    private ArrayList<ChatItem> chatItemArrayList;
    private String user_username;
    private String friend_username;

    public ChatActivityRecyclerAdapter(Context context, ArrayList<ChatItem> chatItemArrayList, String user_username, String friend_username) {
        this.context = context;
        this.chatItemArrayList = chatItemArrayList;
        this.user_username = user_username;
        this.friend_username = friend_username;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == USER_CHAT) {
            view = LayoutInflater.from(context).inflate(R.layout.user_chat_box, parent, false);
            return new UserChatAreaViewHolder(view);
        } else if (viewType == FRIEND_CHAT) {
            view = LayoutInflater.from(context).inflate(R.layout.friend_chat_box, parent, false);
            return new FriendChatAreaViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.layout_sticky_header_item, parent, false);
            return new StickyHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatItemArrayList.get(position);
        if (chatItem.getSenderUsername().equalsIgnoreCase(friend_username)) {
            FriendChatAreaViewHolder friendChatAreaViewHolder = (FriendChatAreaViewHolder) holder;
            friendChatAreaViewHolder.textViewFriendChatArea.setText(chatItem.getChatBody());
        } else {
            UserChatAreaViewHolder userChatAreaViewHolder = (UserChatAreaViewHolder) holder;
            userChatAreaViewHolder.textViewUserChatArea.setText(chatItem.getChatBody());
        }
    }

    @Override
    public int getItemCount() {
        return chatItemArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String sender_type = chatItemArrayList.get(position).getSenderUsername();
        if (sender_type.equalsIgnoreCase(user_username)) {
            return FRIEND_CHAT;
        } else if (sender_type.equalsIgnoreCase(friend_username))
            return USER_CHAT;
        else
            return HEADER;
    }

    class UserChatAreaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserChatArea;

        UserChatAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserChatArea = itemView.findViewById(R.id.user_chat_area_textView);
        }
    }

    class FriendChatAreaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFriendChatArea;

        FriendChatAreaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFriendChatArea = itemView.findViewById(R.id.friend_chat_area_textView);
        }
    }

    class StickyHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewStickyHeader;

        StickyHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewStickyHeader = itemView.findViewById(R.id.stickyHeaderTextView);
        }
    }
}
