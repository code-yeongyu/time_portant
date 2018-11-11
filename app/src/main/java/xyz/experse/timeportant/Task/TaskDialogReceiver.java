package xyz.experse.timeportant.Task;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by experse on 17. 8. 22.
 */

public class TaskDialogReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent popupIntent = new Intent(context, TaskAlert.class);
        String title = intent.getStringExtra("title");
        long time = intent.getLongExtra("time", 0);
        popupIntent.putExtra("title", title);
        popupIntent.putExtra("time", time);

        PendingIntent pi = PendingIntent.getActivity(context, TasksManager.getID(context, title), popupIntent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pi.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
