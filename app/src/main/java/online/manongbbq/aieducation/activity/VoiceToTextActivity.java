package online.manongbbq.aieducation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.ai.AiSummary;
import online.manongbbq.aieducation.ai.RobotAssistant;

public class VoiceToTextActivity extends AppCompatActivity {

    private static final String TAG = "VoiceToTextActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String AUDIO_FILE_PATH = "/sdcard/recorded_audio.3gp";

    private MediaRecorder mediaRecorder;
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
        Button buttonStopRecording = findViewById(R.id.buttonStopRecording);
        textViewResult = findViewById(R.id.textViewResult);

        buttonBack.setOnClickListener(v -> finish());

        buttonAiSummary.setOnClickListener(v -> showAiSummary());

        buttonVoiceToText.setOnClickListener(v -> startVoiceRecognition());

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
                handleSpeechRecognizerError(error);
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

    private void startVoiceRecognition() {
        try {
            speechRecognizer.startListening(recognizerIntent);
            Log.d(TAG, "Started listening.");
        } catch (Exception e) {
            Toast.makeText(this, "语音识别不可用", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Speech recognition is not available.", e);
        }
    }

    private void stopVoiceRecognition() {
        try {
            speechRecognizer.stopListening();
            Log.d(TAG, "Stopped listening.");
        } catch (Exception e) {
            Toast.makeText(this, "无法停止语音识别", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to stop speech recognition.", e);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void handleSpeechRecognizerError(int error) {
        Log.d(TAG, "onError: " + error);
        String errorMessage;
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "音频问题";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "客户端错误";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "权限不足";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "网络错误";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "网络超时";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "没有匹配";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "识别服务忙";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "服务错误";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "没有语音输入";
                break;
            default:
                errorMessage = "未知错误";
                break;
        }
        Toast.makeText(VoiceToTextActivity.this, "语音识别出错: " + errorMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Stopping recognition due to error: " + errorMessage);

        // 如果错误是识别器繁忙，添加延迟后重启识别器
        if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
            new Handler().postDelayed(this::startVoiceRecognition, 1000);
        }
    }
}