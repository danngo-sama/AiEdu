package online.manongbbq.aieducation.information;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;
import java.util.Map;
import online.manongbbq.aieducation.data.CloudDatabaseHelper;
import online.manongbbq.aieducation.data.FirestoreQueryCallback;

public class SessionManager {
    private static SessionManager instance;
    private Context context;

    private static final String PREF_NAME = "MyApp";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_IS_STUDENT = "isStudent";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CloudDatabaseHelper cloudDbHelper = new CloudDatabaseHelper();

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 存储用户登录信息
     *
     * @param userId 用户ID
     * @param password 用户密码
     * @param isStudent 是否是学生
     */
    public void saveUserCredentials(int userId, int password, boolean isStudent) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_PASSWORD, password);
        editor.putBoolean(KEY_IS_STUDENT, isStudent);
        editor.apply();
    }

    /**
     * 获取已登录的用户ID
     *
     * @return 用户ID，如果未找到则返回-1
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * 获取是否是学生
     *
     * @return 是否是学生，如果未找到则返回false
     */
    public boolean isStudent() {
        return sharedPreferences.getBoolean(KEY_IS_STUDENT, false);
    }

    /**
     * 检查用户是否已登录
     *
     * @return 如果用户已登录则返回true，否则返回false
     */
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    /**
     * 获取用户姓名
     *
     * @param callback 回调接口
     */
    public void getUserName(NameCallback callback) {
        int userId = getUserId();
        if (userId == -1) {
            callback.onError(new Exception("User not logged in"));
            return;
        }

        cloudDbHelper.queryUserInfo(userId, new FirestoreQueryCallback() {
            @Override
            public void onCallback(List<Map<String, Object>> userList) {
                if (userList.isEmpty()) {
                    callback.onError(new Exception("User not found"));
                    return;
                }

                Map<String, Object> user = userList.get(0);
                String name = (String) user.get("name");
                if (name != null) {
                    callback.onNameFound(name);
                } else {
                    callback.onError(new Exception("Name not found"));
                }
            }
        });
    }

    /**
     * 退出登录，清除存储的用户信息
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }
}