package com.example.androidnotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private List<Notes> notesList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private NotesAdapter notesAdapter; // Data to recyclerview adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        notesList.addAll(loadFile());
        this.setTitle("Android Notes (" + notesList.size() + ")");

        recyclerView = findViewById(R.id.recycler);
        notesAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(notesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_create:
                openEditActivity(0, new Notes("", "", new Date()), false);
                break;
            case R.id.menu_item_info:
                openAboutActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private List<Notes> loadFile() {

        Log.d(TAG, "loadFile: Loading JSON File");
        ArrayList notesList = new ArrayList<>();
        try {
            InputStream is = getApplicationContext().openFileInput(getString(R.string.file_name));
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");
                String description = jsonObject.getString("description");

                System.out.println("Date updated:" + jsonObject.getString("lastUpdateTime"));

                Date lastUpdateTime = new Date(jsonObject.getString("lastUpdateTime"));
                Notes note = new Notes(title, description, lastUpdateTime);
                notesList.add(note);
            }

//            Toast.makeText(this, "File Loaded Properly.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
//            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
            File file = new File(getString(R.string.file_name));
            try {
                file.createNewFile();
//                Toast.makeText(this, getString(R.string.create_file), Toast.LENGTH_SHORT).show();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notesList;
    }

    private void saveNotes() {

        Log.d(TAG, "saveProduct: Saving JSON File");

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);

            PrintWriter printWriter = new PrintWriter(fos);
            printWriter.print(notesList);
            printWriter.close();
            fos.close();

            Log.d(TAG, "Saved Nodes: JSON:\n" + notesList.toString());

            this.setTitle("Android Notes (" + notesList.size() + ")");
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void openEditActivity(Integer index, Notes note, boolean isEdit) {

        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra("index", index);
        intent.putExtra("Note", note);
        intent.putExtra("isEdit", isEdit);
        activityResultLauncher.launch(intent);
    }

    public void openAboutActivity() {

        Intent intent = new Intent(this, AboutActivity.class);
        activityResultLauncher.launch(intent);
    }

    public void handleResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        Intent data = result.getData();
        if (result.getResultCode() == RESULT_OK) {
            Notes note = (Notes) data.getSerializableExtra("Note");
            if (note != null) {
                int index = data.getIntExtra("index", 0);
                boolean isEdit = data.getBooleanExtra("isEdit", false);
                if(isEdit) {
                    notesList.remove(index);
                    notesAdapter.notifyItemRemoved(index);
                    notesList.add(0, note);
                    notesAdapter.notifyItemInserted(0);
                } else {
                    notesList.add(0, note);
                    notesAdapter.notifyItemInserted(0);
                }

                saveNotes();
            }
        } else {
            Toast.makeText(this, "OTHER result not OK!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        openEditActivity(pos, notesList.get(pos), true);
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("YES", (dialog, id) -> deleteNote(pos));
        builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());

        String noteTitle =  notesList.get(pos).getTitle();
        String title = "Delete Note '" + (noteTitle.length() > 10 ? noteTitle.substring(0, 10) + "..." : noteTitle) + "'?";
        builder.setTitle(title);

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    private void deleteNote(int index) {
        notesList.remove(index);
        notesAdapter.notifyItemRemoved(index);
        saveNotes();
    }
}