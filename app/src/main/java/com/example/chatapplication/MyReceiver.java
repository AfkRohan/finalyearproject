package com.example.chatapplication;

import android.app.usage.NetworkStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

//        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
//            boolean noConnectivity = intent.getBooleanExtra(
//                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false
//            );
//            if(noConnectivity) {
//                // No internet
//            }else{
//                // There is internet
//                // send notification of messages and miss calls.
//            }
//
//        }

//        throw new UnsupportedOperationException("Not yet implemented");
    }
}