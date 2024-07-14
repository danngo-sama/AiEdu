package online.manongbbq.aieducation.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;
//import online.manongbbq.aieducation.ai.RobotAssistant;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatLayout;
    private EditText editTextQuestion;
    private Button buttonSend,buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatLayout = findViewById(R.id.chatLayout);
        editTextQuestion = findViewById(R.id.editTextQuestion);
        buttonSend = findViewById(R.id.buttonSend);
        buttonBack = findViewById(R.id.buttonBack);

        buttonSend.setOnClickListener(v -> sendQuestion());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void sendQuestion() {
        String question = editTextQuestion.getText().toString().trim();
        if (question.isEmpty()) {
            Toast.makeText(this, "请输入问题", Toast.LENGTH_SHORT).show();
            return;
        }

        addMessageToChat("你: " + question);
        String answer = getAnswer(question);
        addMessageToChat("AI: " + answer);

        editTextQuestion.setText("");
    }

    private void addMessageToChat(String message) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setPadding(8, 8, 8, 8);
        chatLayout.addView(textView);
    }

    private String getAnswer(String question) {
//        Log.d("MyTag", "chat函数已经被调用！");
//        // Placeholder for the actual AI answer fetching logic
//        String answer1= new RobotAssistant().getAnswer(question);
//        System.out.print(answer1);
//        Log.d("MyTag", "返回内容是"+answer1);
//        return answer1;
        return question;
    }
}
