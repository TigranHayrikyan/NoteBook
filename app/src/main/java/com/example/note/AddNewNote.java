package com.example.note;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class AddNewNote extends AppCompatActivity {

    public EditText setTitle;
    public EditText setDescription;
    public Button addBtn;
    public Button cancelBtn;
    String title;
    String description;


    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Note");
        setContentView(R.layout.activity_add_new_note);

        setTitle = findViewById(R.id.editTextTitle);
        setDescription = findViewById(R.id.editTextDescription);
        cancelBtn = findViewById(R.id.cancelBtn);
        addBtn = findViewById(R.id.addBtn);

        cancelBtn.setOnClickListener(v -> finish());


        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            title = setTitle.getText().toString();
            description = setDescription.getText().toString();
            if (title.equals("") || description.equals("")) {
                ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                Snackbar.make(constraintLayout,  "Title or description cannot be an empty" + "\n" + "Please fill in the fields", Snackbar.LENGTH_SHORT)
                        .setAction("Insert", v1 -> {
                            if (title.equals("")) {
                                setTitle.requestFocus();
                            }else if(description.equals("")){
                                setDescription.requestFocus();
                            }
                        }).show();
            }else{
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}