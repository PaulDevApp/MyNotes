package com.appsforlife.mynotes.listeners;

import android.widget.RelativeLayout;

import com.appsforlife.mynotes.entities.Note;


public interface NoteListener {
    void onNoteClicked(Note note, RelativeLayout noteLayout);
}
