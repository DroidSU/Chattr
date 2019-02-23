/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.chattr.managers;

import io.socket.client.Socket;

/**
 * SocketManager manages socket connections.
 * It also provide listeners for connection status.
 */
public class SocketManager {
    public static final String TAG = "SocketManager";

    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTED = 3;

    public static final String CONNECTING = "Connecting";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";

    private static SocketManager newInstance;
    private Socket socket;

    private SocketManager() {
    }

    public synchronized static SocketManager getInstance() {
        if (newInstance == null) {
            newInstance = new SocketManager();
        }
        return newInstance;
    }


}
