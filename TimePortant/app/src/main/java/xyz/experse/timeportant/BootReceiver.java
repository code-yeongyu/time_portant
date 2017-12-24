package xyz.experse.timeportant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xyz.experse.timeportant.Task.TaskDialogReceiver;
import xyz.experse.timeportant.Task.TaskNotificationReceiver;

/**
 * Created by experse on 17. 8. 22.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context,Intent intent){
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent i = new Intent(context, TaskNotificationReceiver.class);
            Intent in = new Intent(context, TaskDialogReceiver.class);
            context.startService(i);
            context.startService(in);
        }
    }
}
