package xyz.experse.timeportant.Task;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by experse on 17. 8. 22.
 */

public class TaskDialogReceiver extends BroadcastReceiver {
    TasksManager tm = new TasksManager();
    @Override
    @SuppressWarnings("deprecation")
    public void onReceive(Context context, Intent intent) {
        Intent popupIntent = new Intent(context, TaskAlert.class);
        String title = intent.getStringExtra("title");
        int remain = intent.getIntExtra("remain", 4);
        popupIntent.putExtra("title", title);
        popupIntent.putExtra("remain", remain);
        Log.d("onReceive", remain+"");

        PendingIntent pi = PendingIntent.getActivity(context, TasksManager.getID(context, title), popupIntent, PendingIntent.FLAG_ONE_SHOT);
        try {
            pi.send();
        }catch (Exception e){
            char c;
        }
    }
}
