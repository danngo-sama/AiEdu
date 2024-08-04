package online.manongbbq.aieducation.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import online.manongbbq.aieducation.model.Note;

public class NoteStorage {

    private static final String PREFS_NAME = "notes_pref";
    private static final String NOTES_KEY = "notes";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public NoteStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<Note> getNotes() {
        String json = sharedPreferences.getString(NOTES_KEY, "");
        Type type = new TypeToken<List<Note>>() {}.getType();
        return json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, type);
    }

    public Note getNoteById(int id) {
        List<Note> notes = getNotes();
        for (Note note : notes) {
            if (note.getId() == id) {
                return note;
            }
        }
        return null;
    }

    public void saveNotes(List<Note> notes) {
        String json = gson.toJson(notes);
        sharedPreferences.edit().putString(NOTES_KEY, json).apply();
    }

    public void updateNote(Note updatedNote) {
        List<Note> notes = getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == updatedNote.getId()) {
                notes.set(i, updatedNote);
                break;
            }
        }
        saveNotes(notes);
    }

    public void addNote(Note newNote) {
        List<Note> notes = getNotes();
        notes.add(newNote);
        saveNotes(notes);
    }

    public void deleteNoteById(int id) {
        List<Note> notes = getNotes();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id) {
                notes.remove(i);
                break;
            }
        }
        saveNotes(notes);
    }
}