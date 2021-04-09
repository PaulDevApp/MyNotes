package com.appsforlife.mynotes.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.appsforlife.mynotes.dao.NoteDao;
import com.appsforlife.mynotes.entities.Note;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDataBase extends RoomDatabase {

    public abstract NoteDao noteDao();
}
