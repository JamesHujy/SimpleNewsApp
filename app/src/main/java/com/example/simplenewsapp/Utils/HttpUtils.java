package com.example.simplenewsapp.Utils;


import android.content.Context;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class HttpUtils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isConnected()) {
            return false;
        }

        return true;
    }
}
