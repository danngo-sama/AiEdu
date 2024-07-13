package online.manongbbq.aieducation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Calendar;
import android.widget.Button;

import online.manongbbq.aieducation.R;

public class ReportActivityStu extends AppCompatActivity {

    private Spinner spinnerWeek;
    private TextView textViewWrongQuestionsCount;
    private TextView textViewTasksCount;
    private TextView textViewAttendanceCount;
    private TextView textViewLeavesCount;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_stu);

        spinnerWeek = findViewById(R.id.spinnerWeek);
        textViewWrongQuestionsCount = findViewById(R.id.textViewWrongQuestionsCount);
        textViewTasksCount = findViewById(R.id.textViewTasksCount);
        textViewAttendanceCount = findViewById(R.id.textViewAttendanceCount);
        textViewLeavesCount = findViewById(R.id.textViewLeavesCount);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());

        setupWeekSpinner();
        loadCurrentWeekData();
    }

    private void setupWeekSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.weeks_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWeek.setAdapter(adapter);

        spinnerWeek.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadSelectedWeekData(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadCurrentWeekData() {
        int currentWeek = getCurrentWeekOfYear();
        spinnerWeek.setSelection(currentWeek - 1);
        loadSelectedWeekData(currentWeek - 1);
    }

    private int getCurrentWeekOfYear() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setMinimalDaysInFirstWeek(7);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    private void loadSelectedWeekData(int week) {
        // 在这里加载选择的周的数据
        // 你需要根据实际的数据源来实现这个功能

        // 假设我们有一些示例数据：
        int wrongQuestionsCount = 5; // 示例数据
        int tasksCount = 10; // 示例数据
        int attendanceCount = 6; // 示例数据
        int leavesCount = 1; // 示例数据

        // 根据选择的周加载数据
        // 这里使用示例数据
        textViewWrongQuestionsCount.setText(String.valueOf(wrongQuestionsCount));
        textViewTasksCount.setText(String.valueOf(tasksCount));
        textViewAttendanceCount.setText(String.valueOf(attendanceCount));
        textViewLeavesCount.setText(String.valueOf(leavesCount));
    }
}