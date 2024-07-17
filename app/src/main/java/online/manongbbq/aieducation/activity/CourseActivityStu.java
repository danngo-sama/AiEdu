package online.manongbbq.aieducation.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.data.CloudDatabaseHelper;
import online.manongbbq.aieducation.data.FirestoreInsertCallback;
import online.manongbbq.aieducation.data.FirestoreUpdateCallback;
import online.manongbbq.aieducation.information.SessionManager;

public class CourseActivityStu extends AppCompatActivity {
    private Spinner spinnerCourses;
    private Button buttonBack;
    private CloudDatabaseHelper cloudDbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_stu);
        buttonBack = findViewById(R.id.buttonBack);
        spinnerCourses = findViewById(R.id.spinnerCourses);
        ImageView imageViewMyCourse = findViewById(R.id.imageViewMyCourse);
        ImageView imageViewVoice = findViewById(R.id.imageViewVoice);
        ImageView imageViewAttendance = findViewById(R.id.imageViewAttendance);
        ImageView imageViewLeave = findViewById(R.id.imageViewLeave);

        cloudDbHelper = new CloudDatabaseHelper();
        sessionManager = new SessionManager(this);

        buttonBack.setOnClickListener(v -> finish());

        loadCourses();

        imageViewMyCourse.setOnClickListener(v -> showJoinCourseDialog());

        imageViewVoice.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivityStu.this, VoiceToTextActivity.class);
            startActivity(intent);
        });

        imageViewAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(CourseActivityStu.this, AttendanceActivityStu.class);
            startActivity(intent);
        });

        imageViewLeave.setOnClickListener(v -> {
            int userId = sessionManager.getUserId();
            int selectedCourseId = getSelectedCourseId(); // Replace with your method to get the selected course ID
            showLeaveInfoDialog(userId, selectedCourseId);
        });
    }

    private void showLeaveInfoDialog(int studentId, int courseId) {
        cloudDbHelper.queryClassInfo(courseId, classInfoList -> {
            String courseName = "未知课程";
            if (!classInfoList.isEmpty()) {
                courseName = (String) classInfoList.get(0).get("courseName");
            }

            String finalCourseName = courseName;
            cloudDbHelper.queryLeaveInfoByStudent(studentId, courseId, leaveInfoList -> {
                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("请假信息");

                    if (leaveInfoList.isEmpty()) {
                        builder.setMessage("暂无请假");
                    } else {
                        LinearLayout layout = new LinearLayout(this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        int padding = 16; // 这里直接定义一个固定的 padding 值

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        for (Map<String, Object> leave : leaveInfoList) {
                            StringBuilder leaveInfo = new StringBuilder();
                            leaveInfo.append("课程名: ").append(finalCourseName).append("\n");
                            leaveInfo.append("课程ID: ").append(leave.get("courseId")).append("\n");

                            // 格式化请假时间
                            Object leaveDateObj = leave.get("leaveDate");
                            String leaveDateStr = leaveDateObj instanceof Date ? dateFormat.format((Date) leaveDateObj) : leaveDateObj.toString();
                            leaveInfo.append("请假时间: ").append(leaveDateStr).append("\n");

                            leaveInfo.append("事由: ").append(leave.get("leaveContent")).append("\n");
                            boolean isApproved = (boolean) leave.get("isApproved");
                            String approvalStatus = isApproved ? "已批准" : "暂未批准";
                            leaveInfo.append("审批状态: ").append(approvalStatus).append("\n");

                            TextView leaveTextView = new TextView(this);
                            leaveTextView.setText(leaveInfo.toString());
                            leaveTextView.setPadding(padding, padding, padding, padding);
                            leaveTextView.setBackgroundResource(R.drawable.border);
                            layout.addView(leaveTextView);
                        }

                        ScrollView scrollView = new ScrollView(this);
                        scrollView.addView(layout);

                        builder.setView(scrollView);
                    }

                    builder.setPositiveButton("确定", null);

                    builder.setNegativeButton("请假", (dialog, which) -> showLeaveRequestDialog(studentId, courseId));

                    builder.create().show();
                });
            });
        });
    }
    private void showLeaveRequestDialog(int studentId, int courseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请假");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_leave_request, null);
        builder.setView(dialogView);

        final EditText editTextLeaveDate = dialogView.findViewById(R.id.editTextLeaveDate);
        final EditText editTextLeaveReason = dialogView.findViewById(R.id.editTextLeaveReason);

        // 设置当前日期为默认日期
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextLeaveDate.setText(dateFormat.format(calendar.getTime()));

        // 点击日期编辑框时显示日期选择器
        editTextLeaveDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                editTextLeaveDate.setText(dateFormat.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        builder.setPositiveButton("提交", (dialog, which) -> {
            String leaveDateStr = editTextLeaveDate.getText().toString().trim();
            String leaveReason = editTextLeaveReason.getText().toString().trim();

            if (!leaveDateStr.isEmpty() && !leaveReason.isEmpty()) {
                try {
                    java.util.Date utilDate = dateFormat.parse(leaveDateStr);
                    java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); // 转换为 java.sql.Date
                    boolean isApproved = false; // Initially not approved

                    cloudDbHelper.queryClassInfo(courseId, classInfoList -> {
                        if (!classInfoList.isEmpty()) {
                            Long teacherIdLong = (Long) classInfoList.get(0).get("teacherId");
                            int teacherId = teacherIdLong.intValue(); // 将 Long 转换为 Integer

                            cloudDbHelper.insertLeaveInfo(studentId, teacherId, courseId, isApproved, leaveReason, sqlDate, new FirestoreInsertCallback() {
                                @Override
                                public void onStoreSuccess() {
                                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "请假信息提交成功", Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onStoreFailure(Exception e) {
                                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "请假信息提交失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onSuccess() {}

                                @Override
                                public void onFailure(Exception e) {}

                                @Override
                                public void onInsertSuccess() {
                                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "请假信息提交成功", Toast.LENGTH_SHORT).show());
                                }

                                @Override
                                public void onInsertFailure(Exception e) {
                                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "请假信息提交失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "无法获取课程信息", Toast.LENGTH_SHORT).show());
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(CourseActivityStu.this, "日期格式错误，请重新选择日期", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CourseActivityStu.this, "请填写完整的请假信息", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    private int getSelectedCourseId() {
        String selectedCourse = (String) spinnerCourses.getSelectedItem();
        if (selectedCourse != null && !selectedCourse.equals("暂时没有课程")) {
            return Integer.parseInt(selectedCourse.split(" - ")[0].trim());
        } else {
            return -1; // or some other invalid ID
        }
    }

    private void loadCourses() {
        int userId = sessionManager.getUserId();
        Log.d("CourseActivityStu", "Loading courses for userId: " + userId);

        cloudDbHelper.queryUserInfo(userId, userList -> {
            if (userList.isEmpty()) {
                Log.d("CourseActivityStu", "没有用户信息");
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"暂时没有课程"});
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourses.setAdapter(adapter);
                });
            } else {
                Log.d("CourseActivityStu", "查找到用户信息");
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
                Log.d("CourseActivityStu", "Found classIds: " + classIds);

                if (!classIds.isEmpty()) {
                    Log.d("CourseActivityStu", "发现该用户有课程列表");
                    List<String> courseNames = new ArrayList<>();
                    for (int classId : classIds) {
                        cloudDbHelper.queryClassInfo(classId, classList -> {
                            if (!classList.isEmpty()) {
                                String courseName = (String) classList.get(0).get("courseName");
                                Log.d("CourseActivityStu", "Found courseName: " + courseName);
                                if (courseName != null) {
                                    courseNames.add(classId + " - " + courseName);
                                }
                            } else {
                                Log.d("CourseActivityStu", "No class information found for classId: " + classId);
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
                                    }
                                });
                            }
                        });
                    }
                } else {
                    Log.d("CourseActivityStu", "没有查找到用户的课程列表");
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"暂时没有课程"});
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCourses.setAdapter(adapter);
                    });
                }
            }
        });
    }

    private void showJoinCourseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("加入课堂");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_join_course, null);
        builder.setView(dialogView);

        final EditText editTextCourseId = dialogView.findViewById(R.id.editTextCourseId);

        builder.setPositiveButton("提交", (dialog, which) -> {
            String courseIdStr = editTextCourseId.getText().toString().trim();
            if (!courseIdStr.isEmpty()) {
                int courseId = Integer.parseInt(courseIdStr);
                joinCourse(courseId);
            } else {
                Toast.makeText(CourseActivityStu.this, "请输入课程ID", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void joinCourse(int courseId) {
        int userId = sessionManager.getUserId();
        cloudDbHelper.queryUserInfo(userId, userList -> {
            if (!userList.isEmpty()) {
                List<Integer> classIds = (List<Integer>) userList.get(0).get("classIds");
                if (classIds == null) {
                    classIds = new ArrayList<>();
                }
                if (!classIds.contains(courseId)) {
                    classIds.add(courseId);
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("classIds", classIds);
                    cloudDbHelper.updateUserInfo(userId, userUpdates, new FirestoreUpdateCallback() {
                        @Override
                        public void onUpdateSuccess() {
                            Log.d("CourseActivityStu", "用户课程列表更新成功");

                            // 现在更新课程的学生列表
                            cloudDbHelper.queryClassInfo(courseId, classList -> {
                                if (!classList.isEmpty()) {
                                    List<Integer> studentIds = (List<Integer>) classList.get(0).get("studentIds");
                                    if (studentIds == null) {
                                        studentIds = new ArrayList<>();
                                    }
                                    if (!studentIds.contains(userId)) {
                                        studentIds.add(userId);
                                        Map<String, Object> classUpdates = new HashMap<>();
                                        classUpdates.put("studentIds", studentIds);
                                        cloudDbHelper.updateClassInfo(courseId, classUpdates, new FirestoreUpdateCallback() {
                                            @Override
                                            public void onUpdateSuccess() {
                                                Log.d("CourseActivityStu", "课程学生列表更新成功");
                                                runOnUiThread(() -> {
                                                    Toast.makeText(CourseActivityStu.this, "成功加入课程: " + courseId, Toast.LENGTH_SHORT).show();
                                                    loadCourses(); // Refresh the course list
                                                });
                                            }

                                            @Override
                                            public void onUpdateFailure(Exception e) {
                                                Log.d("CourseActivityStu", "课程学生列表更新失败: " + e.getMessage());
                                                runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "加入课程失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                            }
                                        });
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "你已在该课程的学生列表中", Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "找不到课程信息", Toast.LENGTH_SHORT).show());
                                }
                            });
                        }

                        @Override
                        public void onUpdateFailure(Exception e) {
                            Log.d("CourseActivityStu", "用户课程列表更新失败: " + e.getMessage());
                            runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "加入课程失败: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CourseActivityStu.this, "你已加入该课程", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}