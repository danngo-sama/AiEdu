package online.manongbbq.aieducation.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WrBoDatabaseOperations extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wrongbook.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "errorbook";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMG_PATH = "img_path";
    public static final String COLUMN_DESCRIPTION = "description";

    public WrBoDatabaseOperations(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_IMG_PATH + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertErrorBook(String imgPath, String description) {
        if (imgPath == null || imgPath.isEmpty()) {
            throw new IllegalArgumentException("图片路径不能为空");
        }
        // 检查路径是否有效
        File imgFile = new File(imgPath);
        if (!imgFile.exists()) {
            throw new IllegalArgumentException("图片路径无效，文件不存在: " + imgPath);
        }

        // 检查 imgPath 是否为绝对路径
        if (!imgPath.startsWith("/")) {
            throw new IllegalArgumentException("图片路径不是绝对路径: " + imgPath);
        }


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMG_PATH, imgPath);
        values.put(COLUMN_DESCRIPTION, description);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public boolean deleteErrorBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsDeleted > 0;
    }



    public List<JSONObject> queryErrorBook() throws JSONException {
        List<JSONObject> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                JSONObject obj = new JSONObject();
                obj.put(COLUMN_ID, cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                obj.put(COLUMN_IMG_PATH, cursor.getString(cursor.getColumnIndex(COLUMN_IMG_PATH)));
                obj.put(COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                data.add(obj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }
}