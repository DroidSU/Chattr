package com.morningstar.chattr.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.morningstar.chattr.R;
import com.morningstar.chattr.activities.ChatActivity;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.pojo.ChatItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecentChatsRecyclerAdapter extends RecyclerView.Adapter<RecentChatsRecyclerAdapter.RecentChatsViewHolder> {
    public static final String TAG = "RecentChatsRecycler";

    private Context context;
    private View view;

    private ArrayList<String> usernames;
    private ArrayList<String> names;
    private ArrayList<ChatItem> chatItemArrayList;

    public RecentChatsRecyclerAdapter(Context context, ArrayList<String> usernames, ArrayList<String> names, ArrayList<ChatItem> chatItemArrayList) {
        this.context = context;
        this.usernames = usernames;
        this.names = names;
        this.chatItemArrayList = chatItemArrayList;
    }

    @NonNull
    @Override
    public RecentChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.recent_chat_fragment_item, parent, false);
        return new RecentChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentChatsViewHolder holder, int position) {
        holder.contactDp.setVisibility(View.GONE);
        String name = names.get(position);
        if (name.equalsIgnoreCase("")) {
            holder.textViewContactInitial.setText(String.valueOf(usernames.get(position).charAt(0)).toUpperCase());
            holder.textViewContactName.setText(usernames.get(position));
        } else {
            holder.textViewContactInitial.setText(String.valueOf(names.get(position).charAt(0)).toUpperCase());
            holder.textViewContactName.setText(names.get(position));
        }

        ChatItem chatItem = chatItemArrayList.get(position);
        if (chatItem.getSenderUsername().equals(usernames.get(position)))
            holder.textViewLastMessage.setText(chatItem.getChatBody());
        else {
            String message = "You: " + chatItem.getChatBody();
            holder.textViewLastMessage.setText(message);
        }
        holder.textViewLastMessageTime.setText(chatItem.getTime());
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantManager.INITIATOR_ACTIVITY, TAG);
                bundle.putString(ConstantManager.FRIEND_USERNAME, usernames.get(position));
                context.startActivity(new Intent(context, ChatActivity.class).putExtra(ConstantManager.BUNDLE_EXTRAS, bundle));
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    class RecentChatsViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContactInitial, textViewContactName, textViewLastMessage, textViewUnreadCount, textViewLastMessageTime;
        LinearLayout rootLayout;
        CircleImageView contactDp;

        RecentChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContactInitial = itemView.findViewById(R.id.recent_chats_contact_initial);
            textViewContactName = itemView.findViewById(R.id.recent_chats_contact_name);
            textViewLastMessage = itemView.findViewById(R.id.recent_chats_contact_last_message);
            textViewUnreadCount = itemView.findViewById(R.id.recent_chats_unread_count);
            textViewLastMessageTime = itemView.findViewById(R.id.recent_chats_last_time);
            rootLayout = itemView.findViewById(R.id.recent_chats_root_layout);
            contactDp = itemView.findViewById(R.id.recent_chats_contact_dp);
        }
    }
}
