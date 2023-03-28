package com.hduy.app_lay_so.Controller;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class PrinterConnection {
    private Socket socket;

    public boolean connect(String ipAddress, int port) {
        boolean isConnected = false;

        try {
            socket = new Socket(ipAddress, port);
            isConnected = true;
        } catch (IOException e) {
            Log.e("PrinterConnection", "Error connecting to printer", e);
        }

        return isConnected;
    }

    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e("PrinterConnection", "Error disconnecting from printer", e);
        }
    }
}

