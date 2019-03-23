/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.morningstar.chattr.R;
import com.morningstar.chattr.managers.ConstantManager;
import com.morningstar.chattr.managers.NetworkManager;
import com.morningstar.chattr.utils.DrawerUtils;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @BindView(R.id.mainActivityToolbar)
    Toolbar toolbar;
    @BindView(R.id.mainActivityRootLayout)
    LinearLayout rootLayout;

    private String mobileNumber = "";
    private String userName = "";

    private SharedPreferences sharedPreferences;
    private String userInstanceId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar = findViewById(R.id.mainActivityToolbar);
        rootLayout = findViewById(R.id.mainActivityRootLayout);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chattr");

        DrawerUtils.getDrawer(this, toolbar);

        getValueFromPreference();

        if (userName == null || userName.isEmpty() || mobileNumber == null || mobileNumber.isEmpty() || mobileNumber.length() != 10) {
            Intent intent = new Intent(MainActivity.this, RegisterUsingEmail.class);
            startActivity(intent);
            finish();
        }

        NetworkManager.isConnectToSocket();
        NetworkManager.changeLoggedInStatus(this, ConstantManager.ON);

        if (userInstanceId != null) {
            FirebaseDatabase.getInstance().getReference(ConstantManager.FIREBASE_USERS_TABLE)
                    .child(userName).child(ConstantManager.FIREBASE_USER_INSTANCE_ID).setValue(userInstanceId);
        }
    }

    private void getValueFromPreference() {
        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        mobileNumber = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_MOBILE, null);
        userName = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_USERNAME, null);
        userInstanceId = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_TOKEN, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
//                logOutUser();
                break;
        }
        return true;
    }

//    private void logOutUser() {
//        databaseReference = FirebaseDatabase.getInstance().getReference().child(ConstantManager.FIREBASE_PHONE_NUMBERS_TABLE)
//                .child(mobileNumber);
//        databaseReference.child(ConstantManager.FIREBASE_IS_ONLINE_COLUMN)
//                .setValue(false).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                databaseReference.child(ConstantManager.FIREBASE_IS_LOGGED_IN_COLUMN).setValue(false)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                firebaseAuth.signOut();
//                                Intent intent = new Intent(MainActivity.this, LoginUsingEmailActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Snackbar snackbar = Snackbar.make(rootLayout, "Could not sign out at the moment", Snackbar.LENGTH_SHORT);
//                        snackbar.setAction("TRY AGAIN", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                logOutUser();
//                            }
//                        });
//                        snackbar.show();
//                    }
//                });
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NetworkManager.changeLoggedInStatus(this, ConstantManager.OFF);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetworkManager.disconnectFromSocket();
    }
}
