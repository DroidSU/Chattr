/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.morningstar.chattr.R;
import com.morningstar.chattr.receivers.ContactSyncReceiver;
import com.morningstar.chattr.services.ContactSyncService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.realm.Realm;

public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "LoadingActivity";
    private static final String PROGRESS_PERCENT = "PROGRESS_PERCENT";

    private final int READ_CONTACTS_REQUEST_CODE = 1;

    private ProgressBar progressBar;
    private TextView textViewProgressStatus;

    private String permissionItem = "";

    private Realm realm;
    private ContactSyncReceiver contactSyncReceiver = new ContactSyncReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                int progressPercent = intent.getExtras().getInt(PROGRESS_PERCENT);
                progressBar.setProgress(progressPercent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.loadingActivityProgressBar);
        textViewProgressStatus = findViewById(R.id.loadingActivityProgressStatus);

        realm = Realm.getDefaultInstance();

        permissionItem = Manifest.permission.READ_CONTACTS;
        IntentFilter intentFilter = new IntentFilter(ContactSyncReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(contactSyncReceiver, intentFilter);
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
            textViewProgressStatus.setText("Looking for chattrs you know...");
            Intent intent = new Intent(LoadingActivity.this, ContactSyncService.class);
            startService(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACTS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(LoadingActivity.this, ContactSyncService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }
}
