/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ContactSyncReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP = "com.morningstar.intent.action.MESSAGE_PROCESSED";
    private static final String PROGRESS_PERCENT = "PROGRESS_PERCENT";

    public ContactSyncReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int progressCount = 0;
        if (intent.getExtras() != null)
            progressCount = intent.getExtras().getInt(PROGRESS_PERCENT);
    }
}
