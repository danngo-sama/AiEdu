package online.manongbbq.aieducation.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.ai.RobotAssistant;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText editTextQuestion;
    private Button buttonSend, buttonBack;
    private RobotAssistant robotAssistant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatLayout = findViewById(R.id.chatLayout);
        editTextQuestion = findViewById(R.id.editTextQuestion);
        buttonSend = findViewById(R.id.buttonSend);
        buttonBack = findViewById(R.id.buttonBack);

        robotAssistant = new RobotAssistant(this); // 传递Context参数

        buttonSend.setOnClickListener(v -> sendQuestion());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void sendQuestion() {
        String question = editTextQuestion.getText().toString().trim();
        if (question.isEmpty()) {
            Toast.makeText(this, "请输入问题", Toast.LENGTH_SHORT).show();
            return;
        }

        addMessageToChat("你: " + question, true);
        String answer = getAnswer(question);
        addMessageToChat("AI: " + answer, false);

        editTextQuestion.setText("");
    }

    private void addMessageToChat(String message, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextColor(getResources().getColor(android.R.color.black));
        textView.setBackgroundResource(R.drawable.chat_bubble_user); // 设置背景

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);

        if (isUser) {
            params.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.chat_bubble_user); // 用户消息背景
        } else {
            params.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.chat_bubble_ai); // AI消息背景
        }

        textView.setLayoutParams(params);
        chatLayout.addView(textView);
    }

    private String getAnswer(String question) {
        Log.d("MyTag", "chat函数已经被调用！");
        String answer = robotAssistant.getAnswer(question); // 使用已经初始化的robotAssistant
        Log.d("MyTag", "返回内容是" + answer);
        return answer;
    }
}