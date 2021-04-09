package com.appsforlife.mynotes;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.appsforlife.mynotes.constants.Constants;
import com.appsforlife.mynotes.dao.NoteDao;
import com.appsforlife.mynotes.database.NoteDataBase;

import static com.appsforlife.mynotes.constants.Constants.*;

public class App extends Application {

    private NoteDao noteDao;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private boolean changeView;
    private boolean includeDone;
    private boolean switchAnim;
    private boolean isVisible;
    private boolean isPreview;
    private boolean isConfirmed;
    private boolean isReplace;
    private String themeMode;
    private String selectedColor;

    public static int newId;
    public static String colorPicker;


    private static App instance = null;

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.changeView = mPrefs.getBoolean(Constants.VIEW, false);
        this.includeDone = mPrefs.getBoolean(INCLUDE_DONE, false);
        this.selectedColor = mPrefs.getString(Constants.SELECTED_COLOR, All_COLORS);
        this.themeMode = mPrefs.getString(THEME_MODE, AUTO_MODE);
        this.switchAnim = mPrefs.getBoolean(SWITCH_ANIM, false);
        this.isVisible = mPrefs.getBoolean(VISIBLE_IMAGES, false);
        this.isPreview = mPrefs.getBoolean(PREVIEW_LINK, false);
        this.isConfirmed = mPrefs.getBoolean(CONFIRM_DELETE, false);
        this.isReplace = mPrefs.getBoolean(CONFIRM_REPLACE, false);

        instance = this;

        NoteDataBase noteDataBase = Room.databaseBuilder(getApplicationContext(),
                NoteDataBase.class, "note_db")
                .allowMainThreadQueries()
                .build();

        noteDao = noteDataBase.noteDao();
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putString(SELECTED_COLOR, selectedColor);
        editor.apply();
    }

    public void setIsNightModeEnabled(String themeMode) {
        this.themeMode = themeMode;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putString(THEME_MODE, themeMode);
        editor.apply();
    }


    public void setIncludeDone(boolean includeDone) {
        this.includeDone = includeDone;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(INCLUDE_DONE, includeDone);
        editor.apply();
    }

    public void setAnim(boolean switchAnim) {
        this.switchAnim = switchAnim;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(SWITCH_ANIM, switchAnim);
        editor.apply();
    }

    public void setVisibilityImages(boolean isVisible) {
        this.isVisible = isVisible;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(VISIBLE_IMAGES, isVisible);
        editor.apply();

    }

    public void setPreviewLink(boolean isPreview) {
        this.isPreview = isPreview;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(PREVIEW_LINK, isPreview);
        editor.apply();

    }

    public void setChangeView(boolean changeView) {
        this.changeView = changeView;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(VIEW, changeView);
        editor.apply();
    }

    public void setCheckConfirmed(boolean isConfirmed){
        this.isConfirmed = isConfirmed;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(CONFIRM_DELETE, isConfirmed);
        editor.apply();
    }

    public void setCheckReplace(boolean isReplace){
        this.isReplace = isReplace;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        editor.putBoolean(CONFIRM_REPLACE, isReplace);
        editor.apply();
    }


    public boolean isConfirmed() { return isConfirmed; }

    public String getSelectedColor() {
        return selectedColor;
    }

    public boolean isIncludeDone() {
        return includeDone;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public boolean isSwitchAnim() {
        return switchAnim;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isPreview() {
        return isPreview;
    }

    public boolean isChangeView() {
        return changeView;
    }

    public boolean isReplace() {
        return isReplace;
    }

    public NoteDao getNoteDao() {
        return noteDao;
    }
}
