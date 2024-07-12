package online.manongbbq.aieducation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;

public class CourseActivity extends AppCompatActivity {
    private Spinner spinnerCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        spinnerCourses = findViewById(R.id.spinnerCourses);
        ImageView imageViewMyCourse = findViewById(R.id.imageViewMyCourse);
        ImageView imageViewVoice = findViewById(R.id.imageViewVoice);
        ImageView imageViewAttendance = findViewById(R.id.imageViewAttendance);
        ImageView imageViewLeave = findViewById(R.id.imageViewLeave);

        // Placeholder data for the spinner, replace with data from your database
        String[] courses = {"课程1", "课程2", "课程3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapter);

        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCourse = courses[position];
                Toast.makeText(CourseActivity.this, "选中课程: " + selectedCourse, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        imageViewMyCourse.setOnClickListener(v -> {
            // Replace MyCourseActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivity.this, MyCourseActivity.class);
            startActivity(intent);
        });

        imageViewVoice.setOnClickListener(v -> {
            // Replace VoiceToTextActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivity.this, VoiceToTextActivity.class);
            startActivity(intent);
        });

        imageViewAttendance.setOnClickListener(v -> {
            // Replace AttendanceActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivity.this, AttendanceActivity.class);
            startActivity(intent);
        });

        imageViewLeave.setOnClickListener(v -> {
            // Replace LeaveActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivity.this, LeaveActivity.class);
            startActivity(intent);
        });
    }
}