package com.appsforlife.mynotes.model;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.entities.Note;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final LiveData<List<Note>> notes = App.getInstance().getNoteDao().getAllNotes();

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

}
