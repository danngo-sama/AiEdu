package online.manongbbq.aieducation.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.hutool.json.JSONUtil;
import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.ai.AiSummary;
import online.manongbbq.aieducation.ai.RobotAssistant;
//import online.manongbbq.aieducation.ai.VoiceToText.AudioUploadTask;
import online.manongbbq.aieducation.ai.VoiceToText.Ifasrdemo;



public class VoiceToTextActivity extends AppCompatActivity {

    private static final String TAG = "VoiceToTextActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int REQUEST_CODE_RECORD_AUDIO = 400;

    private TextView textViewDebug;

    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private TextView textViewResult;
    private AiSummary aiSummary;
    private RobotAssistant robotAssistant;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_to_text);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonAiSummary = findViewById(R.id.buttonAiSummary);
        Button buttonVoiceToText = findViewById(R.id.buttonVoiceToText);
        textViewResult = findViewById(R.id.textViewResult);

        buttonBack.setOnClickListener(v -> finish());

        buttonAiSummary.setOnClickListener(v -> showAiSummary());

        buttonVoiceToText.setOnClickListener(v -> startVoiceRecording());

        setupSpeechRecognizer();


        robotAssistant = new RobotAssistant(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // 可选: 可以在这里处理音量变化
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // 可选: 可以在这里处理音频缓冲
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech");
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                Log.d(TAG, "onResults");
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    textViewResult.append(recognizedText + "\n");
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // 可选: 可以在这里处理部分识别结果
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // 可选: 可以在这里处理其他事件
            }
        });

        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    private void startVoiceRecording() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_CODE_RECORD_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECORD_AUDIO && resultCode == RESULT_OK) {
            Uri audioUri = data.getData();
            if (audioUri != null) {
                convertAudioToText(audioUri);
            }
        }
    }




    private void convertAudioToText(Uri audioUri) {
//        // 检查音频 URI
//        if (audioUri == null) {
//            appendDebugLog("音频 URI 为 null");
//            return;
//        }
//
//        // 确保 Context 有效
//        if (this == null) {
//            appendDebugLog("Context 为 null");
//            return;
//        }

        // 方法级别声明 executor 和 handler
        ExecutorService executor;
        Handler handler;

        try {
            executor = Executors.newSingleThreadExecutor();
            handler = new Handler(Looper.getMainLooper());
        } catch (Exception e) {
//            appendDebugLog("ExecutorService 创建失败: " + e.getMessage());
            return;
        }

        // 打印步骤0日志
//        appendDebugLog("步骤0: 准备进入后台线程");

        executor.execute(() -> {
            try {
                // 将音频文件保存到本地
                String audioFilePath = saveAudioFileLocally(audioUri);
//                handler.post(() -> appendDebugLog("步骤2: 音频文件保存成功: " + audioFilePath));

                // 上传音频文件
                String uploadResponse = Ifasrdemo.upload(this, audioFilePath);
                if (uploadResponse == null) {
//                    handler.post(() -> appendDebugLog("上传响应为null"));
                    return;
                }

//                handler.post(() -> appendDebugLog("步骤3: 上传响应成功: " + uploadResponse));

                // 解析 orderId
                String jsonStr = StringEscapeUtils.unescapeJavaScript(uploadResponse);
                String orderId = String.valueOf(JSONUtil.getByPath(JSONUtil.parse(jsonStr), "content.orderId"));

                if (orderId == null || orderId.isEmpty()) {
//                    handler.post(() -> appendDebugLog("解析 orderId 失败: " + uploadResponse));
                    return;
                }

//                appendDebugLog("步骤4");

                // 获取识别结果
                try {
//                    appendDebugLog("步骤5");
                    String result = Ifasrdemo.getResult(this, orderId);
//                    appendDebugLog("步骤6");
                    String recognizedText = parseRecognizedText(result);
//                    appendDebugLog("步骤7");
                    handler.post(() -> {
                        if (recognizedText != null) {
                            textViewResult.append(recognizedText + "\n");
//                            appendDebugLog("识别结果成功: " + recognizedText);
                        }
                    });
                } catch (SignatureException e) {
//                    handler.post(() -> appendDebugLog("获取识别结果失败: 签名错误"));
                } catch (InterruptedException e) {
//                    handler.post(() -> appendDebugLog("获取识别结果失败: 任务被中断"));
                    Thread.currentThread().interrupt(); // 重新设置线程中断状态
                } catch (IOException e) {
//                    handler.post(() -> appendDebugLog("获取识别结果失败: 网络错误"));
                }

            } catch (Exception e) {
//                handler.post(() -> appendDebugLog("上传失败: " + e.getClass().getName() + ": " + e.getMessage()));
            }
        });
    }


    private String saveAudioFileLocally(Uri audioUri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        String fileName = getFileName(audioUri);

        File audioFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
        try (InputStream inputStream = contentResolver.openInputStream(audioUri);
             OutputStream outputStream = new FileOutputStream(audioFile)) {
            if (inputStream == null) {
//                appendDebugLog("无法打开音频文件流");
                throw new IOException("无法打开音频文件");
            }

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        if (!audioFile.exists() || audioFile.length() == 0) {
//            appendDebugLog("音频文件保存失败");
            throw new IOException("音频文件保存失败");
        }

//        appendDebugLog("音频文件路径: " + audioFile.getAbsolutePath());
        return audioFile.getAbsolutePath();
    }


    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }


    private String parseRecognizedText(String jsonString) {
        // 初始化一个StringBuilder来存储转写内容
        StringBuilder transcriptBuilder = new StringBuilder();

        try {
            // 将字符串解析为JSON对象
            JSONObject jsonObject = new JSONObject(jsonString);

            // 定位到“orderResult”部分并将其解析为JSON对象
            JSONObject contentObject = jsonObject.getJSONObject("content");
            JSONObject orderResultObject = new JSONObject(contentObject.getString("orderResult"));

            // 从"lattice"数组中提取转写文本
            JSONArray latticeArray = orderResultObject.getJSONArray("lattice");

            // 遍历lattice数组中的每个元素
            for (int i = 0; i < latticeArray.length(); i++) {
                JSONObject latticeObject = latticeArray.getJSONObject(i);
                JSONObject json1BestObject = new JSONObject(latticeObject.getString("json_1best"));
                JSONObject stObject = json1BestObject.getJSONObject("st");
                JSONArray rtArray = stObject.getJSONArray("rt");

                // 遍历每个rt数组中的ws数组
                for (int j = 0; j < rtArray.length(); j++) {
                    JSONObject rtObject = rtArray.getJSONObject(j);
                    JSONArray wsArray = rtObject.getJSONArray("ws");

                    // 提取每个ws数组中的cw数组中的w字段值
                    for (int k = 0; k < wsArray.length(); k++) {
                        JSONObject wsObject = wsArray.getJSONObject(k);
                        JSONArray cwArray = wsObject.getJSONArray("cw");

                        // 连接所有的w字段值到transcriptBuilder
                        for (int l = 0; l < cwArray.length(); l++) {
                            JSONObject cwObject = cwArray.getJSONObject(l);
                            transcriptBuilder.append(cwObject.getString("w"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 在异常情况下返回错误信息
            return "解析失败: " + e.getMessage();
        }

        // 返回构建好的转写文本
        return transcriptBuilder.toString();
    }


    private void showAiSummary() {
        String text = textViewResult.getText().toString();
        if (text.isEmpty()) {
            Toast.makeText(this, "请先进行语音转文字", Toast.LENGTH_SHORT).show();
            return;
        }

        String text2 = "我下面会给你一段课堂内容，你帮我进行一下课堂总结\n";

        String Text = text2 + text;

        String summary = robotAssistant.getAnswer(Text);

        new AlertDialog.Builder(this)
                .setTitle("AI总结")
                .setMessage(summary)
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}




