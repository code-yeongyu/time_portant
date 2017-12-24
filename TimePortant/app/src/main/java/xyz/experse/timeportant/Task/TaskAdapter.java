package xyz.experse.timeportant.Task;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import xyz.experse.timeportant.R;

/**
 * Created by experse on 17. 8. 10.
 */

class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    List<TaskLists> tasks = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTaskTitle;
        private final TextView mTaskTime;

        ViewHolder(final View v){
            super(v);
            mTaskTitle = v.findViewById(R.id.task_title);
            mTaskTime = v.findViewById(R.id.task_when);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//If click each tasks
                    Intent intent = new Intent(v.getContext(), EditTask.class);
                    intent.putExtra("task_id", new TasksManager().getID(v.getContext(), mTaskTitle.getText()+""));
                    //intent.putExtra("task_hour", getTime(mTaskTime.getText()+"", TasksManager.timeValue.HOUR));
                    //intent.putExtra("task_minute", getTime(mTaskTime.getText()+"", TasksManager.timeValue.MINUTE));
                    v.getContext().startActivity(intent);
                }
            });
        }
        TextView getTaskString() {
            return mTaskTitle;
        }
        TextView getTaskTime() {
            return mTaskTime;
        }


    }

    TaskAdapter() {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String title = tasks.get(position).task_title;
        holder.getTaskString().setText(title);
        String when = TasksManager.getTimeFromMillis(tasks.get(position).task_time, TasksManager.timeValue.HOUR)
                + " : "
                + TasksManager.getTimeFromMillis(tasks.get(position).task_time, TasksManager.timeValue.MINUTE)
                +" 에 알림 울릴 예정";
        holder.getTaskTime().setText(when);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

}
