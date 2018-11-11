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

import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.experse.timeportant.MainActivity;
import xyz.experse.timeportant.R;

import static xyz.experse.timeportant.Task.TasksManager.getID;

public class TaskAlert extends AppCompatActivity {
    TasksManager tm;
    AlertDialog.Builder builder = null;
    AlertDialog dialog = null;
    String title;
    long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        tm = new TasksManager();

        title = getIntent().getStringExtra("title");
        time = getIntent().getLongExtra("time", 0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH시 mm분");

        Date d = new Date(time);
        String text = sdf.format(d);

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
                        tm.removeTask(getApplicationContext(), getID(getApplicationContext(), title));
                        tm.cancelDialogAlert(getApplicationContext(), getID(getApplicationContext(), title), 4);
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

        builder.setMessage(text+"에 "+title+"을 하기로 하셨습니다.\n일을 시작하시겠습니까?");
        dialog = builder.create();
        dialog.show();
    }

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