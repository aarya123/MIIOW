package com.example.MIIOW;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UTILITIES {
    public static String CLIENT_TOKEN="qIlbtu8nxNSSFcIvgcrP8t97AuwwhW";
    public static String CLIENT_SECRET="gvvRp1THczVjF9PRhyCUcm6LVNjgUk";
    public static String ACCOUNT_KEY="ywdrXOJdA6pYydpGGGmIDTrM88ZVXW";
    public static String ACCOUNT_SECRET="k8WfVWexlQOKRSIgkJmU3H27Lc3ziY";
    public static String API_URL = "http://app.smartfile.com/api/2";
    public static String DOWNLOAD_LOC = "/sdcard/MIIOWdownloads";
    public static boolean isOnline(Context c)
    {
        // Get connection service
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get connection info
        NetworkInfo info = cm.getActiveNetworkInfo();
        // Check status
        if (info != null)
        {
            if (info.isConnected())
                return true;
        }
        return false;
    }
}
