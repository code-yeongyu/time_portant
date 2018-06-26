package xyz.experse.timeportant.Task;

/**
 * Created by experse on 17. 8. 18.
 */

public class TaskLists {
    public String task_title;
    public long task_time;
    public TaskLists newInstance(String taskTitle, long taskTime){
        task_title = taskTitle;
        task_time = taskTime;
        return TaskLists.this;
    }
}
