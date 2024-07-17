package online.manongbbq.aieducation.activity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.information.LoginHelper;
import online.manongbbq.aieducation.information.SessionManager;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUserId, editTextPassword;
    private Button buttonLogin;
    private SwitchMaterial switchIsTeacher;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        View textViewButton = findViewById(R.id.textView5);
        textViewButton.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 检查是否已经登录
        if (sessionManager.isLoggedIn()) {
            if (sessionManager.isStudent()) {
                navigateToHomepage();
            } else {
                navigateToHomepageTe();
            }
        }

        editTextUserId = findViewById(R.id.edit_text);
        editTextPassword = findViewById(R.id.edit_text2);
        buttonLogin = findViewById(R.id.button);
        switchIsTeacher = findViewById(R.id.switch1);

        buttonLogin.setOnClickListener(v -> {
            String userIdStr = editTextUserId.getText().toString().trim();
            String passwordStr = editTextPassword.getText().toString().trim();

            if (userIdStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(MainActivity.this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    int userId = Integer.parseInt(userIdStr);
                    int password = Integer.parseInt(passwordStr);

                    LoginHelper loginHelper = new LoginHelper(this);
                    loginHelper.login(userId, password, success -> {
                        if (success) {
                            // 保存用户凭据
                            sessionManager.saveUserCredentials(userId, password,!switchIsTeacher.isChecked());

                            // 检查Switch是否未选中
                            if (!switchIsTeacher.isChecked()) {
                                navigateToHomepage();
                            } else {
                                navigateToHomepageTe();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "用户ID和密码必须是整数", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void navigateToHomepage() {
        Intent intent = new Intent(MainActivity.this, HomepageStuActivity.class);
        startActivity(intent);
        finish(); // 关闭当前活动，防止返回
    }

    private void navigateToHomepageTe() {
        Intent intent = new Intent(MainActivity.this, HomepageTeActivity.class);
        startActivity(intent);
        finish(); // 关闭当前活动，防止返回
    }
}
