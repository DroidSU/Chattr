/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.morningstar.chattr.managers.ConstantManager;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;

public class CustomApplication extends Application {
    public static final String TAG = "CustomApplication";

    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder().name("ChattrRealm.realm").build();
        Realm.setDefaultConfiguration(config);

        //Initialize and create image loader logic using glide
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return super.placeholder(ctx, tag);
            }
        });
    }

    public Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
            } catch (URISyntaxException e) {
                Log.i(TAG, e.getMessage());
            }
        }
        return socket;
    }
}
