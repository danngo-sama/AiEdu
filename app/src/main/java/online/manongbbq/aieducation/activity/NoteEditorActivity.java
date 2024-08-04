package online.manongbbq.aieducation.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.ai.RobotAssistant;
import online.manongbbq.aieducation.model.Note;
import online.manongbbq.aieducation.activity.NoteStorage;

public class NoteEditorActivity extends AppCompatActivity {

    private EditText editTextNoteTitle;
    private EditText editTextNoteContent;
    private NoteStorage noteStorage;
    private Note currentNote;
    private Button buttonAiFeature;

    private RobotAssistant robotAssistant;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        // 设置Toolbar和返回按钮
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        robotAssistant=new RobotAssistant(this);

        // 初始化存储和笔记数据
        noteStorage = new NoteStorage(this);
        int noteId = getIntent().getIntExtra("noteId", -1);
        currentNote = noteStorage.getNoteById(noteId);

        // 设置笔记标题和内容编辑框
        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);
        buttonAiFeature = findViewById(R.id.buttonAiFeature);

        if (currentNote != null) {
            editTextNoteTitle.setText(currentNote.getTitle());
            editTextNoteContent.setText(currentNote.getContent());
        }

        // AI按钮功能
        buttonAiFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNoteContentWithAI();
            }
        });
    }

    private void processNoteContentWithAI() {
        // 获取当前笔记内容
        String originalContent = editTextNoteContent.getText().toString();

        String text2 = "我下面会给你一段笔记，我要你帮我把笔记整理一下，整理得更有逻辑、更有条理；你直接给我返回整理后的内容，不要给我发寒暄问暖的东西。\n";

        String Text = text2 + originalContent;

        String summary = robotAssistant.getAnswer(Text);

        // AI处理逻辑（暂时留空）
        // 例如调用一个API或者使用某个AI算法进行内容处理
        // String aiProcessedContent = callAIService(originalContent);

        // 暂时使用简单示例替代AI处理结果
        String aiProcessedContent = summary;  // 这个字符串应替换为实际AI处理后的内容

        // 将AI处理后的内容设置为笔记内容
        editTextNoteContent.setText(aiProcessedContent);

        // 提示用户内容已更新
        Toast.makeText(this, "笔记内容已通过AI处理", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveNote();
            finish(); // 返回上一个页面
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNote(); // 保存笔记
    }

    private void saveNote() {
        if (currentNote != null) {
            currentNote.setTitle(editTextNoteTitle.getText().toString());
            currentNote.setContent(editTextNoteContent.getText().toString());
            noteStorage.updateNote(currentNote);
        }
    }
}