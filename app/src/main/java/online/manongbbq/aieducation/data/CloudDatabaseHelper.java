package online.manongbbq.aieducation.data;

import android.util.Log;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * data to cloud database.
 * <p>
 *     <p>数据至云数据库</p>
 *     <ui>
 *         <li>{@link #insertUserInfo(int, int, String, byte[], boolean, List, FirestoreInsertCallback)}
 *         <li>{@link #insertClassInfo(int, int, List, String, String, byte[], byte[], String, String, FirestoreInsertCallback)}
 *         <li>{@link #queryUserInfo(FirestoreQueryCallback)}
 *         <li>{@link #queryUserInfo(int, FirestoreQueryCallback)}
 *         <li>{@link #queryClassInfo(FirestoreQueryCallback)}
 *         <li>{@link #queryUserInfo(int, FirestoreQueryCallback)}
 *         <li>{@link #updateUserInfo(int, Map, FirestoreUpdateCallback)}
 *         <li>{@link #updateClassInfo(int, Map, FirestoreUpdateCallback)}
 *         <li>{@link #addClassToStudent(int, int, FirestoreUpdateCallback)}
 *         <li>{@link #addStudentToClass(int, int, FirestoreUpdateCallback)}
 *         <li>{@link #removeClassFromStudent(int, int, FirestoreUpdateCallback)}
 *         <li>{@link #removeStudentFromClass(int, int, FirestoreUpdateCallback)}
 *         <li>{@link #deleteUserInfoById(int, FirestoreDeleteCallback)}
 *         <li>{@link #deleteClassInfoById(int, FirestoreDeleteCallback)}
 *     </ui>
 * </p>
 *
 * @author liang zifan
 * @version 0.1.1
 * @since 07/09
 * @see FirestoreQueryCallback
 * @see FirestoreInsertCallback
 * @see FirestoreUpdateCallback
 * @see FirestoreDeleteCallback
 * @see FirebaseFirestore
 */
public class CloudDatabaseHelper {

    private FirebaseFirestore db;

    public CloudDatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * <p>存储用户信息</p>
     * <p><pre>{@code
     *     cloudDbHelper.storeUserInfo(user, new FirestoreInsertCallback() {
     *     @Override
     *     public void onStoreSuccess() {
     *         runOnUiThread(() -> Toast.makeText(MainActivity.this, "Store successful", Toast.LENGTH_SHORT).show());
     *     }
     *
     *     @Override
     *     public void onStoreFailure(Exception e) {
     *         runOnUiThread(() -> Toast.makeText(MainActivity.this, "Store failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
     *     }
     *     });}
     * </pre></p>
     *
     * @param userId    账户：学号/工号
     * @param password  密码
     * @param name      姓名
     * @param faceData  面部信息
     * @param isStudent 是否学生标记
     * @param classIds  所在课程列表
     * @param callback  回调
     * @see FirestoreInsertCallback
     */
    public void insertUserInfo(int userId, int password, String name, byte[] faceData, boolean isStudent,
                               List<Integer> classIds, FirestoreInsertCallback callback) {
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("password", password);
        user.put("name", name);
        user.put("faceData", faceData);
        user.put("isStudent", isStudent);
        user.put("classIds", classIds);

        db.collection("userInfo")
                .add(user)
                .addOnSuccessListener(documentReference -> callback.onStoreSuccess())
                .addOnFailureListener(e -> callback.onStoreFailure(e));
    }


    /**
     * 查询所有用户信息
     * <p>
     * <p>因为数据库是异步查询，<strong>你必须按照以下样例的方式在{@code activity}中使用</strong></p>
     * <pre>for example:
     * {@code
     * cloudDbHelper = new CloudDatabaseHelper();
     *
     * // 查询用户信息并处理结果
     * cloudDbHelper.queryUserInfo(new FirestoreQueryCallback() {
     *     @Override
     *     public void onCallback(List<Map<String, Object>> userList) {
     *         // 处理查询结果
     *         for (Map<String, Object> user : userList) {
     *             Log.d("Firestore", "User: " + user);
     *         }
     *
     *         // 例如：将查询结果显示在UI中
     *         TextView textView = findViewById(R.id.textView);
     *         StringBuilder sb = new StringBuilder();
     *         for (Map<String, Object>> user : userList) {
     *             sb.append(user.toString()).append("\n");
     *         }
     *         extView.setText(sb.toString());
     *     }
     * });}
     * </pre>
     * </p>
     *
     * @param callback 回调接口
     * @see FirestoreQueryCallback
     */
    public void queryUserInfo(FirestoreQueryCallback callback) {
        db.collection("userInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.getData());
                        }
                        callback.onCallback(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }


    /**
     * 查询某个用户信息
     * <p>
     * <p>因为数据库是异步查询，<strong>你必须按照以下样例的方式在{@code activity}中使用</strong></p>
     * <pre>for example:
     * {@code
     * cloudDbHelper = new CloudDatabaseHelper();
     *
     * int userId = 1; // 假设你要查询的用户ID是1
     *
     * // 根据用户ID查询用户信息
     * cloudDbHelper.queryUserInfoById(userId, new FirestoreQueryCallback() {
     *      @Override
     *      public void onCallback(List<Map<String, Object>> userList) {
     *          StringBuilder sb = new StringBuilder();
     *          if (userList.isEmpty()) {
     *              sb.append("User not found");
     *          } else {
     *              for (Map<String, Object> user : userList) {
     *                  sb.append(user.toString()).append("\n");
     *              }
     *          }
     *          runOnUiThread(() -> textView.setText(sb.toString()));
     *      }
     * });}
     * </pre>
     * </p>
     *
     * @param userId   要查询的用户ID
     * @param callback 回调接口
     * @see FirestoreQueryCallback
     */
    public void queryUserInfo(int userId, FirestoreQueryCallback callback) {
        db.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.getData());
                        }
                        callback.onCallback(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        callback.onCallback(new ArrayList<>()); // Return an empty list in case of error
                    }
                });
    }


    /**
     * 更新用户信息
     * <p>以下为一个使用样例</p>
     * <pre>for example:
     * {@code
     * cloudDbHelper = new CloudDatabaseHelper();
     *
     * int userId = 1; // 假设你要更新的用户ID是1
     *
     * // 准备要更新的数据
     * Map<String, Object> updates = new HashMap<>();
     * updates.put("name", "New Name");
     * updates.put("password", 123456);
     *
     * // 根据用户ID更新用户信息
     * cloudDbHelper.updateUserInfoById(userId, updates, new FirestoreUpdateCallback() {
     *      @Override
     *      public void onUpdateSuccess() {
     *          runOnUiThread(() -> Toast.makeText(MainActivity.this, "Update successful", Toast.LENGTH_SHORT).show());
     *      }
     *
     *      @Override
     *      public void onUpdateFailure(Exception e) {
     *          runOnUiThread(() -> Toast.makeText(MainActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
     *      }
     * });}
     * </pre>
     *
     * @param userId   更新用户的id
     * @param updates  要更新的信息，储存在Map对象中
     * @param callback 回调接口
     * @see FirestoreUpdateCallback
     */
    public void updateUserInfo(int userId, Map<String, Object> updates, FirestoreUpdateCallback callback) {
        db.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("userInfo")
                                .document(docId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "User not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }


    /**
     * 更新userId中的classIds列表，向列表中加入
     *
     * @param userId     用户id
     * @param newClassId 加入的班级id
     * @param callback   回调
     * @see FirestoreUpdateCallback
     */
    public void addClassToStudent(int userId, int newClassId, FirestoreUpdateCallback callback) {
        db.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("userInfo")
                                .document(docId)
                                .update("classIds", FieldValue.arrayUnion(newClassId))
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "User not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }

    /**
     * 更新userId中的classIds列表(删除条目)
     *
     * @param userId          用户id
     * @param classIdToRemove 删除的课程id
     * @param callback        回调
     * @see FirestoreDeleteCallback
     */
    public void removeClassFromStudent(int userId, int classIdToRemove, FirestoreUpdateCallback callback) {
        db.collection("userinfo")
                .whereEqualTo("userid", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String documentId = task.getResult().getDocuments().get(0).getId();
                        db.collection("userinfo").document(documentId)
                                .update("classIds", FieldValue.arrayRemove(classIdToRemove))
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "User not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }


    /**
     * 删除用户信息
     * <p>以下为一个使用样例</p>
     * <pre>for example:
     * {@code
     * int userId = 1; // 假设你要删除的用户ID是1
     *
     * // 删除用户信息
     * cloudDbHelper.deleteUserInfoById(userId, new FirestoreDeleteCallback() {
     *      @Override
     *          public void onDeleteSuccess() {
     *              runOnUiThread(() -> Toast.makeText(MainActivity.this, "Delete successful", Toast.LENGTH_SHORT).show());
     *          }
     *
     *      @Override
     *          public void onDeleteFailure(Exception e) {
     *              runOnUiThread(() -> Toast.makeText(MainActivity.this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
     *          }
     * });}
     * </pre>
     *
     * @param userId   删除用户的id
     * @param callback 回调
     */
    public void deleteUserInfoById(int userId, FirestoreDeleteCallback callback) {
        db.collection("userinfo")
                .whereEqualTo("userid", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("userinfo")
                                .document(docId)
                                .delete()
                                .addOnSuccessListener(aVoid -> callback.onDeleteSuccess())
                                .addOnFailureListener(e -> callback.onDeleteFailure(e));
                    } else {
                        Log.w("Firestore", "User not found or error getting documents.", task.getException());
                        callback.onDeleteFailure(task.getException());
                    }
                });
    }

    /**
     * Stores class information in Firestore.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.insertClassInfo(courseId, teacherId, studentIds, courseName, courseDescription,
     *     location, classAudio, classContent, classSummary, new FirestoreInsertCallback() {
     *     @Override
     *     public void onStoreSuccess() {
     *         // Handle success
     *     }
     *     @Override
     *     public void onStoreFailure(Exception e) {
     *         // Handle failure
     *     }
     * });}
     * </pre>
     *
     * @param courseId          The ID of the course.
     * @param teacherId         The ID of the teacher.
     * @param studentIds        A list of student IDs associated with the class.学生列表
     * @param courseName        The name of the course.
     * @param courseDescription A description of the course.课堂描述
     * @param location          The location of the class (a byte array representing a map or coordinates).地理位置
     * @param classAudio        The audio recording of the class (a byte array of audio data).音频
     * @param classContent      The content of the class (lecture notes).内容
     * @param classSummary      A summary of the class.总结
     * @param callback          A callback to handle the result of the operation.
     * @see FirestoreInsertCallback
     */
    public void insertClassInfo(int courseId, int teacherId, List<Integer> studentIds, String courseName,
                                String courseDescription, byte[] location, byte[] classAudio,
                                String classContent, String classSummary, FirestoreInsertCallback callback) {
        Map<String, Object> classInfo = new HashMap<>();
        classInfo.put("courseId", courseId);
        classInfo.put("teacherId", teacherId);
        classInfo.put("studentIds", studentIds);
        classInfo.put("courseName", courseName);
        classInfo.put("courseDescription", courseDescription);
        classInfo.put("location", location);
        classInfo.put("classAudio", classAudio);
        classInfo.put("classContent", classContent);
        classInfo.put("classSummary", classSummary);

        db.collection("classInfo")
                .add(classInfo)
                .addOnSuccessListener(documentReference -> callback.onStoreSuccess())
                .addOnFailureListener(e -> callback.onStoreFailure(e));
    }

    /**
     * Queries all class information from Firestore.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.queryClassInfo(new FirestoreQueryCallback() {
     *     @Override
     *     public void onCallback(List<Map<String, Object>> classList) {
     *         // Handle the retrieved class information
     *     }
     * });}
     * </pre>
     *
     * @param callback A callback to handle the results of the query.
     * @see FirestoreQueryCallback
     */
    public void queryClassInfo(FirestoreQueryCallback callback) {
        db.collection("classInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> classList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            classList.add(document.getData());
                        }
                        callback.onCallback(classList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        callback.onCallback(new ArrayList<>()); // Return an empty list in case of error
                    }
                });
    }

    /**
     * Queries class information by course ID from Firestore.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.queryClassInfo(courseId, new FirestoreQueryCallback() {
     *     @Override
     *     public void onCallback(List<Map<String, Object>> classList) {
     *         // Handle the retrieved class information
     *     }
     * });}
     * </pre>
     *
     * @param courseId The ID of the course to query.
     * @param callback A callback to handle the results of the query.
     * @see FirestoreQueryCallback
     */
    public void queryClassInfo(int courseId, FirestoreQueryCallback callback) {
        db.collection("classInfo")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> classList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            classList.add(document.getData());
                        }
                        callback.onCallback(classList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        callback.onCallback(new ArrayList<>()); // Return an empty list in case of error
                    }
                });
    }

    /**
     * Updates class information in Firestore by course ID.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * Map<String, Object> updates = new HashMap<>();
     * updates.put("courseName", "New Course Name");
     * cloudDbHelper.updateClassInfo(courseId, updates, new FirestoreUpdateCallback() {
     *     @Override
     *     public void onUpdateSuccess() {
     *         // Handle update success
     *     }
     *     @Override
     *     public void onUpdateFailure(Exception e) {
     *         // Handle update failure
     *     }
     * });}
     * </pre>
     *
     * @param courseId The ID of the course to update.
     * @param updates  A map containing the fields to update and their new values.
     * @param callback A callback to handle the result of the update operation.
     * @see FirestoreUpdateCallback
     */
    public void updateClassInfo(int courseId, Map<String, Object> updates, FirestoreUpdateCallback callback) {
        db.collection("classInfo")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("classInfo")
                                .document(docId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "Class not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }

    /**
     * Updates the studentIds list in a classInfo document by adding a new student ID.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.addStudentToClass(courseId, newStudentId, new FirestoreUpdateCallback() {
     *     @Override
     *     public void onUpdateSuccess() {
     *         // Handle success
     *     }
     *     @Override
     *     public void onUpdateFailure(Exception e) {
     *         // Handle failure
     *     }
     * });}
     * </pre>
     *
     * @param courseId     The ID of the course.
     * @param newStudentId The ID of the student to add.
     * @param callback     A callback to handle the result of the operation.
     * @see FirestoreUpdateCallback
     */
    public void addStudentToClass(int courseId, int newStudentId, FirestoreUpdateCallback callback) {
        db.collection("classInfo")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("classInfo")
                                .document(docId)
                                .update("studentIds", FieldValue.arrayUnion(newStudentId))
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "Class not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }

    /**
     * Updates the studentIds list in a classInfo document by removing a student ID.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.removeStudentFromClass(courseId, studentIdToRemove, new FirestoreUpdateCallback() {
     *     @Override
     *     public void onUpdateSuccess() {
     *         // Handle success
     *     }
     *     @Override
     *     public void onUpdateFailure(Exception e) {
     *         // Handle failure
     *     }
     * });}
     * </pre>
     *
     * @param courseId        The ID of the course.
     * @param studentIdToRemove The ID of the student to remove.
     * @param callback        A callback to handle the result of the operation.
     * @see FirestoreUpdateCallback
     */
    public void removeStudentFromClass(int courseId, int studentIdToRemove, FirestoreUpdateCallback callback) {
        db.collection("classInfo")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("classInfo")
                                .document(docId)
                                .update("studentIds", FieldValue.arrayRemove(studentIdToRemove))
                                .addOnSuccessListener(aVoid -> callback.onUpdateSuccess())
                                .addOnFailureListener(e -> callback.onUpdateFailure(e));
                    } else {
                        Log.w("Firestore", "Class not found or error getting documents.", task.getException());
                        callback.onUpdateFailure(task.getException());
                    }
                });
    }

    /**
     * Deletes class information from Firestore by course ID.
     *
     * <p>Usage example:</p>
     * <pre>{@code
     * cloudDbHelper.deleteClassInfoById(courseId, new FirestoreDeleteCallback() {
     *     @Override
     *     public void onDeleteSuccess() {
     *         // Handle delete success
     *     }
     *     @Override
     *     public void onDeleteFailure(Exception e) {
     *         // Handle delete failure
     *     }
     * });}
     * </pre>
     *
     * @param courseId The ID of the course to delete.
     * @param callback A callback to handle the result of the delete operation.
     * @see FirestoreDeleteCallback
     */
    public void deleteClassInfoById(int courseId, FirestoreDeleteCallback callback) {
        db.collection("classInfo")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("classInfo")
                                .document(docId)
                                .delete()
                                .addOnSuccessListener(aVoid -> callback.onDeleteSuccess())
                                .addOnFailureListener(e -> callback.onDeleteFailure(e));
                    } else {
                        Log.w("Firestore", "Class not found or error getting documents.", task.getException());
                        callback.onDeleteFailure(task.getException());
                    }
                });
    }
}

