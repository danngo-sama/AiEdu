package online.manongbbq.aieducation.data;

import java.util.List;
import java.util.Map;

/**
 * 存储回调接口
 *
 * @author liang zifan
 * @see CloudDatabaseHelper
 * @version 0.1.1
 * @since 07/09
 */
public interface FirestoreQueryCallback {
    void onCallback(List<Map<String, Object>> userList);
}
