package online.manongbbq.aieducation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import online.manongbbq.aieducation.R;

public class RegisterActivity extends AppCompatActivity {
    private Button button;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        button = findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, HomepageStuActivity.class);
            startActivity(intent);
        });
    }

}
