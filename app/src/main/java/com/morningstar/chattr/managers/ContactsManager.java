/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.morningstar.chattr.activities.MainActivity;
import com.morningstar.chattr.models.ContactsModel;
import com.morningstar.chattr.pojo.Contacts;
import com.morningstar.chattr.receivers.ContactSyncReceiver;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.realm.Realm;

public class ContactsManager {

    private static final String TAG = "ContactsManager";
    private static final String PROGRESS_PERCENT = "PROGRESS_PERCENT";

    public static void syncContacts(Context context) {
        getPhoneContacts(context);
    }

    private static void getPhoneContacts(Context context) {
        int totalContacts = 0;
        int currentContactCount = 0;
        int progressPercent = 0;

        ArrayList<String> contactNumberArraylist = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        String myNo = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, "NONE");

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            totalContacts = cursor.getCount();

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
                            String contactNumber = RegexManager.removeCountryCode(
                                    phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));

                            if (contactNumber != null && !contactNumber.equals(myNo)) {
                                contactsModel.setContactNumber(contactNumber);
                                String result = addContactsToDb(contactsModel);

                                if (result != null)
                                    contactNumberArraylist.add(result);

                                progressPercent = (currentContactCount / totalContacts) * 100;

                                Intent broadcastIntent = new Intent();
                                broadcastIntent.setAction(ContactSyncReceiver.ACTION_RESP);
                                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                                broadcastIntent.putExtra(PROGRESS_PERCENT, progressPercent);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
                            }
                        }
                        phoneCursor.close();
                    }
                }
            }
        } else {
            Log.i(TAG, "Contacts could not be fetched");
        }

        if (cursor != null) {
            cursor.close();
        }

        syncWithFirebase(contactNumberArraylist, context);
    }

    private static String addContactsToDb(ContactsModel contactsModel) {
        boolean b = false;
        try (Realm realm = Realm.getDefaultInstance()) {
            if (realm.where(Contacts.class).equalTo("contactNumber", contactsModel.getContactNumber()).findAll().size() == 0) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Contacts contacts = realm.createObject(Contacts.class);
                        contacts.setContactNumber(contactsModel.getContactNumber());
                        contacts.setContactName(contactsModel.getContactName());
                        contacts.setAdded(false);
                        contacts.setContactID(contactsModel.getContactID());
                    }
                });

                b = true;
            }
        } catch (Exception e) {
            Log.i(TAG, "Realm failed: " + e.getMessage());
        }

        if (b)
            return contactsModel.getContactNumber();
        else
            return null;
    }

    private static void syncWithFirebase(ArrayList<String> arrayList, Context context) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_PHONE_NUMBERS_TABLE);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String mobNumber : arrayList) {
                    if (dataSnapshot.child(mobNumber).exists()) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            Contacts contacts = realm.where(Contacts.class).equalTo(ConstantManager.CONTACT_NUMBER, mobNumber).findFirst();
                            if (contacts != null) {
                                try {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            contacts.setAdded(true);
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.i(TAG, "Updating sync status failed");
                                }
                            }
                        }
                    }
                }

                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
