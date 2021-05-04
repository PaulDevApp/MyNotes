package com.appsforlife.mynotes.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.databinding.ActivitySettingsBinding;
import com.appsforlife.mynotes.dialogs.DeleteDialog;
import com.appsforlife.mynotes.dialogs.NoteTextSizeDialog;
import com.appsforlife.mynotes.dialogs.OpenSourceDialog;
import com.appsforlife.mynotes.dialogs.PreviewNumberLinesDialog;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.listeners.DialogDeleteNoteListener;
import com.appsforlife.mynotes.model.MainViewModel;

import java.io.File;
import java.util.ArrayList;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.App.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class SettingsActivity extends AppCompatActivity implements DialogDeleteNoteListener {

    private DeleteDialog deleteDialog;
    private NoteTextSizeDialog noteTextSizeDialog;
    private PreviewNumberLinesDialog previewNumberLinesDialog;
    private OpenSourceDialog openSourceDialog;
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

        deleteDialog = new DeleteDialog(this, this);
        noteTextSizeDialog = new NoteTextSizeDialog(this);
        previewNumberLinesDialog = new PreviewNumberLinesDialog(this);
        openSourceDialog = new OpenSourceDialog(this);

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

        settingsBinding.tvDeleteAllNotes.setOnClickListener(v -> {
            if (notesFromDB.size() == 0) {
                getToast(this, R.string.the_list_is_empty);
            } else {
                deleteDialog.createDeleteAllNotesDialog();
            }
        });

        settingsBinding.switchAnim.setChecked(App.getInstance().isSwitchAnim());
        settingsBinding.switchAnim.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setAnim(isChecked));

        settingsBinding.switchImageVisible.setChecked(App.getInstance().isVisible());
        settingsBinding.switchImageVisible.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setVisibilityImages(isChecked));

        settingsBinding.switchHidePreviewLink.setChecked(App.getInstance().isPreview());
        settingsBinding.switchHidePreviewLink.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setPreviewLink(isChecked));

        settingsBinding.tvOpenSource.setOnClickListener(v -> openSourceDialog.createOpenSourceDialog());

        settingsBinding.tvRateApp.setOnClickListener(v -> {
            final String appPackageName = getApplication().getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        settingsBinding.tvPrivacy.setOnClickListener(v -> startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1hoo4ymHYNdTLd2iSfj31_4aZzJiDKbwxilBa-qAhAG4/edit?usp=sharing"))));

        settingsBinding.tvFeedBack.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:paul.dev.app@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Notes");
            startActivity(emailIntent);
        });

        settingsBinding.tvIncreaseFont.setOnClickListener(v -> noteTextSizeDialog.createNoteTextSizeDialog());

        settingsBinding.tvPreviewCountLine.setOnClickListener(v -> previewNumberLinesDialog.createPreviewNumberLinesDialog());

        settingsBinding.tvShareApp.setOnClickListener(v -> shareApp());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_slide_from_top, R.anim.activity_slide_to_bottom);
    }

    @Override
    public void dialogDeleteCallback(boolean confirm) {
        if (confirm) {
            getInstance().getNoteDao().deleteAllNotes();
            getToast(this, R.string.successfully);
        }
    }

    private void shareApp() {
        try {
            final String appPackageName = getApplication().getPackageName();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=" + appPackageName);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
