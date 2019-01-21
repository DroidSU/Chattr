/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.models.ContactsModel;
import com.morningstar.chattr.pojo.Contacts;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.realm.Realm;

public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";
    private final int READ_CONTACTS_REQUEST_CODE = 1;

    private ProgressBar progressBar;
    private TextView textViewProgressStatus;

    private int totalContactCount = 0;
    private int currentContactCount = 0;

    private String permissionItem = "";

    private Realm realm;
    private ArrayList<ContactsModel> contactsModelArrayList;
    private Contacts contacts;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.loadingActivityProgressBar);
        textViewProgressStatus = findViewById(R.id.loadingActivityProgressStatus);

        contactsModelArrayList = new ArrayList<>();
        realm = Realm.getDefaultInstance();

        permissionItem = Manifest.permission.READ_CONTACTS;
        checkForPermission();
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionItem)) {
                //Explain to the user asynchronously why the permission is needed
                //Don't wait for user's response. After user sees explain try again to request for permission
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permissionItem}, READ_CONTACTS_REQUEST_CODE);
            }
        } else {
            getPhoneContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getPhoneContacts();
        }
    }

    private void getPhoneContacts() {
        progressBar.setProgress(0);
        textViewProgressStatus.setText("Fetching contacts from phone...");

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        totalContactCount = cursor != null ? cursor.getCount() : 0;

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                currentContactCount += 1;
                ContactsModel contactsModel = new ContactsModel();
                String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contactsModel.setContactID(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
                contactsModel.setContactName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?", new String[]{contactID}, null);

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            contactsModel.setContactNumber(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        }
                        phoneCursor.close();
                    }
                }
                progressBar.setProgress((currentContactCount / totalContactCount) * 100);
                contactsModelArrayList.add(contactsModel);
            }
        } else {
            Toast.makeText(this, "Contacts could not be fetched", Toast.LENGTH_SHORT).show();
            progressBar.setProgress(100);
        }

        if (cursor != null) {
            cursor.close();
        }

        addContactsToDb();
    }

    private void addContactsToDb() {
        textViewProgressStatus.setText("Looking for chattrs you know...");
        progressBar.setProgress(0);
        currentContactCount = 0;
        contacts = new Contacts();
        for (ContactsModel model : contactsModelArrayList) {
            currentContactCount += 1;
            try {
                if (realm.where(Contacts.class).equalTo("contactNumber", model.getContactNumber()).findAll().size() == 0) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.createObject(Contacts.class);
                            contacts.setContactNumber(model.getContactNumber());
                            contacts.setContactName(model.getContactName());
                            contacts.setAdded(false);
                            contacts.setContactID(model.getContactID());
                            realm.copyToRealm(contacts);
                        }
                    });
                }
            } catch (Exception e) {
                Log.i(TAG, "Realm failed: " + e.getMessage());
                Toast.makeText(this, "Could not sync contacts", Toast.LENGTH_SHORT).show();
            }

            progressBar.setProgress((currentContactCount / totalContactCount) * 100);
        }


        Toast.makeText(this, "All contacts have been added", Toast.LENGTH_SHORT).show();

        syncWithFirebase();
    }

    private void syncWithFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_PHONE_NUMBERS_TABLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String number = snapshot.getKey();
                        Contacts contacts = realm.where(Contacts.class).equalTo(ConstantManager.CONTACT_NUMBER, number).findFirst();
                        if (contacts != null) {
                            contacts.setAdded(true);
                            try {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(contacts);
                                        Toast.makeText(LoadingActivity.this, "Added: " + number, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                Log.i(TAG, "Sync failed: " + e.getMessage());
                            }
                        }
                    }
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
