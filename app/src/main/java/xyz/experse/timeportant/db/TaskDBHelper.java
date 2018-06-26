package xyz.experse.timeportant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by experse on 17. 8. 7.
 */

public class TaskDBHelper extends SQLiteOpenHelper {
    public TaskDBHelper (Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String createTable =
                "CREATE TABLE " + TaskContract.TaskEntry.TABLE +
                        " ( " +
                        TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // id
                        TaskContract.TaskEntry.TASK_TITLE + " TEXT NOT NULL, " + // Title of the task
                        TaskContract.TaskEntry.TASK_TIME + " INTEGER" + //Time of the task
                        " );";
        db.execSQL(createTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }
}
