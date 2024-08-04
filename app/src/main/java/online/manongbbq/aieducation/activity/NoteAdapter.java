package online.manongbbq.aieducation.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import online.manongbbq.aieducation.R;
import online.manongbbq.aieducation.model.Note;
import online.manongbbq.aieducation.activity.NoteStorage;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notes;
    private Context context;
    private NoteStorage noteStorage;

    public NoteAdapter(List<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
        this.noteStorage = new NoteStorage(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteContent.setText(note.getContent());

        // 设置点击事件以编辑笔记
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteEditorActivity.class);
            intent.putExtra("noteId", note.getId());
            context.startActivity(intent);
        });

        // 设置删除按钮事件
        holder.deleteButton.setOnClickListener(v -> {
            noteStorage.deleteNoteById(note.getId());
            notes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notes.size());
            Toast.makeText(context, "笔记已删除", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle;
        TextView noteContent;
        ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            deleteButton = itemView.findViewById(R.id.buttonDeleteNote);
        }
    }
}