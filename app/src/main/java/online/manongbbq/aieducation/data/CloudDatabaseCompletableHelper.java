package online.manongbbq.aieducation.data;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @deprecated
 */
public class CloudDatabaseCompletableHelper {

    private FirebaseFirestore db;

    public CloudDatabaseCompletableHelper() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * 存储用户信息
     *
     * @param userId    账户：学号/工号
     * @param password  密码
     * @param name      姓名
     * @param faceData  面部信息
     * @param isStudent 是否学生标记
     * @param classIds  所在课程列表
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> insertUserInfo(int userId, int password, String name, byte[] faceData, boolean isStudent,
                                                  List<Integer> classIds) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId);
        user.put("password", password);
        user.put("name", name);
        user.put("faceData", faceData);
        user.put("isStudent", isStudent);
        user.put("classIds", classIds);

        db.collection("userInfo")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                    future.complete(null);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                    future.completeExceptionally(e);
                });

        return future;
    }

    /**
     * 查询所有用户信息
     *
     * @return CompletableFuture<List<Map<String, Object>>>
     */
    public CompletableFuture<List<Map<String, Object>>> queryUserInfo() {
        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();

        db.collection("userInfo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.getData());
                        }
                        future.complete(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * 查询某个用户信息
     *
     * @param userId 要查询的用户ID
     * @return CompletableFuture<List<Map<String, Object>>>
     */
    public CompletableFuture<List<Map<String, Object>>> queryUserInfo(int userId) {
        CompletableFuture<List<Map<String, Object>>> future = new CompletableFuture<>();

        db.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userList.add(document.getData());
                        }
                        future.complete(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * 更新用户信息
     *
     * @param userId 更新用户的id
     * @param updates 要更新的信息，储存在Map对象中
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> updateUserInfo(int userId, Map<String, Object> updates) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        db.collection("userInfo")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        db.collection("userInfo")
                                .document(docId)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> future.complete(null))
                                .addOnFailureListener(e -> future.completeExceptionally(e));
                    } else {
                        Log.w("Firestore", "User not found or error getting documents.", task.getException());
                        future.completeExceptionally(task.getException());
                    }
                });

        return future;
    }

    /**
     * 删除用户信息
     *
     * @param documentId 要删除的文档ID
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> deleteUserInfo(String documentId) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        db.collection("userInfo").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully deleted!");
                    future.complete(null);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting document", e);
                    future.completeExceptionally(e);
                });

        return future;
    }
}
