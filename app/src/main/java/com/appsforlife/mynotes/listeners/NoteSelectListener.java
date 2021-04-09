package com.appsforlife.mynotes.listeners;

import android.view.View;

import com.appsforlife.mynotes.entities.Note;


public interface NoteSelectListener {
    void onNoteSelectListener(Note note, View viewSelected);
}
