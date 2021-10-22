package com.example.androidnotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;


public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder>{

    private static final String TAG = "NotesAdapter";
    private final List<Notes> notesList;
    private final MainActivity mainAct;
    private View inflatedLayout;

    public NotesAdapter(List<Notes> notesList, MainActivity mainAct) {
        this.notesList = notesList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        inflatedLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_list_row, parent, false);

        inflatedLayout.setOnClickListener(mainAct);
        inflatedLayout.setOnLongClickListener(mainAct);

        return new NotesViewHolder(inflatedLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Notes " + position);
        Notes note = notesList.get(position);
        holder.title.setText(note.getTitle());
        holder.description.setText(note.getDescription().length() > 80? note.getDescription().
                substring(0, 80) + "..." : note.getDescription());
        String pattern = "EEE MMM dd, hh:mm aa";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        holder.lastUpdateTime.setText(simpleDateFormat.format(note.getLastUpdateTime()));

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
