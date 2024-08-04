package online.manongbbq.aieducation.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.model.Note;
import online.manongbbq.aieducation.activity.NoteStorage;

public class NoteBookActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotes;
    private NoteAdapter noteAdapter;
    private List<Note> notesList;
    private NoteStorage noteStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_book);

        // 设置Toolbar和返回按钮
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 初始化存储和笔记列表
        noteStorage = new NoteStorage(this);
        notesList = noteStorage.getNotes();

        // 设置RecyclerView
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(notesList, this);
        recyclerViewNotes.setAdapter(noteAdapter);

        // 设置FloatingActionButton以添加新笔记
        FloatingActionButton fabAddNote = findViewById(R.id.fabAddNote);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 创建一个新笔记
                Note newNote = new Note();
                notesList.add(newNote);
                noteStorage.addNote(newNote);
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(NoteBookActivity.this, "新笔记已创建", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // 关闭当前活动并返回到上一个页面
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 刷新笔记列表
        notesList.clear();
        notesList.addAll(noteStorage.getNotes());
        noteAdapter.notifyDataSetChanged();
    }
}