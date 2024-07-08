package online.manongbbq.aieducation.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Use function to save and load information.
 * <p><h1>all functions</h1></p>
 * <p>
 *     以下为所有方法的定义和<strong>部分</strong>用例。
 *     <ui>
 *         <li>
 *             <p>{@link #insertUserInfo(Context, int, int, String, byte[], boolean)}</p>
 *             <p>{@code DatabaseOperations.insertUserInfo(this, 1, 123456, "John Doe", null, true);}
 *             // 插入用户信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #insertClassInfo(Context, int, String, String)}</p>
 *             <p>{@code DatabaseOperations.insertClassInfo(this, 1, "Math", "This is a math course.");}
 *             // 插入课程信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #insertErrorBook(Context, int, byte[], String)}</p>
 *             <p>{@code DatabaseOperations.insertErrorBook(this, 1, null, "Error analysis here.");}
 *             // 插入错题本信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #insertLeaveInfo(Context, int, String, int, int, String)}</p>
 *             <p>{@code DatabaseOperations.insertLeaveInfo(this, 1, "Personal reasons", 1, 1, "2023-07-01");}
 *             // 插入请假信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #insertAIRequest(Context, int, String, String)}</p>
 *             <p>{@code DatabaseOperations.insertAIRequest(this, "Request content", "Request result");}
 *             // 插入AI请求信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryUserInfo(Context)}</p>
 *             <p>{@code DatabaseOperations.queryUserInfo(this);}
 *             // 查询所有用户信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryUserInfo(Context, int)}</p>
 *             <p>{@code DatabaseOperations.queryUserInfo(this, 1);}
 *             // 查询某个用户信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryClassInfo(Context)}</p>
 *             <p>{@code DatabaseOperations.queryClassInfo(this);}
 *             // 查询所有课程信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryClassInfo(Context, int)}</p>
 *             <p>{@code DatabaseOperations.queryClassInfo(this， 1);}
 *             // 查询某个课程信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryErrorBook(Context)}</p>
 *             <p>{@code DatabaseOperations.queryErrorBook(this);}
 *             // 查询错题本信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryLeaveInfo(Context)}</p>
 *             <p>{@code DatabaseOperations.queryLeaveInfo(this);}
 *             // 查询请假信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #queryAIRequest(Context)}</p>
 *             <p>{@code DatabaseOperations.queryAIRequest(this);}
 *             // 查询AI请求信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #updateUserInfo(Context, int, int, String, byte[], boolean)}</p>
 *             <p>{@code DatabaseOperations.updateUserInfo(this, 1, 654321, "Jane Doe", null, false);}
 *             // 更新用户信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #updateClassInfo(Context, int, String, String)}</p>
 *             <p>{@code }
 *             // 更新课程信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #updateErrorBook(Context, int, byte[], String)}</p>
 *             <p>{@code }
 *             // 更新错题本信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #updateLeaveInfo(Context, int, String, int, int, String)}</p>
 *             <p>{@code }
 *             // 更新请假信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #updateAIRequest(Context, int, String, String)}</p>
 *             <p>{@code }
 *             // 更新AI请求信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #deleteUserInfo(Context, int)}</p>
 *             <p>{@code }
 *             // 删除用户信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #deleteClassInfo(Context, int)}</p>
 *             <p>{@code }
 *             // 删除课程信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #deleteErrorBook(Context, int)}</p>
 *             <p>{@code }
 *             // 删除错题本信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #deleteLeaveInfo(Context, int)}</p>
 *             <p>{@code DatabaseOperations.deleteLeaveInfo(this, 1);}
 *             // 删除请假信息</p>
 *         </li>
 *         <li>
 *             <p>{@link #deleteAIRequest(Context, int)}</p>
 *             <p>{@code }
 *             // 删除AI请求信息</p>
 *         </li>
 *     </ui>
 * </p>
 *
 * @author liang zifan
 * @version 0.0.2
 * @since 07/08
 */
public class DatabaseOperations {
    // 插入用户信息
    public static void insertUserInfo(Context context, int userid, int password, String name,
                                      byte[] facedata, boolean isstudent) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userid", userid);
        values.put("password", password);
        values.put("name", name);
        values.put("facedata", facedata);
        values.put("isstudent", isstudent);

        db.insert("userinfo", null, values);
        db.close();
    }

    // 插入课程信息
    public static void insertClassInfo(Context context, int classid, String coursename,
                                       String coursedescription) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("classid", classid);
        values.put("coursename", coursename);
        values.put("coursedescription", coursedescription);

        db.insert("classinfo", null, values);
        db.close();
    }

    // 插入错题本信息
    public static void insertErrorBook(Context context, int bookid, byte[] errorimg,
                                       String erroranalys) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("bookid", bookid);
        values.put("errorimg", errorimg);
        values.put("erroranalys", erroranalys);

        db.insert("errorbook", null, values);
        db.close();
    }

    // 插入请假信息
    public static void insertLeaveInfo(Context context, int leaveid, String leavecontent,
                                       int leavestudentid, int leaveclassid, String leavedate) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("leaveid", leaveid);
        values.put("leavecontent", leavecontent);
        values.put("leavestudentid", leavestudentid);
        values.put("leaveclassid", leaveclassid);
        values.put("leavedate", leavedate);

        db.insert("leaveinfo", null, values);
        db.close();
    }

    // 插入AI请求信息
    public static void insertAIRequest(Context context, int requestid, String requestcontent,
                                       String requestresult) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("requestid", requestid);
        values.put("requestcontent", requestcontent);
        values.put("requestresult", requestresult);

        db.insert("airequest", null, values);
        db.close();
    }

    // 查询所有用户信息
    public static List<JSONObject> queryUserInfo(Context context) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "userid",
                "password",
                "name",
                "facedata",
                "isstudent"
        };

        Cursor cursor = db.query(
                "userinfo",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int userid = cursor.getInt(cursor.getColumnIndexOrThrow("userid"));
            int password = cursor.getInt(cursor.getColumnIndexOrThrow("password"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            byte[] facedata = cursor.getBlob(cursor.getColumnIndexOrThrow("facedata"));
            boolean isstudent = cursor.getInt(cursor.getColumnIndexOrThrow("isstudent")) > 0;

            JSONObject obj = new JSONObject();
            obj.put("userid", userid);
            obj.put("password", password);
            obj.put("name", name);
            obj.put("facedata", facedata);
            obj.put("isstudent", isstudent);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询某个用户信息
    public static List<JSONObject> queryUserInfo(Context context, int id) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "userid",
                "password",
                "name",
                "facedata",
                "isstudent"
        };

        String selection = "userid = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                "userinfo",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                selection,             // The columns for the WHERE clause
                selectionArgs,         // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int userid = cursor.getInt(cursor.getColumnIndexOrThrow("userid"));
            int password = cursor.getInt(cursor.getColumnIndexOrThrow("password"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            byte[] facedata = cursor.getBlob(cursor.getColumnIndexOrThrow("facedata"));
            boolean isstudent = cursor.getInt(cursor.getColumnIndexOrThrow("isstudent")) > 0;

            JSONObject obj = new JSONObject();
            obj.put("userid", userid);
            obj.put("password", password);
            obj.put("name", name);
            obj.put("facedata", facedata);
            obj.put("isstudent", isstudent);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询所有课程信息
    public static List<JSONObject> queryClassInfo(Context context) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "classid",
                "coursename",
                "coursedescription"
        };

        Cursor cursor = db.query(
                "classinfo",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int classid = cursor.getInt(cursor.getColumnIndexOrThrow("classid"));
            String coursename = cursor.getString(cursor.getColumnIndexOrThrow("coursename"));
            String coursedescription = cursor.getString(cursor.getColumnIndexOrThrow("coursedescription"));

            JSONObject obj = new JSONObject();
            obj.put("classid", classid);
            obj.put("coursename", coursename);
            obj.put("coursedescription", coursedescription);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询某个课程信息
    public static List<JSONObject> queryClassInfo(Context context, int id) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "classid",
                "coursename",
                "coursedescription"
        };

        String selection = "classid = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                "classinfo",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                selection,                  // The columns for the WHERE clause
                selectionArgs,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int classid = cursor.getInt(cursor.getColumnIndexOrThrow("classid"));
            String coursename = cursor.getString(cursor.getColumnIndexOrThrow("coursename"));
            String coursedescription = cursor.getString(cursor.getColumnIndexOrThrow("coursedescription"));

            JSONObject obj = new JSONObject();
            obj.put("classid", classid);
            obj.put("coursename", coursename);
            obj.put("coursedescription", coursedescription);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询错题本信息
    public static List<JSONObject> queryErrorBook(Context context) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "bookid",
                "errorimg",
                "erroranalys"
        };

        Cursor cursor = db.query(
                "errorbook",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int bookid = cursor.getInt(cursor.getColumnIndexOrThrow("bookid"));
            byte[] errorimg = cursor.getBlob(cursor.getColumnIndexOrThrow("errorimg"));
            String erroranalys = cursor.getString(cursor.getColumnIndexOrThrow("erroranalys"));

            JSONObject obj = new JSONObject();
            obj.put("bookid", bookid);
            obj.put("errorimg", errorimg);
            obj.put("erroranalys", erroranalys);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询请假信息
    public static List<JSONObject> queryLeaveInfo(Context context) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "leaveid",
                "leavecontent",
                "leavestudentid",
                "leaveclassid",
                "leavedate"
        };

        Cursor cursor = db.query(
                "leaveinfo",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int leaveid = cursor.getInt(cursor.getColumnIndexOrThrow("leaveid"));
            String leavecontent = cursor.getString(cursor.getColumnIndexOrThrow("leavecontent"));
            int leavestudentid = cursor.getInt(cursor.getColumnIndexOrThrow("leavestudentid"));
            int leaveclassid = cursor.getInt(cursor.getColumnIndexOrThrow("leaveclassid"));
            String leavedate = cursor.getString(cursor.getColumnIndexOrThrow("leavedate"));

            JSONObject obj = new JSONObject();
            obj.put("leaveid", leaveid);
            obj.put("leavecontent", leavecontent);
            obj.put("leavestudentid", leavestudentid);
            obj.put("leaveclassid", leaveclassid);
            obj.put("leavedate", leavedate);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 查询AI请求信息
    public static List<JSONObject> queryAIRequest(Context context) throws JSONException {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "requestid",
                "requestcontent",
                "requestresult"
        };

        Cursor cursor = db.query(
                "airequest",   // The table to query
                projection,            // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // don't group the rows
                null,                  // don't filter by row groups
                null);                 // The sort order

        List<JSONObject> data = new ArrayList<>();

        while (cursor.moveToNext()) {
            int requestid = cursor.getInt(cursor.getColumnIndexOrThrow("requestid"));
            String requestcontent = cursor.getString(cursor.getColumnIndexOrThrow("requestcontent"));
            String requestresult = cursor.getString(cursor.getColumnIndexOrThrow("requestresult"));

            JSONObject obj = new JSONObject();
            obj.put("requestid", requestid);
            obj.put("requestcontent", requestcontent);
            obj.put("requestresult", requestresult);

            data.add(obj);
        }
        cursor.close();
        db.close();

        return data;
    }

    // 更新用户信息
    public static void updateUserInfo(Context context, int userid, int newPassword, String newName, byte[] newFacedata, boolean newIsStudent) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        values.put("name", newName);
        values.put("facedata", newFacedata);
        values.put("isstudent", newIsStudent);

        String selection = "userid = ?";
        String[] selectionArgs = {String.valueOf(userid)};

        db.update("userinfo", values, selection, selectionArgs);
        db.close();
    }

    // 更新课程信息
    public static void updateClassInfo(Context context, int classid, String newCoursename, String newCoursedescription) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("coursename", newCoursename);
        values.put("coursedescription", newCoursedescription);

        String selection = "classid = ?";
        String[] selectionArgs = {String.valueOf(classid)};

        db.update("classinfo", values, selection, selectionArgs);
        db.close();
    }

    // 更新错题本信息
    public static void updateErrorBook(Context context, int bookid, byte[] newErrorimg, String newErroranalys) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("errorimg", newErrorimg);
        values.put("erroranalys", newErroranalys);

        String selection = "bookid = ?";
        String[] selectionArgs = {String.valueOf(bookid)};

        db.update("errorbook", values, selection, selectionArgs);
        db.close();
    }

    // 更新请假信息
    public static void updateLeaveInfo(Context context, int leaveid, String newLeavecontent, int newLeavestudentid, int newLeaveclassid, String newLeavedate) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("leavecontent", newLeavecontent);
        values.put("leavestudentid", newLeavestudentid);
        values.put("leaveclassid", newLeaveclassid);
        values.put("leavedate", newLeavedate);

        String selection = "leaveid = ?";
        String[] selectionArgs = {String.valueOf(leaveid)};

        db.update("leaveinfo", values, selection, selectionArgs);
        db.close();
    }

    // 更新AI请求信息
    public static void updateAIRequest(Context context, int requestid, String newRequestcontent, String newRequestresult) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("requestcontent", newRequestcontent);
        values.put("requestresult", newRequestresult);

        String selection = "requestid = ?";
        String[] selectionArgs = {String.valueOf(requestid)};

        db.update("airequest", values, selection, selectionArgs);
        db.close();
    }

    // 删除用户信息
    public static void deleteUserInfo(Context context, int userid) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "userid = ?";
        String[] selectionArgs = {String.valueOf(userid)};

        db.delete("userinfo", selection, selectionArgs);
        db.close();
    }

    // 删除课程信息
    public static void deleteClassInfo(Context context, int classid) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "classid = ?";
        String[] selectionArgs = {String.valueOf(classid)};

        db.delete("classinfo", selection, selectionArgs);
        db.close();
    }

    // 删除错题本信息
    public static void deleteErrorBook(Context context, int bookid) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "bookid = ?";
        String[] selectionArgs = {String.valueOf(bookid)};

        db.delete("errorbook", selection, selectionArgs);
        db.close();
    }

    // 删除请假信息
    public static void deleteLeaveInfo(Context context, int leaveid) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "leaveid = ?";
        String[] selectionArgs = {String.valueOf(leaveid)};

        db.delete("leaveinfo", selection, selectionArgs);
        db.close();
    }

    // 删除AI请求信息
    public static void deleteAIRequest(Context context, int requestid) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = "requestid = ?";
        String[] selectionArgs = {String.valueOf(requestid)};

        db.delete("airequest", selection, selectionArgs);
        db.close();
    }
}
