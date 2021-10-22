package com.example.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView description;
    TextView lastUpdateTime;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.noteTitle);
        description = itemView.findViewById(R.id.noteDescription);
        lastUpdateTime = itemView.findViewById(R.id.noteLastUpdateTime);
    }
}
