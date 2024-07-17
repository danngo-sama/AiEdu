package online.manongbbq.aieducation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.data.CloudDatabaseHelper;
import online.manongbbq.aieducation.data.FirestoreInsertCallback;
import online.manongbbq.aieducation.data.FirestoreUpdateCallback;
import online.manongbbq.aieducation.information.SessionManager;

public class CourseActivityTe extends AppCompatActivity {
    private Spinner spinnerCourses;
    private Button buttonBack;
    private CloudDatabaseHelper cloudDbHelper;
    private SessionManager sessionManager;

    private int selectedCourseId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_te);
        buttonBack = findViewById(R.id.buttonBack);
        spinnerCourses = findViewById(R.id.spinnerCourses);
        ImageView imageViewMyCourse = findViewById(R.id.imageViewMyCourse);
        ImageView imageViewStuList = findViewById(R.id.imageStuList);
        ImageView imageViewAttendance = findViewById(R.id.imageViewAttendance);
        ImageView imageViewLeave = findViewById(R.id.imageViewLeave);

        cloudDbHelper = new CloudDatabaseHelper();
        sessionManager = new SessionManager(this);

        buttonBack.setOnClickListener(v -> finish());

        loadCourses();

        imageViewMyCourse.setOnClickListener(v -> showCreateCourseDialog());

        imageViewStuList.setOnClickListener(v -> showStudentListDialog());

        imageViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivityTe.this, AttendanceActivityTe.class);
            startActivity(intent);
        });

        imageViewLeave.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivityTe.this, LeaveActivityTe.class);
            startActivity(intent);
        });
    }

    private void showStudentListDialog() {
        if (selectedCourseId == -1) {
            Toast.makeText(this, "请选择一个课程", Toast.LENGTH_SHORT).show();
            return;
        }

        cloudDbHelper.queryClassInfo(selectedCourseId, classList -> {
            if (classList.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(CourseActivityTe.this, "未找到课程信息", Toast.LENGTH_SHORT).show());
                return;
            }

            List<?> rawStudentIds = (List<?>) classList.get(0).get("studentIds");
            List<Integer> studentIds = new ArrayList<>();
            if (rawStudentIds != null) {
                for (Object id : rawStudentIds) {
                    if (id instanceof Long) {
                        studentIds.add(((Long) id).intValue());
                    } else if (id instanceof Integer) {
                        studentIds.add((Integer) id);
                    }
                }
            }

            if (studentIds.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(CourseActivityTe.this, "该课程没有学生", Toast.LENGTH_SHORT).show());
                return;
            }

            List<String> studentNames = new ArrayList<>();
            for (int studentId : studentIds) {
                cloudDbHelper.queryUserInfo(studentId, userList -> {
                    if (!userList.isEmpty()) {
                        String studentName = (String) userList.get(0).get("name");
                        if (studentName != null) {
                            studentNames.add(studentName);
                        }
                    }

                    if (studentId == studentIds.get(studentIds.size() - 1)) {
                        runOnUiThread(() -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CourseActivityTe.this);
                            builder.setTitle("学生列表");

                            LayoutInflater inflater = CourseActivityTe.this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_student_list, null);
                            builder.setView(dialogView);

                            String[] studentsArray = studentNames.toArray(new String[0]);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CourseActivityTe.this, android.R.layout.simple_list_item_1, studentsArray);
                            ListView listView = dialogView.findViewById(R.id.listViewStudents);
                            listView.setAdapter(adapter);

                            builder.setPositiveButton("确定", (dialog, which) -> dialog.dismiss());

                            builder.create().show();
                        });
                    }
                });
            }
        });
    }




    private void loadCourses() {
        int userId = sessionManager.getUserId();
        Log.d("CourseActivityTe", "Loading courses for userId: " + userId);

        cloudDbHelper.queryUserInfo(userId, userList -> {
            if (userList.isEmpty()) {
                Log.d("CourseActivityTe", "没有用户信息");
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"暂时没有课程"});
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourses.setAdapter(adapter);
                });
            } else {
                Log.d("CourseActivityTe", "查找到用户信息");
                List<?> rawClassIds = (List<?>) userList.get(0).get("classIds");
                List<Integer> classIds = new ArrayList<>();
                if (rawClassIds != null) {
                    for (Object id : rawClassIds) {
                        if (id instanceof Long) {
                            classIds.add(((Long) id).intValue());
                        } else if (id instanceof Integer) {
                            classIds.add((Integer) id);
                        }
                    }
                }
                Log.d("CourseActivityTe", "Found classIds: " + classIds);

                if (!classIds.isEmpty()) {
                    Log.d("CourseActivityTe", "发现该用户有课程列表");
                    List<String> courseNames = new ArrayList<>();
                    for (int classId : classIds) {
                        cloudDbHelper.queryClassInfo(classId, classList -> {
                            if (!classList.isEmpty()) {
                                String courseName = (String) classList.get(0).get("courseName");
                                Log.d("CourseActivityTe", "Found courseName: " + courseName);
                                if (courseName != null) {
                                    courseNames.add(classId + " - " + courseName);
                                }
                            } else {
                                Log.d("CourseActivityTe", "No class information found for classId: " + classId);
                            }
                            if (classId == classIds.get(classIds.size() - 1)) {
                                runOnUiThread(() -> {
                                    if (courseNames.isEmpty()) {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"暂时没有课程"});
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerCourses.setAdapter(adapter);
                                    } else {
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseNames);
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinnerCourses.setAdapter(adapter);

                                        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                String selectedCourse = courseNames.get(position);
                                                Toast.makeText(CourseActivityTe.this, "选中课程: " + selectedCourse, Toast.LENGTH_SHORT).show();
                                                selectedCourseId = Integer.parseInt(selectedCourse.split(" - ")[0]); // Extract courseId
                                                Log.d("CourseActivityTe", "Selected courseId: " + selectedCourseId);
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {
                                                // Do nothing
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Log.d("CourseActivityTe", "没有查找到用户的课程列表");
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"暂时没有课程"});
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCourses.setAdapter(adapter);
                    });
                }
            }
        });
    }
    private void showCreateCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建课堂");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_course, null);
        builder.setView(dialogView);

        final EditText editTextCourseId = dialogView.findViewById(R.id.editTextCourseId);
        final EditText editTextCourseName = dialogView.findViewById(R.id.editTextCourseName);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String courseId = editTextCourseId.getText().toString().trim();
            String courseName = editTextCourseName.getText().toString().trim();
            if (!courseId.isEmpty() && !courseName.isEmpty()) {
                Log.d("CourseActivityTe", "开始创建课堂");
                createCourse(Integer.parseInt(courseId), courseName);
            } else {
                Toast.makeText(CourseActivityTe.this, "请输入课程ID和课程名称", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void createCourse(int courseId, String courseName) {
        Log.d("CourseActivityTe", "进入创建班级函数");
        int teacherId = sessionManager.getUserId();

        cloudDbHelper.insertClassInfo(courseId, teacherId, null, courseName, "", null, null, "", "", new FirestoreInsertCallback() {
            @Override
            public void onStoreSuccess() {

            }

            @Override
            public void onStoreFailure(Exception e) {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(Exception e) {

            }

            @Override
            public void onInsertSuccess() {
//                Log.d("CourseActivityTe", "课堂表插入成功，现在给用户插入课堂列表！");
//                updateUserClasses(teacherId, courseId);
//                runOnUiThread(() -> Toast.makeText(CourseActivityTe.this, "课程创建成功", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onInsertFailure(Exception e) {
//                Log.d("CourseActivityTe", "课堂表插入失败");
//                runOnUiThread(() -> Toast.makeText(CourseActivityTe.this, "课程创建失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        Log.d("CourseActivityTe", "现在给用户插入课堂列表！");
        updateUserClasses(teacherId, courseId);
    }

    private void updateUserClasses(int teacherId, int newClassId) {
        cloudDbHelper.queryUserInfo(teacherId, userList -> {
            if (!userList.isEmpty()) {
                List<Integer> classIds = (List<Integer>) userList.get(0).get("classIds");
                if (classIds != null) {
                    classIds.add(newClassId);
                } else {
                    classIds = new ArrayList<>();
                    classIds.add(newClassId);
                }
                Map<String, Object> updates = new HashMap<>();
                updates.put("classIds", classIds);
                cloudDbHelper.updateUserInfo(teacherId, updates, new FirestoreUpdateCallback() {
                    @Override
                    public void onUpdateSuccess() {
                        Log.d("CourseActivityTe", "User classes updated successfully");
                        loadCourses();
                    }

                    @Override
                    public void onUpdateFailure(Exception e) {
                        Log.d("CourseActivityTe", "User classes update failed: " + e.getMessage());
                        runOnUiThread(() -> Toast.makeText(CourseActivityTe.this, "更新用户信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                Log.d("CourseActivityTe", "No user information found for update");
            }
        });
    }
}