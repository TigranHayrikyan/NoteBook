package com.example.note;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.note.Note;
import com.example.note.NoteDao;
import com.example.note.NoteDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteRepository {
    private final NoteDao noteDao;
    LiveData<List<Note>> notes;

    ExecutorService executor = Executors.newSingleThreadExecutor();


    public NoteRepository(Application application) {
        NoteDatabase noteDatabase = NoteDatabase.getInstance(application);
        noteDao = noteDatabase.noteDao();
        notes = noteDao.getAllNotes();

    }

    public void insert(Note note) {

        executor.execute(() -> noteDao.insert(note));

    }

    public void update(Note note) {
        executor.execute(() -> noteDao.update(note));
    }


    public void delete(Note note) {
        executor.execute(() -> noteDao.delete(note));

    }


    public LiveData<List<Note>> getAllNotes() {
        return notes;
    }

}
