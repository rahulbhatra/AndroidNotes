package com.example.androidnotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText title;
    private EditText description;
    private Notes note;
    private int index;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        title = findViewById(R.id.editTitle);
        description = findViewById(R.id.editDescription);
        description.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();

        if(intent.hasExtra("isEdit")) {
            isEdit = intent.getBooleanExtra("isEdit", false);
            System.out.println("isEdit " + (isEdit? " For Editing" : " For Creating New"));
        }

        if (intent.hasExtra("index")) {
            index = intent.getIntExtra("index", 0);
        }

        if (intent.hasExtra("Note")) {
            note = (Notes) intent.getSerializableExtra("Note");
            title.setText(note.getTitle());
            description.setText(note.getDescription());
        } else {
            note = new Notes("", "", new Date());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_save:
                if(isTitleEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setPositiveButton("OK", (dialog, id) -> super.onBackPressed());
                    builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                    builder.setMessage("Note will not be saved without a title.");
                    builder.setTitle("Your note is not saved!");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else if(!isChanged()) {
                    super.onBackPressed();
                }else {
                    returnToMainActivity();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void returnToMainActivity() {

        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        Date lastUpdateTime = new Date();

        note.setTitle(title);
        note.setDescription(description);
        note.setLastUpdateTime(lastUpdateTime);

        Intent intent = new Intent();
        intent.putExtra("index", index);
        intent.putExtra("Note", note);
        intent.putExtra("isEdit", isEdit);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void returnToMainActivityWithoutSaving() {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        String title = this.title.getText().toString();
        if(isTitleEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("OK", (dialog, id) -> super.onBackPressed());
            builder.setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

            builder.setTitle("Your note is not saved!");
            builder.setMessage("Note will not be saved without a title.");

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if(!isChanged()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("YES", (dialog, id) -> returnToMainActivity());
            builder.setNegativeButton("NO", (dialog, id) -> super.onBackPressed());

            String message = "Save Note '" + (title.length() > 10? title.substring(0, 10) + "..." : title) + "'?";
            builder.setMessage(message);
            builder.setTitle("Your note is not saved!");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean isTitleEmpty() {
        String title = this.title.getText().toString();
        if(title == null || title.isEmpty() || title.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean isChanged() {
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        if(title.equals(note.getTitle()) && description.equals(note.getDescription())) {
            return false;
        }
        return true;
    }
}