package xyz.experse.timeportant.db;

import android.provider.BaseColumns;

/**
 * Created by experse on 17. 8. 7.
 */

public class TaskContract {
    public static final String DB_NAME = "xyz.experse.timeportant.db";
    public static final int DB_VERSION = 1;
    public class TaskEntry implements BaseColumns {
        public static final String TABLE  = "tasks";
        public static final String TASK_TITLE = "title";
        public static final String TASK_TIME = "time";
    }
}
