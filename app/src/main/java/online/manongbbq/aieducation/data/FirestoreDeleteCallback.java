package online.manongbbq.aieducation.data;

/**
 * 删除回调接口
 *
 * @author liang zifan
 * @version 0.1.1
 * @see CloudDatabaseHelper
 * @since 07/09
 */
public interface FirestoreDeleteCallback {
    /**
     * 删除成功处理
     */
    void onDeleteSuccess();

    /**
     * 删除失败处理
     *
     * @param e 掷出的错误
     */
    void onDeleteFailure(Exception e);
}
