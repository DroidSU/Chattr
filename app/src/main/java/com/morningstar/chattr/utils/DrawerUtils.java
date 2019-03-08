/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.morningstar.chattr.R;
import com.morningstar.chattr.activities.AllContactsActivity;
import com.morningstar.chattr.activities.MainActivity;
import com.morningstar.chattr.activities.ProfileActivity;
import com.morningstar.chattr.managers.ConstantManager;

import androidx.appcompat.widget.Toolbar;

public class DrawerUtils {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_EMAIL, "");
        String displayName = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_USERNAME, "");

        PrimaryDrawerItem drawerItemProfile = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.drawable.ic_default_user_black_24dp).withName(R.string.string_profile);
        PrimaryDrawerItem drawerItemRecentChats = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.drawable.ic_chat).withName(R.string.string_recent_chats);
        PrimaryDrawerItem drawerItemGroups = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.drawable.ic_group_black_24dp).withName(R.string.string_groups);
        PrimaryDrawerItem drawerItemAllContacts = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.drawable.ic_contacts_black_24dp).withName(R.string.string_all_contacts);

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(displayName).withEmail(email);

        AccountHeader accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(activity)
                .addProfiles(profileDrawerItem)
                .build();

        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeaderBuilder)
                .withTranslucentStatusBar(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(drawerItemProfile, drawerItemRecentChats, drawerItemGroups, drawerItemAllContacts)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position==1){
                            Intent intent = new Intent(activity, ProfileActivity.class);
                            activity.startActivity(intent);
                        }
                        if (position == 2) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            activity.startActivity(intent);
                        }
                        if (position == 4) {
                            Intent intent = new Intent(activity, AllContactsActivity.class);
                            activity.startActivity(intent);
                        }

                        return true;
                    }
                })
                .build();
    }

}
