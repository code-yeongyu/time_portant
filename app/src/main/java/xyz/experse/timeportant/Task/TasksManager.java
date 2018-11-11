package xyz.experse.timeportant.Task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import xyz.experse.timeportant.db.TaskContract;
import xyz.experse.timeportant.db.TaskDBHelper;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static java.util.Calendar.getInstance;

/**
 * Created by experse on 17. 8. 17.
 */

class TasksManager {
    enum timeValue{
        HOUR, MINUTE
    }
    @SuppressWarnings("WeakerAccess")
    enum proper{
        PASS, TIME_OVERLAP, TIME_TOO_FAST, TITLE_OVERLAP
    }

    private final static long ONE_MINUTE = 60000;
    private TaskDBHelper mHelper;
    private ArrayList<String> taskTitle;
    private ArrayList<Long> taskTime;
    //Syncing alert with DB
    private boolean addAlert(Context context, String title, long time) {
        int id = getID(context, title);
        long long_time = time;
        if(time - (ONE_MINUTE * 10) <= Calendar.getInstance().getTimeInMillis() && time - (ONE_MINUTE * 4) <= Calendar.getInstance().getTimeInMillis()) {
            return false;
        }
        if (time - (ONE_MINUTE * 10) >= Calendar.getInstance().getTimeInMillis()) {
            AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent notification_intent = new Intent(context.getApplicationContext(), TaskNotificationReceiver.class);
            notification_intent.putExtra("title", title);

            PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), id, notification_intent, 0);
            if (Build.VERSION.SDK_INT >= 23) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time - (ONE_MINUTE * 10), sender);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, time - (ONE_MINUTE * 10), sender);
            }
        }
        //10
        if (time - (ONE_MINUTE * 4) >= Calendar.getInstance().getTimeInMillis()) {
            AlarmManager am_dialog_4 = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            Intent dialog = new Intent(context.getApplicationContext(), TaskDialogReceiver.class);
            dialog.putExtra("title", title);
            dialog.putExtra("time", long_time);

            PendingIntent sender_dialog_4 = PendingIntent.getBroadcast(context.getApplicationContext(), TasksManager.getID(context, title) - (10 + 4), dialog, 0);

            am_dialog_4.setInexactRepeating(AlarmManager.RTC_WAKEUP, time - (ONE_MINUTE * 4), ONE_MINUTE, sender_dialog_4);
        }
        return true;
    }
    private void removeAlertWithID(Context context, int id) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), TaskNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, FLAG_UPDATE_CURRENT);
        if (sender != null) {
            sender.cancel();
            am.cancel(sender);
        }
        cancelDialogAlert(context, id, 4);
    }
    void cancelDialogAlert(Context context, int id, int remain){

        AlarmManager am_dialog = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent_dialog = new Intent(context.getApplicationContext(), TaskDialogReceiver.class);
        PendingIntent sender_dialog = PendingIntent.getBroadcast(context.getApplicationContext(), id-(10+remain), intent_dialog, PendingIntent.FLAG_UPDATE_CURRENT);

        if (sender_dialog != null) {
            sender_dialog.cancel();
            am_dialog.cancel(sender_dialog);
        }
    }
    private void editAlertWithID(Context context, int id) {
        removeAlertWithID(context, id);
        addAlert(context, getTitle(context, id), getTimeInMillis(context, id));
    }
    //Alert managing
    private proper isProper(Context context, String title, long time) {

        if(time - (ONE_MINUTE * 15) <= Calendar.getInstance().getTimeInMillis() && time - (ONE_MINUTE * 4) <= Calendar.getInstance().getTimeInMillis()) {//if requested time is less than current time + 15
            return proper.TIME_TOO_FAST;
        } else {
            for (int i = 0; i < getTaskTimeArray(context, false).size(); i++) {
                if (getTaskTimeArray(context, true).get(i) == time) {//checks overlap time
                    return proper.TIME_OVERLAP;
                } else if (getTaskTitleArray(context, true).get(i).equals(title)) {//checks overlap title
                    return proper.TITLE_OVERLAP;
                }
            }
            return proper.PASS;
        }
    }

    void autoRemove(Context context) {
        mHelper = new TaskDBHelper(context);
        Cursor cursor = mHelper.getReadableDatabase()
                .query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.TASK_TITLE,
                                TaskContract.TaskEntry.TASK_TIME},
                        null, null, null, null, null);
        while (cursor.moveToNext()) {
            Calendar cal = getInstance();
            cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TIME)));
            int task_day = cal.get(Calendar.DAY_OF_YEAR);
            int actual_day = getInstance().get(Calendar.DAY_OF_YEAR);
            if (task_day != actual_day) {
                removeTask(context, cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)));
            }
        }
        cursor.close();
        mHelper.close();
    }

    proper addTask(Context context, String task_title, long task_time) {
        switch (isProper(context, task_title, task_time)){
            case TIME_TOO_FAST :
                return proper.TIME_TOO_FAST;
            case TIME_OVERLAP :
                return proper.TIME_OVERLAP;
            case TITLE_OVERLAP :
                return proper.TITLE_OVERLAP;
            default :
                mHelper = new TaskDBHelper(context);
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.TASK_TITLE, task_title);
                values.put(TaskContract.TaskEntry.TASK_TIME, task_time + "");
                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                        null,
                        values,
                        SQLiteDatabase.CONFLICT_REPLACE);//set text of task
                db.close();
                addAlert(context, task_title, task_time);
                return proper.PASS;
        }
    }
    void removeTask(Context context, int id) {
        mHelper = new TaskDBHelper(context);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE, TaskContract.TaskEntry._ID + "=?", new String[]{id+""});
        db.close();
        removeAlertWithID(context, id);
    }
    proper editTask(Context context, int id, String task_title, long task_time) {
        switch (isProper(context, task_title, task_time)){
            case TIME_TOO_FAST :
                return proper.TIME_TOO_FAST;
            case TIME_OVERLAP :
                if(getTimeInMillis(context, id) != task_time){
                    return proper.TIME_OVERLAP;
                }//Todo : fix this
            case TITLE_OVERLAP :
                if(!getTitle(context, id).equals(task_title)) {
                    return proper.TITLE_OVERLAP;
                }
            default :
                mHelper = new TaskDBHelper(context);
                SQLiteDatabase db = mHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.TASK_TITLE, task_title);
                values.put(TaskContract.TaskEntry.TASK_TIME, task_time + "");
                db.update(TaskContract.TaskEntry.TABLE, values, TaskContract.TaskEntry._ID + "=?", new String[]{id+""});
                db.close();
                editAlertWithID(context, id);
                return proper.PASS;
        }
    }
    //DB managing
    private void defineTasksToArray(Context context) {
        mHelper = new TaskDBHelper(context);
        taskTitle = new ArrayList<>();
        taskTime = new ArrayList<>();
        Cursor cursor = mHelper.getReadableDatabase()
                .query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.TASK_TITLE,
                                TaskContract.TaskEntry.TASK_TIME},
                        null, null, null, null, null);

        while (cursor.moveToNext()) {
            int task_title = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TITLE);
            int task_time = cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TIME);

            taskTitle.add(cursor.getString(task_title));
            Calendar cal = getInstance();
            cal.setTimeInMillis(cursor.getLong(task_time));
            taskTime.add(cursor.getLong(task_time));
        }
        cursor.close();
        mHelper.close();
    }
    ArrayList<String> getTaskTitleArray(Context context, Boolean isAlreadyDefined) {
        if(!isAlreadyDefined) {
            defineTasksToArray(context);
        }
        return taskTitle;
    }
    ArrayList<Long> getTaskTimeArray(Context context, Boolean isAlreadyDefined) {
        if(!isAlreadyDefined) {
            defineTasksToArray(context);
        }
        return taskTime;
    }
    //getting Tasks in array
    static int getID(Context context, String task_title) {
        TaskDBHelper mHelper = new TaskDBHelper(context);
        int id = 0;
        Cursor cursor = mHelper.getReadableDatabase()
                .query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.TASK_TITLE,
                                TaskContract.TaskEntry.TASK_TIME},
                        null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TITLE)).equals(task_title)){
                id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            }
        }
        cursor.close();
        mHelper.close();
        return id;
    }
    static String getTitle(Context context, int id) {
        TaskDBHelper mHelper = new TaskDBHelper(context);
        String title = "";
        Cursor cursor = mHelper.getReadableDatabase()
                .query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.TASK_TITLE,
                                TaskContract.TaskEntry.TASK_TIME},
                        null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)) == id){
                title = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TITLE));
                break;
            }
        }
        cursor.close();
        mHelper.close();
        return title;
    }
    static long getTimeInMillis(Context context, int id) {
        TaskDBHelper mHelper = new TaskDBHelper(context);
        long time = 0;
        Cursor cursor = mHelper.getReadableDatabase()
                .query(TaskContract.TaskEntry.TABLE,
                        new String[]{TaskContract.TaskEntry._ID,
                                TaskContract.TaskEntry.TASK_TITLE,
                                TaskContract.TaskEntry.TASK_TIME},
                        null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID)) == id){
                time = cursor.getLong(cursor.getColumnIndex(TaskContract.TaskEntry.TASK_TIME));
                break;
            }
        }
        cursor.close();
        mHelper.close();
        return time;
    }
    //get from DB
    static int getTimeFromMillis(long millis, timeValue wanted) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        if (wanted == timeValue.HOUR){
            return cal.get(Calendar.HOUR_OF_DAY);
        } else {
            return cal.get(Calendar.MINUTE);
        }
    }
}