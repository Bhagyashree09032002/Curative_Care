package com.curativecare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

    public static boolean wasScreenOn = true;
    Long time, pretime;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("LOB","onReceive");
        pretime = System.currentTimeMillis();
        if(UserData.count >=0){
            time = System.currentTimeMillis();
            if(time-pretime > 500) {
                UserData.count = 0;
            }else{
                UserData.count = UserData.count + 1;
            }
        }

        if(UserData.count>4){
            //LockService.wakeUp();
            UserData.count = 0;
            Message msg = LocService.handler.obtainMessage();
            LocService.handler.sendMessage(msg);

        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // do whatever you need to do here
            wasScreenOn = false;
            Log.e("LOB","wasScreenOn"+wasScreenOn);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // and do whatever you need to do here
            wasScreenOn = true;

        }else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Log.e("LOB","userpresent");
            Log.e("LOB","wasScreenOn"+wasScreenOn);
            /*String url = "http://www.stackoverflow.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);*/
        }
    }
}