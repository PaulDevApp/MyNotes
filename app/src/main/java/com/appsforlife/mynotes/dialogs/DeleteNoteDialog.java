package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.entities.Note;
import com.google.android.material.checkbox.MaterialCheckBox;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.App.*;

import java.util.ArrayList;

public class DeleteNoteDialog {

    private final Activity activity;

    public DeleteNoteDialog(Activity activity) {
        this.activity = activity;
    }

    public void createDeleteAllSelectedNotesDialog(SearchView svSearch, ConstraintLayout rlMultiSelectLayout,
                                                   ImageView ivSelectedAll, ImageView ivFavorite, ImageView ivFavoriteOff, TextView tvToolbarCount,
                                                   ImageView ivDelete, ImageView ivClose, ImageView ivPickColor, ArrayList<Note> notesFromDB) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delet_select_notes, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_delete_all_selected_notes).setOnClickListener(v -> {
            for (Note note : notesFromDB) {
                if (note.isSelected()) {
                    deleteImage(note.getImagePath(), activity);
                    App.getInstance().getNoteDao().deleteNote(note);
                }
            }
            isSelect = false;
            discharge(activity, svSearch, rlMultiSelectLayout,
                    ivSelectedAll, ivFavorite, ivFavoriteOff, tvToolbarCount, ivDelete, ivClose, tvToolbarCount, ivPickColor, notesFromDB);
            dialogDelete.cancel();
        });
        dialogDelete.show();
    }

    public void createDeleteNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delete_note, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) {
            view.findViewById(R.id.tv_dialog_message).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.tv_dialog_message).setVisibility(View.GONE);
        }
        view.findViewById(R.id.tv_delete_note).setOnClickListener(v -> {
            deleteImage(note.getImagePath(), activity);
            App.getInstance().getNoteDao().deleteNote(note);
            dialogDelete.cancel();
            activity.finish();
        });

        MaterialCheckBox checkBox = view.findViewById(R.id.checkbox_detail_delete_note);
        checkBox.setChecked(App.getInstance().isConfirmed());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setCheckConfirmed(isChecked));
        dialogDelete.show();
    }


    public void createDeleteAllNotesDialog(ArrayList<Note> notesFromDB) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delete_all_notes, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_delete_all_notes_forever).setOnClickListener(v -> {
            if (isEmpty) {
                for (Note note : notesFromDB) {
                    deleteImage(note.getImagePath(), activity);
                }
                getInstance().getNoteDao().deleteAllNotes();
                getToast(activity, R.string.successfully);
            } else {
                getToast(activity, R.string.the_list_was_empty);
            }
            dialogDelete.dismiss();
        });
        dialogDelete.show();
    }

}
