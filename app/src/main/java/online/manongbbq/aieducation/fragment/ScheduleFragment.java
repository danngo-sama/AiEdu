package online.manongbbq.aieducation.fragment;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import online.manongbbq.aieducation.R;

public class ScheduleFragment extends Fragment {

    private CalendarView calendarView;
    private TextView textViewDate, textViewNoEvents;
    private EditText editTextEvent;
    private Button buttonAddEvent;
    private LinearLayout linearLayoutEvents;
    private Calendar selectedDate = Calendar.getInstance(Locale.CHINA);
    private List<String> events = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

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

    private void addEvent() {
        String eventText = editTextEvent.getText().toString().trim();
        if (eventText.isEmpty()) {
            Toast.makeText(getContext(), "请输入事件内容", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA);
        String event = sdf.format(selectedDate.getTime()) + ": " + eventText;
        events.add(event);
        updateEventsDisplay();
        editTextEvent.setText("");
        Toast.makeText(getContext(), "添加日程: " + event, Toast.LENGTH_SHORT).show();
    }

    private void updateEventsDisplay() {
        linearLayoutEvents.removeAllViews();
        if (events.isEmpty()) {
            linearLayoutEvents.addView(textViewNoEvents);
        } else {
            for (String event : events) {
                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(event);
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        events.remove(event);
                        updateEventsDisplay();
                    }
                });
                linearLayoutEvents.addView(checkBox);
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