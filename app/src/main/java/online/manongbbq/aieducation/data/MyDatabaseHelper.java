package online.manongbbq.aieducation.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This is the class to help build a database.
 *
 * @author liang zifan
 * @version 0.1.1
 * @since 07/08
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 创建了6个表
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表
        db.execSQL("CREATE TABLE userinfo (userid INTEGER PRIMARY KEY, password INTEGER, " +
                "name TEXT, facedata BLOB, isstudent BOOLEAN)");
        db.execSQL("CREATE TABLE classinfo (classid INTEGER PRIMARY KEY, coursename TEXT, " +
                "coursedescription TEXT)");
        db.execSQL("CREATE TABLE errorbook (bookid INTEGER PRIMARY KEY, errorimg BLOB, " +
                "erroranalys TEXT)");
        db.execSQL("CREATE TABLE leaveinfo (leaveid INTEGER PRIMARY KEY, leavecontent TEXT, " +
                "leavestudentid INTEGER, leaveclassid INTEGER, leavedate DATE)");
        db.execSQL("CREATE TABLE airequest (requestid INTEGER PRIMARY KEY, requestcontent TEXT, " +
                "requestresult TEXT)");
        db.execSQL("CREATE TABLE schedule (scheduleid INTEGER PRIMARY KEY, starttime TIMESTAMP, " +
                "endtime TIMESTAMP, task TEXT)");
    }

    /**
     * 升级数据库（重置数据库）
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库（这里简单地删除旧表并创建新表）
        db.execSQL("DROP TABLE IF EXISTS userinfo");
        db.execSQL("DROP TABLE IF EXISTS classinfo");
        db.execSQL("DROP TABLE IF EXISTS errorbook");
        db.execSQL("DROP TABLE IF EXISTS leaveinfo");
        db.execSQL("DROP TABLE IF EXISTS airequest");
        db.execSQL("DROP TABLE IF EXISTS schedule");
        onCreate(db);
    }
}