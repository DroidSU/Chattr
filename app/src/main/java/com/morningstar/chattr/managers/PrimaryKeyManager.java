/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import android.util.Log;

import com.morningstar.chattr.pojo.ChatItem;

import io.realm.Realm;
import io.realm.RealmResults;

public class PrimaryKeyManager {

    private static final String TAG = "PrimaryKeyManager";

    public static long getPrimaryKeyForChatItem() {
        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<ChatItem> realmResults = realm.where(ChatItem.class).findAll();
            return DateTimeManager.getCurrentSystemDate() * 1000 + realmResults.size();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return 0;
        }
    }
}
