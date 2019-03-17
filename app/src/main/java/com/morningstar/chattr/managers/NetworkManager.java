/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static Socket socket;

//    public static boolean isUserOnline(Context context) {
//        @SuppressLint("ServiceCast") ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        try {
//            NetworkInfo networkInfo = null;
//            if (connectivityManager != null) {
//                networkInfo = connectivityManager.getActiveNetworkInfo();
//            } else {
//                Log.i("NetworkManger", "Connectivity Manager is null");
//            }
//            return networkInfo != null && networkInfo.isConnectedOrConnecting();
//        } catch (NullPointerException exception) {
//            Log.i("NetworkManger", "Exception: " + exception.getMessage());
//            return false;
//        }
//    }

    //Checks if device has internet access
    //https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/27312494#27312494
    public static boolean hasInternetAccess() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnectToSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
                socket.connect();
                return true;
            } catch (Exception e) {
                Log.i(TAG, "Connection failed: " + e.getMessage());
                return false;
            }
        } else
            return true;
    }

    public static Socket getConnectedSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
                socket.connect();
                return socket;
            } catch (Exception e) {
                Log.i(TAG, "Connection failed: " + e.getMessage());
                return socket;
            }
        } else
            return socket;
    }

    public static void disconnectFromSocket() {
        if (socket != null) {
            socket.disconnect();
            socket = null;
        }
    }

    public static void sendNumberForFriendDetails(String mobNumber) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("friendNumber", mobNumber);
            if (socket == null) {
                try {
                    socket = IO.socket(ConstantManager.IP_LOCALHOST);
                    socket.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            socket.emit("sendFriendDetails", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void changeLoggedInStatus(Context context, String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(ConstantManager.PREF_TITLE_USER_USERNAME, "");
        JSONObject data = new JSONObject();
        try {
            data.put("displayName", username);
            if (socket == null) {
                socket = IO.socket(ConstantManager.IP_LOCALHOST);
                socket.connect();
            }
            if (status.equalsIgnoreCase(ConstantManager.OFF))
                socket.emit("statusOffline", data);
            else
                socket.emit("statusOnline", data);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void disconnectSocket() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
        }
    }

    public static void sendUsernameForFrientDetails(String friend_user_name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("friendUsername", friend_user_name);
            if (socket == null) {
                try {
                    socket = IO.socket(ConstantManager.IP_LOCALHOST);
                    socket.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
            socket.emit("sendFriendDetailsForUsername", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
