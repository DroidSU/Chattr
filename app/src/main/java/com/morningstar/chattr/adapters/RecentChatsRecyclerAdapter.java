package com.morningstar.chattr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.morningstar.chattr.R;
import com.morningstar.chattr.pojo.ChatItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecentChatsRecyclerAdapter extends RecyclerView.Adapter<RecentChatsRecyclerAdapter.RecentChatsViewHolder> {
    private Context context;
    private View view;

    private ArrayList<String> names;
    private ArrayList<ChatItem> chatItemArrayList;

    public RecentChatsRecyclerAdapter(Context context, ArrayList<String> names, ArrayList<ChatItem> chatItemArrayList) {
        this.context = context;
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

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    class RecentChatsViewHolder extends RecyclerView.ViewHolder {

        RecentChatsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
