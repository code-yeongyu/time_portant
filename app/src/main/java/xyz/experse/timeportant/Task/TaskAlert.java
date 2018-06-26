package xyz.experse.timeportant.Task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import xyz.experse.timeportant.MainActivity;
import xyz.experse.timeportant.R;

import static xyz.experse.timeportant.Task.TasksManager.getID;

public class TaskAlert extends AppCompatActivity {
    TasksManager tm;
    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;
    String title;
    int remain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tm = new TasksManager();

        Log.d("TaskAlert", "잘 받았다.");
        title = getIntent().getStringExtra("title");
        remain = getIntent().getIntExtra("remain", 4);
        Intent dialog_intent = new Intent(getApplicationContext(), TaskDialogReceiver.class);

        dialog_intent.putExtra("title", title);
        dialog_intent.putExtra("remain", 4);


        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        LinearLayout Linear = new LinearLayout(getApplicationContext());
        Linear.setBackgroundColor(0xff000000);
        setContentView(Linear);
        registReceive();
        builder = new AlertDialog.Builder(TaskAlert.this)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tm.removeTaskFromDB(getApplicationContext(), getID(getApplicationContext(), title));
                        tm.cancelDialogAlert(getApplicationContext(), getID(getApplicationContext(), title), remain);
                        dialogInterface.dismiss();
                        finish();
                    }
                })
        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        String gonna_do = "\n일을 시작하시겠습니까?";
        if(remain != 0)
            builder.setMessage(title + " 예약 하신 시간까지 "+remain+"분 남았습니다."+gonna_do);
        else if(remain == 0) {
            builder.setMessage(title + " 예약하신 시간입니다." + gonna_do);
            NotificationManager notificationmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            String text_display = "계획하신 일을 할 시간이 되셨습니다.\n이 앱을 사용하시는 이유를 잊지 마세요!";
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
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
        dialog = builder.create();
        dialog.show();
    }
        /*setContentView(R.layout.activity_task_alert);


        TextView task_label = (TextView) findViewById(R.id.task_label);

    }
    public void onTaskYes(View v){


    }
    public void onTaskNo(View v){
        finish();
    }*/

    public void registReceive() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
// TODO Auto-generated method stub

            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF) && dialog.isShowing()) {
                dialog.dismiss();
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}