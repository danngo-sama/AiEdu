package online.manongbbq.aieducation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.data.CloudDatabaseHelper;
import online.manongbbq.aieducation.data.FirestoreInsertCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUserId, editTextName, editTextPassword, editTextConfirmPassword;
    private SwitchMaterial switchIsTeacher;
    private Button buttonRegister,buttongo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUserId = findViewById(R.id.edit_text);
        editTextName = findViewById(R.id.edit_text2);
        editTextPassword = findViewById(R.id.edit_text3);
        editTextConfirmPassword = findViewById(R.id.edit_text4);
        switchIsTeacher = findViewById(R.id.switch1);
        buttonRegister = findViewById(R.id.button);
        buttongo = findViewById(R.id.button2);

        buttongo.setOnClickListener(v -> {
            // Replace AttendanceActivity.class with the actual activity class
            Intent intent = new Intent(RegisterActivity.this, HomepageStuActivity.class);
            startActivity(intent);
        });

        buttonRegister.setOnClickListener(v -> {
            String userIdStr = editTextUserId.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String passwordStr = editTextPassword.getText().toString().trim();
            String confirmPasswordStr = editTextConfirmPassword.getText().toString().trim();
            boolean isStudent = !switchIsTeacher.isChecked(); // If switch is checked, it means the user is a teacher

            if (userIdStr.isEmpty() || name.isEmpty() || passwordStr.isEmpty() || confirmPasswordStr.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            } else if (!passwordStr.equals(confirmPasswordStr)) {
                Toast.makeText(RegisterActivity.this, "两次输入的密码不同", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    int password = Integer.parseInt(passwordStr);
                    byte[] faceData = null; // Placeholder for face data
                    List<Integer> classIds = new ArrayList<>(); // Placeholder for class IDs

                    insertUserInfo(userId, password, name, faceData, isStudent, classIds, new FirestoreInsertCallback() {
                        @Override
                        public void onStoreSuccess() {

                        }

                        @Override
                        public void onStoreFailure(Exception e) {

                        }

                        @Override
                        public void onSuccess() {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            finish(); // 返回上一个页面
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(RegisterActivity.this, "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(RegisterActivity.this, "用户ID和密码必须是整数", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void insertUserInfo(int userId, int password, String name, byte[] faceData, boolean isStudent, List<Integer> classIds, FirestoreInsertCallback callback) {
        // 模拟的插入用户信息的函数
        // 实际实现应根据您的数据库或服务器API
        // 这里仅作示例
        try {
            CloudDatabaseHelper co = new CloudDatabaseHelper();
            co.insertUserInfo(userId, password, name, faceData, isStudent, classIds, callback);
            // 假设插入成功
            callback.onSuccess();
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    // 回调接口

}