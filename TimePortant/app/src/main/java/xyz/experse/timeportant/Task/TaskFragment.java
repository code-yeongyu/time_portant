package xyz.experse.timeportant.Task;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import xyz.experse.timeportant.R;

/**
 * Created by experse on 17. 8. 9.
 */

public class TaskFragment extends Fragment {

    TasksManager tm = new TasksManager();
    TaskAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, null);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter = new TaskAdapter();
        RecyclerView mRecyclerView;
        mRecyclerView = getActivity().findViewById(R.id.tasks_container);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        updateUI(mAdapter);
    }

    @Override
    public void onViewCreated(final View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final EditText taskEditText = new EditText(getActivity());
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("할 일 추가")
                        .setMessage("할 일의 이름을 입력해주세요.")
                        .setView(taskEditText)
                        .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                mInputMethodManager.hideSoftInputFromWindow(taskEditText.getWindowToken(), 0);
                                //hide keyboard
                                setTime(String.valueOf(taskEditText.getText()), mAdapter);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();
                dialog.show();
            }
        });

        //load & RecyclerView settings
    }
    @SuppressWarnings("deprecation")
    private void setTime(final String task_title, final TaskAdapter mAdapter) {
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {//TimePickerDialog popup
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Calendar cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                int hour, minute;
                if (Build.VERSION.SDK_INT > 23) {//for latest versions
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {//for old version support
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                switch (tm.addTaskToDB(getActivity(), task_title, cal.getTimeInMillis())) {
                    case TIME_TOO_FAST :
                        Toast.makeText(getContext(), "알림이 울릴수 있는 시간으로 설정되어야 합니다.", Toast.LENGTH_LONG).show();
                        break;
                    case TIME_OVERLAP :
                        Toast.makeText(getContext(), "같은 시간에 지정된 작업이 있습니다.", Toast.LENGTH_LONG).show();
                        break;
                    case TITLE_OVERLAP :
                        Toast.makeText(getContext(), "같은 이름으로 지정된 작업이 있습니다.", Toast.LENGTH_LONG).show();
                        break;
                    default :
                        updateUI(mAdapter);
                }
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), false).show();//set time and schedule
    }

    private void updateUI(TaskAdapter mAdapter) {
        if(tm.getTaskTimeArray(getContext(), false).size() != 0) {
            tm.autoRemove(getContext());
            Log.d("1", "1");
        }
        mAdapter.tasks.clear();
        for(int i = 0; i < tm.getTaskTitleArray(getContext(), false).size(); i++) {
            mAdapter.tasks.add(i,
                    new TaskLists().newInstance(
                            tm.getTaskTitleArray(getContext(), true).get(i),
                            tm.getTaskTimeArray(getContext(), true).get(i)));
        }
        Collections.sort(mAdapter.tasks, new tasksComparator());
        mAdapter.notifyDataSetChanged();
    }
    private static class tasksComparator implements Comparator<TaskLists>{
        @Override
        public int compare(TaskLists o1, TaskLists o2) {
            return Long.valueOf(o1.task_time).compareTo(o2.task_time);
        }
    }
}