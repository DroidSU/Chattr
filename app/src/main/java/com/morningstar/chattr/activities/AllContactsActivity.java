/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.morningstar.chattr.R;
import com.morningstar.chattr.adapters.ContactsRecyclerAdapter;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.pojo.Contacts;
import com.morningstar.chattr.services.ContactSyncService;
import com.morningstar.chattr.utils.DrawerUtils;

import java.util.ArrayList;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class AllContactsActivity extends AppCompatActivity {

    private static final String TAG = "AllContacts";

    @BindView(R.id.allContactsToolbar)
    Toolbar toolbar;
    @BindView(R.id.allContactsRecycler)
    RecyclerView recyclerView;

    private ContactsRecyclerAdapter contactsRecyclerAdapter;
    private Realm realm;
    private DatabaseReference databaseReference;
    private ArrayList<Contacts> contactsArrayList;
    private boolean isConnectedToSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.all_chattrs));

        DrawerUtils.getDrawer(this, toolbar);

        initialiseVariables();
        getAvailableContacts();
        isConnectedToSocket = NetworkManager.isConnectToSocket();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerAdapter = new ContactsRecyclerAdapter(this, contactsArrayList);
        recyclerView.setAdapter(contactsRecyclerAdapter);
    }

    private void getAvailableContacts() {
        RealmResults<Contacts> realmResults = realm.where(Contacts.class).equalTo(ConstantManager.IS_CONTACT_ADDED, true).findAll();
        contactsArrayList.addAll(realmResults);
    }

    private void initialiseVariables() {
        realm = Realm.getDefaultInstance();
        contactsArrayList = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        NetworkManager.disconnectFromSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.all_contacts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_contact_refresh:
                refreshAllContacts();
                break;
        }

        return true;
    }

    private void refreshAllContacts() {
        startService(new Intent(AllContactsActivity.this, ContactSyncService.class));
    }
}
