package xyz.xyz.xyz.smartapplocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Lomesh on 4/26/2016.
 */
public class AppLockerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, AppLockerService.class);
        context.startService(service);
        Log.d("AppLockerReceiver", "onHandleIntent:Receiver has started.");
    }
}
