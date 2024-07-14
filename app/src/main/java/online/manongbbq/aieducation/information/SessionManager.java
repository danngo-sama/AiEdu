package online.manongbbq.aieducation.information;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "MyApp";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_PASSWORD = "password";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 存储用户登录信息
     *
     * @param userId 用户ID
     * @param password 用户密码
     */
    public void saveUserCredentials(int userId, int password) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_PASSWORD, password);
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
     * 检查用户是否已登录
     *
     * @return 如果用户已登录则返回true，否则返回false
     */
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_USER_ID);
    }

    /**
     * 退出登录，清除存储的用户信息
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}