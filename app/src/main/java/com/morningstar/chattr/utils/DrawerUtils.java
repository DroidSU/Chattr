/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.util.Objects;

import androidx.appcompat.widget.Toolbar;

public class DrawerUtils {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        PrimaryDrawerItem drawerItemProfile = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.mipmap.ic_default_user).withName(R.string.string_profile);
        PrimaryDrawerItem drawerItemRecentChats = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.drawable.ic_chat).withName(R.string.string_recent_chats);
        PrimaryDrawerItem drawerItemGroups = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.mipmap.ic_group).withName(R.string.string_groups);
        PrimaryDrawerItem drawerItemAllContacts = new PrimaryDrawerItem().withIdentifier(0).withIcon(R.mipmap.ic_contacts).withName(R.string.string_all_contacts);

        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem().withName(Objects.requireNonNull(firebaseUser).getDisplayName()).withEmail(firebaseUser.getEmail());

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
