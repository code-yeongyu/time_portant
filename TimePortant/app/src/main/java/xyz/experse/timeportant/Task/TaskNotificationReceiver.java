package xyz.experse.timeportant.Task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xyz.experse.timeportant.MainActivity;
import xyz.experse.timeportant.R;

/**
 * Created by experse on 17. 8. 18.
 */

public class TaskNotificationReceiver extends BroadcastReceiver {
    public TaskNotificationReceiver(){
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(context.getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        String text_display = "10분뒤에 \""+title+"\" 예약 하셨습니다."+"\n이 앱을 사용하시는 이유를 잊지 마세요!";
        Notification.Builder builder = new Notification.Builder(context.getApplicationContext());
        builder.setContentText(text_display);
        builder.setSmallIcon(R.mipmap.icon_launcher).setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(title).setContentText("계획하신 일을 할 시간이 되셨습니다.")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
        Notification.BigTextStyle style = new Notification.BigTextStyle(builder);
        style.bigText(text_display);
        style.setSummaryText("TimePortant");
        builder.setStyle(style);
        notificationmanager.notify(1, builder.build());
    }
}