package com.example.note;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    NoteViewModel noteViewModel;
    ActivityResultLauncher<Intent> activityResultLauncherForAddNote;
    public final String CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerActivityForAddNote();
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Razmarshav Notes");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);



        noteViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(NoteViewModel.class);

        noteViewModel.getAllNotes().observe(this, noteAdapter::setNotes);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Note backup = noteAdapter.getNotes(viewHolder.getAdapterPosition());
                noteViewModel.delete(noteAdapter.getNotes(viewHolder.getAdapterPosition()));
                ConstraintLayout container = findViewById(R.id.container);
                Snackbar.make(container, "Note is deleted", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", v -> {
                            noteViewModel.insert(backup);
                        }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.new_menu, menu);
        return true;

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.top_menu) {
            Intent intent = new Intent(MainActivity.this, AddNewNote.class);
            activityResultLauncherForAddNote.launch(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void registerActivityForAddNote() {
        activityResultLauncherForAddNote = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            Intent data = result.getData();

            if (data != null) {
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                sendNotification("You added new note", "Name: " + title + "\n" + "Description: " + description);
                Note note = new Note(title, description);
                noteViewModel.insert(note);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            Note note = new Note(title, description);
            noteViewModel.insert(note);


        }
    }

    @SuppressLint({"NewApi", "WrongConstant", "MissingPermission"})
    public void sendNotification(String title, String description) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        @SuppressLint({"NewApi", "LocalSuppress"})
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "1", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification.Builder builder = new Notification.Builder(MainActivity.this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new Notification.BigTextStyle().bigText(description))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_DEFAULT);
        NotificationManagerCompat compat = NotificationManagerCompat.from(MainActivity.this);
        compat.notify(1, builder.build());

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}