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
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.pojo.Contacts;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsRecyclerViewHolder> {

    private View view;
    private Context context;
    private ArrayList<Contacts> contactsArrayList;

    private DatabaseReference databaseReference;

    public ContactsRecyclerAdapter(Context context, ArrayList<Contacts> contactsArrayList) {
        this.context = context;
        this.contactsArrayList = contactsArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference(ConstantManager.FIREBASE_USERS_TABLE);
    }

    @NonNull
    @Override
    public ContactsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_item_contact, null);
        return new ContactsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsRecyclerViewHolder holder, int position) {
        holder.textView.setText(contactsArrayList.get(position).getContactName());
    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    class ContactsRecyclerViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textView;
        RadioButton radioButton;

        ContactsRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImageViewProfileImage);
            textView = itemView.findViewById(R.id.textViewProfileName);
            radioButton = itemView.findViewById(R.id.radioOnlineStatus);
        }
    }
}
