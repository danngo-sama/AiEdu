package online.manongbbq.aieducation.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.data.DatabaseOperations;
import online.manongbbq.aieducation.data.DatabaseOperations;

public class ScheduleFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textViewDate, textViewNoEvents;
    private EditText editTextEvent;
    private Button buttonAddEvent;
    private LinearLayout linearLayoutEvents;
    private Calendar selectedDate = Calendar.getInstance(Locale.CHINA);
    private List<JSONObject> events = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

//    private DatabaseOperations data;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_stu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewNoEvents = view.findViewById(R.id.textViewNoEvents);
        editTextEvent = view.findViewById(R.id.editTextEvent);
        buttonAddEvent = view.findViewById(R.id.buttonAddEvent);
        linearLayoutEvents = view.findViewById(R.id.linearLayoutEvents);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA);
        textViewDate.setText(sdf.format(selectedDate.getTime()));

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            textViewDate.setText(sdf.format(selectedDate.getTime()));
        });

        buttonAddEvent.setOnClickListener(v -> addEvent());

        // Load events from database
        loadEventsFromDatabase();

        // Initialize and start the handler to update the date display every second
        runnable = new Runnable() {
            @Override
            public void run() {
                updateDateTime();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private void loadEventsFromDatabase() {
        try {
            events = DatabaseOperations.querySchedule(getContext());
            updateEventsDisplay();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "加载日程失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void addEvent() {
        String eventText = editTextEvent.getText().toString().trim();
        if (eventText.isEmpty()) {
            Toast.makeText(getContext(), "请输入事件内容", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        String starttime = sdf.format(selectedDate.getTime());
        String endtime = ""; // 不使用endtime参数
        int scheduleid = events.size() + 1; // 简单地用列表的大小+1作为scheduleid

        DatabaseOperations.insertSchedule(getContext(), scheduleid, starttime, endtime, eventText);

        JSONObject newEvent = new JSONObject();
        try {
            newEvent.put("scheduleid", scheduleid);
            newEvent.put("starttime", starttime);
            newEvent.put("endtime", endtime);
            newEvent.put("task", eventText);
            events.add(newEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateEventsDisplay();
        editTextEvent.setText("");
        Toast.makeText(getContext(), "添加日程: " + eventText, Toast.LENGTH_SHORT).show();
    }

    private void updateEventsDisplay() {
        linearLayoutEvents.removeAllViews();
        if (events.isEmpty()) {
            linearLayoutEvents.addView(textViewNoEvents);
        } else {
            for (JSONObject event : events) {
                CheckBox checkBox = new CheckBox(getContext());
                try {
                    String displayText = event.getString("starttime") + ": " + event.getString("task");
                    checkBox.setText(displayText);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            try {
                                int scheduleid = event.getInt("scheduleid");
                                DatabaseOperations.deleteSchedule(getContext(), scheduleid);
                                events.remove(event);
                                updateEventsDisplay();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    linearLayoutEvents.addView(checkBox);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA);
        textViewDate.setText(sdf.format(selectedDate.getTime()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // 防止内存泄漏，移除回调
    }
}