package xyz.experse.timeportant.Task;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import xyz.experse.timeportant.MainActivity;
import xyz.experse.timeportant.R;

/**
 * Created by experse on 17. 8. 18.
 */

public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "noti")
                        .setSmallIcon(R.mipmap.icon_launcher)
                        .setContentTitle(title)
                        .setContentText("10분뒤에 \""+title+"\" 예약 하셨습니다."+"\n이 앱을 사용하시는 이유를 잊지 마세요!");

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    "noti", "noti", NotificationManager.IMPORTANCE_HIGH);

            mNotificationManager.createNotificationChannel(mChannel);

        }
        mNotificationManager.notify(1, mBuilder.build());
    }
}