package online.manongbbq.aieducation.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;

public class CourseActivityStu extends AppCompatActivity {
    private Spinner spinnerCourses;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_stu);
        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(v -> finish());

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
                Toast.makeText(CourseActivityStu.this, "选中课程: " + selectedCourse, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        imageViewMyCourse.setOnClickListener(v -> showJoinCourseDialog());

        imageViewVoice.setOnClickListener(v -> {
            // Replace VoiceToTextActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivityStu.this, VoiceToTextActivity.class);
            startActivity(intent);
        });

        imageViewAttendance.setOnClickListener(v -> {
            // Replace AttendanceActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivityStu.this, AttendanceActivityStu.class);
            startActivity(intent);
        });

        imageViewLeave.setOnClickListener(v -> {
            // Replace LeaveActivity.class with the actual activity class
            Intent intent = new Intent(CourseActivityStu.this, LeaveActivityStu.class);
            startActivity(intent);
        });
    }

    private void showJoinCourseDialog() {
        // 创建一个AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("加入课堂");

        // 设置对话框布局
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_join_course, null);
        builder.setView(dialogView);

        final EditText editTextCourseId = dialogView.findViewById(R.id.editTextCourseId);

        // 设置对话框按钮
        builder.setPositiveButton("提交", (dialog, which) -> {
            String courseId = editTextCourseId.getText().toString();
            // 处理加入班级申请的逻辑
            submitJoinCourseRequest(courseId);
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void submitJoinCourseRequest(String courseId) {
        // 在这里添加加入班级申请的逻辑
        // 例如，向服务器发送请求以加入班级
        Toast.makeText(this, "提交加入班级申请: " + courseId, Toast.LENGTH_SHORT).show();
    }
}