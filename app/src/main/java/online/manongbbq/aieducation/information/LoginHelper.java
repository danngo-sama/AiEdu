package online.manongbbq.aieducation.information;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
import java.util.Map;

import online.manongbbq.aieducation.data.CloudDatabaseHelper;
import online.manongbbq.aieducation.data.FirestoreQueryCallback;

public class LoginHelper {

    private final CloudDatabaseHelper cloudDbHelper;
    private final Context context;

    public LoginHelper(Context context) {
        this.cloudDbHelper = new CloudDatabaseHelper();
        this.context = context;
    }

    public void login(int userId, int password, LoginCallback callback) {
        cloudDbHelper.queryUserInfo(userId, new FirestoreQueryCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> userList) {
                if (userList.isEmpty()) {
                    Log.d("LoginHelper", "User not found");
                    callback.onLoginResult(false);
                    return;
                }

                Map<String, Object> user = userList.get(0);
                int storedPassword = (int) user.get("password");

                if (storedPassword == password) {
                    saveUserCredentials(userId, password);
                    callback.onLoginResult(true);
                } else {
                    Log.d("LoginHelper", "Password mismatch");
                    callback.onLoginResult(false);
                }
            }
        });
    }

    private void saveUserCredentials(int userId, int password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.putInt("password", password); // 通常密码不应该以明文形式存储，建议使用加密存储
        editor.apply();
    }

    public interface LoginCallback {
        void onLoginResult(boolean success);
    }

}