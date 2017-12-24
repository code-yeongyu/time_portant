package xyz.experse.timeportant.Task;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import xyz.experse.timeportant.R;

public class EditTask extends AppCompatActivity {
    String task_title;
    int task_id;
    int task_hour;
    int task_minute;
    TasksManager tm = new TasksManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        task_id = getIntent().getIntExtra("task_id", 0);
        task_title = tm.getTitle(getApplicationContext(), task_id);
        task_hour = TasksManager.getTimeFromMillis(
                tm.getTimeInMillis(getApplicationContext(), task_id)
                , TasksManager.timeValue.HOUR);
        task_minute = TasksManager.getTimeFromMillis(tm.getTimeInMillis(getApplicationContext(), task_id), TasksManager.timeValue.MINUTE);
        ((EditText) findViewById(R.id.task_title_edit)).setText(task_title);
        ((TextView) findViewById(R.id.task_time_display)).setText(task_hour + " : " + task_minute);
    }
    long task_time;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @SuppressWarnings("deprecation")
    public void onTimeChangeButtonClicked(final View v) {

        new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {//TimePickerDialog popup
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                if (Build.VERSION.SDK_INT > 23) {//for latest versions
                    task_hour = timePicker.getHour();
                    task_minute = timePicker.getMinute();
                } else {//for old version support
                    task_hour = timePicker.getCurrentHour();
                    task_minute = timePicker.getCurrentMinute();
                }

                TextView tv = (TextView) findViewById(R.id.task_time_display);
                tv.setText(task_hour + " : " + task_minute);

            }
        }, task_hour, task_minute, false).show();//set time and schedule
    }

    public void onConfirmButtonClicked(View v) {
        EditText et = (EditText) findViewById(R.id.task_title_edit);
        task_title = et.getText() + "";
        Log.d(et.getText() + "", et.getText() + "");

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), task_hour, task_minute, 0);
        cal.set(Calendar.HOUR_OF_DAY, task_hour);
        cal.set(Calendar.MINUTE, task_minute);
        task_time = cal.getTimeInMillis();

        switch (tm.editTaskFromDB(getApplicationContext(), task_id, et.getText() + "", task_time)) {
            case TIME_TOO_FAST:
                Toast.makeText(getApplicationContext(), "알림이 울릴수 있는 시간으로 설정되어야 합니다.", Toast.LENGTH_LONG).show();
                Log.d("1", "default");
                break;
            case TIME_OVERLAP:
                Log.d("2", "default");
                Toast.makeText(getApplicationContext(), "같은 시간에 지정된 작업이 있습니다.", Toast.LENGTH_LONG).show();
                break;
            case TITLE_OVERLAP:
                Log.d("3", "default");
                Toast.makeText(getApplicationContext(), "같은 이름으로 지정된 작업이 있습니다.", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.d("default", "default");
                finish();
        }
    }

    public void onRemoveButtonClicked(View v) {
        tm.removeTaskFromDB(getApplicationContext(), task_id);
        finish();
    }
}
