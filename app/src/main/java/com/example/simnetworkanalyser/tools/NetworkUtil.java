package com.example.simnetworkanalyser.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isMobileDataActive(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null
                && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE));
    }

    public static boolean isMobileDataConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null
                && (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                && activeNetwork.isConnected());
    }
}
