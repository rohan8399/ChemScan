package com.example.eathealthy.Common;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Helper {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final String[] perms = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    public static void t(Context context, String display) {
        Toast.makeText(context, display, Toast.LENGTH_LONG).show();
    }
    public static String getRandomString(final int sizeOfRandomString) {
        StringBuilder sb = new StringBuilder(sizeOfRandomString);

        try {
            // Create a secure random number generator using the SHA1PRNG algorithm
            SecureRandom srg = SecureRandom.getInstance("SHA1PRNG");

            // Get 10 random bytes
            int i = srg.nextInt(1000000);
//            s= String.valueOf(i);

            for (int j = 0; j < sizeOfRandomString; j++)
                sb.append(AB.charAt(srg.nextInt(AB.length())));


        } catch (NoSuchAlgorithmException e) {
        }
        return String.valueOf(sb);
    }
    public static void l(String logdisplay) {
        Log.d("eathealthy", logdisplay);
    }
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {
                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Helper.l("update_statut" + "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Helper.l("update_statut" + "" + e.getMessage());
                }
            }
        }
        Helper.l("update_statut" + "Network is unavailable : FALSE ");
        return false;
    }


}
