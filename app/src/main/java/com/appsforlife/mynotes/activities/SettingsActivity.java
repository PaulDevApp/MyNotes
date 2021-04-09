package com.appsforlife.mynotes.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.databinding.ActivitySettingsBinding;
import com.appsforlife.mynotes.dialogs.DeleteNoteDialog;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.model.MainViewModel;

import java.util.ArrayList;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.App.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class SettingsActivity extends AppCompatActivity {

    private DeleteNoteDialog deleteNoteDialog;
    private ArrayList<Note> notesFromDB;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDarkTheme();

        com.appsforlife.mynotes.databinding.ActivitySettingsBinding settingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(settingsBinding.getRoot());

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getNotes().observe(this, notes -> notesFromDB = new ArrayList<>(notes));

        deleteNoteDialog = new DeleteNoteDialog(this);

        settingsBinding.ivClose.setOnClickListener(v -> onBackPressed());

        if (App.getInstance().getThemeMode().equals(AUTO_MODE)) {
            settingsBinding.rbAuto.setChecked(true);
        } else if (App.getInstance().getThemeMode().equals(LIGHT_MODE)) {
            settingsBinding.rbLight.setChecked(true);
        } else {
            settingsBinding.rbDark.setChecked(true);
        }

        Intent intent = getIntent();
        settingsBinding.switchTheme.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_auto:
                    getInstance().setIsNightModeEnabled(AUTO_MODE);
                    finish();
                    startActivity(intent);
                    break;
                case R.id.rb_light:
                    getInstance().setIsNightModeEnabled(LIGHT_MODE);
                    finish();
                    startActivity(intent);
                    break;
                case R.id.rb_dark:
                    getInstance().setIsNightModeEnabled(NIGHT_MODE);
                    finish();
                    startActivity(intent);
                    break;
            }
        });

        settingsBinding.tvDeleteAllNotes.setOnClickListener(v -> deleteNoteDialog.createDeleteAllNotesDialog(notesFromDB));

        settingsBinding.switchAnim.setChecked(App.getInstance().isSwitchAnim());
        settingsBinding.switchAnim.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setAnim(isChecked));

        settingsBinding.switchImageVisible.setChecked(App.getInstance().isVisible());
        settingsBinding.switchImageVisible.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setVisibilityImages(isChecked));

        settingsBinding.switchHidePreviewLink.setChecked(App.getInstance().isPreview());
        settingsBinding.switchHidePreviewLink.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setPreviewLink(isChecked));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_slide_from_top, R.anim.activity_slide_to_bottom);
    }
}
