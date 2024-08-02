//package online.manongbbq.aieducation.ai.VoiceToText;
//
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.provider.OpenableColumns;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.apache.commons.lang.StringEscapeUtils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.HashMap;
//
//import cn.hutool.json.JSONUtil;
//import online.manongbbq.aieducation.R;
//
//public class AudioUploadTask extends AsyncTask<Uri, String, String> {
//    private final Context context;
//
//    public AudioUploadTask(Context context) {
//        this.context = context.getApplicationContext(); // 使用应用上下文避免内存泄漏
//    }
//
//    @Override
//    protected void onPreExecute() {
//        Toast.makeText(context, "开始处理音频文件...", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    protected String doInBackground(Uri... uris) {
//        Uri audioUri = uris[0];
//        try {
//            // 将音频文件保存到本地
//            String audioFilePath = saveAudioFileLocally(audioUri);
//            publishProgress("音频文件保存成功: " + audioFilePath);
//
//            // 上传音频文件
//            String uploadResponse = Ifasrdemo.upload(this, audioFilePath);
//            publishProgress("音频文件上传成功: " + uploadResponse);
//
//            // 输出完整的上传响应
//            if (uploadResponse == null) {
//                publishProgress("上传响应为null");
//                return "上传失败: 上传响应为null";
//            }
//
//            publishProgress("上传响应: " + uploadResponse);
//
//            // 从响应中解析出 orderId
//            String jsonStr = StringEscapeUtils.unescapeJavaScript(uploadResponse);
//            publishProgress("解析后的JSON: " + jsonStr);
//
//            String orderId = String.valueOf(JSONUtil.getByPath(JSONUtil.parse(jsonStr), "content.orderId"));
//            publishProgress("解析的orderId: " + orderId);
//
//            if (orderId == null || orderId.isEmpty()) {
//                publishProgress("解析 orderId 失败: " + uploadResponse);
//                return "上传失败: 解析 orderId 失败";
//            }
//
//            // 轮询获取识别结果
//            String result = Ifasrdemo.getResult(orderId);
//            publishProgress("识别结果获取成功: " + result);
//
//            // 从识别结果中解析出文本
//            String recognizedText = parseRecognizedText(result);
//            publishProgress("识别文本解析成功: " + recognizedText);
//
//            return recognizedText; // 返回识别的文本
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "上传失败: " + e.getClass().getName() + ": " + e.getMessage();
//        }
//    }
//
//
//    @Override
//    protected void onProgressUpdate(String... values) {
//        for (String value : values) {
//            Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        if (result.startsWith("上传失败")) {
//            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(context, "语音转文字成功", Toast.LENGTH_LONG).show();
//            // 更新UI，例如显示识别文本
//            TextView textViewResult = ((Activity) context).findViewById(R.id.textViewResult);
//            textViewResult.append(result + "\n");
//        }
//    }
//
//    // 将音频文件保存到本地
//    private String saveAudioFileLocally(Uri audioUri) throws IOException {
//        ContentResolver contentResolver = context.getContentResolver();
//        String fileName = getFileName(audioUri);
//
//        File audioFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
//        try (InputStream inputStream = contentResolver.openInputStream(audioUri);
//             OutputStream outputStream = new FileOutputStream(audioFile)) {
//            if (inputStream == null) {
//                Toast.makeText(context, "无法打开音频文件流", Toast.LENGTH_SHORT).show();
//                throw new IOException("无法打开音频文件");
//            }
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//        }
//
//        if (!audioFile.exists() || audioFile.length() == 0) {
//            Toast.makeText(context, "音频文件保存失败", Toast.LENGTH_SHORT).show();
//            throw new IOException("音频文件保存失败");
//        }
//
//        Toast.makeText(context, "音频文件路径: " + audioFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
//        return audioFile.getAbsolutePath();
//    }
//
//
//    // 获取文件名
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    result = cursor.getString(index);
//                }
//            }
//        }
//        if (result == null) {
//            result = uri.getLastPathSegment();
//        }
//        return result;
//    }
//
//    // 解析识别结果文本
//    // 解析识别结果文本
//    private String parseRecognizedText(String jsonResult) {
//        if (jsonResult == null) {
//            Toast.makeText(context, "识别结果为null", Toast.LENGTH_SHORT).show();
//            throw new RuntimeException("识别结果为null");
//        }
//
//        Toast.makeText(context, "识别结果JSON: " + jsonResult, Toast.LENGTH_SHORT).show();
//
//        // 假设返回的 JSON 格式中有一个 "result" 字段包含识别的文本
//        String text = (String) JSONUtil.getByPath(JSONUtil.parse(jsonResult), "content.result");
//        if (text == null || text.isEmpty()) {
//            Toast.makeText(context, "解析识别文本失败", Toast.LENGTH_SHORT).show();
//            throw new RuntimeException("解析识别文本失败: " + jsonResult);
//        }
//        return text;
//    }
//
//}
