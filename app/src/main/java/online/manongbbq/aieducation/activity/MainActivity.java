package online.manongbbq.aieducation.activity;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;

public class MainActivity extends AppCompatActivity {
    private TextView textViewButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MyTag", "跳转函数已经被调用！");
        textViewButton = findViewById(R.id.textView5);
        textViewButton.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}