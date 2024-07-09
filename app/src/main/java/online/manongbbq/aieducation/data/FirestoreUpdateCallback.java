package online.manongbbq.aieducation.data;

/**
 * 更新数据回调接口
 *
 * @author liang zifan
 * @version 0.1.1
 * @see CloudDatabaseHelper
 * @since 07/09
 */
public interface FirestoreUpdateCallback {
    /**
     * 更新成功处理
     */
    void onUpdateSuccess();

    /**
     * 更新失败处理
     *
     * @param e 掷出的错误
     */
    void onUpdateFailure(Exception e);
}
