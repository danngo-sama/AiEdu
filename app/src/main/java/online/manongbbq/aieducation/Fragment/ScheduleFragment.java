package online.manongbbq.aieducation.Fragment;

import android.app.DatePickerDialog;
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
    private TextView textViewDate, textViewEvents;
    private Button buttonSelectYear, buttonAddEvent;
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
        textViewEvents = view.findViewById(R.id.textViewEvents);
        buttonSelectYear = view.findViewById(R.id.buttonSelectYear);
        buttonAddEvent = view.findViewById(R.id.buttonAddEvent);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA);
        textViewDate.setText(sdf.format(selectedDate.getTime()));

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            textViewDate.setText(sdf.format(selectedDate.getTime()));
        });

        buttonSelectYear.setOnClickListener(v -> showYearPickerDialog());
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

    private void showYearPickerDialog() {
        int year = selectedDate.get(Calendar.YEAR);
        DatePickerDialog yearPickerDialog = new DatePickerDialog(getContext(), (view, year1, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year1);
            textViewDate.setText(new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA).format(selectedDate.getTime()));
            calendarView.setDate(selectedDate.getTimeInMillis());
        }, year, selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
        yearPickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        yearPickerDialog.getDatePicker().findViewById(getResources().getIdentifier("month", "id", "android")).setVisibility(View.GONE);
        yearPickerDialog.show();
    }

    private void addEvent() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日, EEEE", Locale.CHINA);
        String event = "日程: " + sdf.format(selectedDate.getTime());
        events.add(event);
        updateEventsDisplay();
        Toast.makeText(getContext(), "添加日程: " + event, Toast.LENGTH_SHORT).show();
    }

    private void updateEventsDisplay() {
        StringBuilder eventsDisplay = new StringBuilder("已添加的事件：\n");
        for (String event : events) {
            eventsDisplay.append(event).append("\n");
        }
        textViewEvents.setText(eventsDisplay.toString());
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