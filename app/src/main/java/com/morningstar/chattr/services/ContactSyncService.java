/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.services;

import android.app.IntentService;
import android.content.Intent;

import com.morningstar.chattr.managers.ContactsManager;

import androidx.annotation.Nullable;

public class ContactSyncService extends IntentService {

    public ContactSyncService() {
        super("ContactIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ContactsManager.syncContacts(this);
    }
}
