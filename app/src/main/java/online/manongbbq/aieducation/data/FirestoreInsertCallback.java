package online.manongbbq.aieducation.data;

/**
 * 存储数据回调接口
 *
 * @author liang zifan
 * @version 0.1.1
 * @see CloudDatabaseHelper
 * @since 07/09
 */
public interface FirestoreInsertCallback {
    /**
     * 存储成功处理
     */
    void onStoreSuccess();

    /**
     * 存储失败处理
     *
     * @param e 掷出的错误
     */
    void onStoreFailure(Exception e);
}

