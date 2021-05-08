package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.listeners.DialogDeleteNoteListener;
import com.google.android.material.checkbox.MaterialCheckBox;

public class DeleteDialog {

    private final Activity activity;
    private final DialogDeleteNoteListener dialogDeleteNoteListener;

    public DeleteDialog(Activity activity, DialogDeleteNoteListener dialogDeleteNoteListener) {
        this.activity = activity;
        this.dialogDeleteNoteListener = dialogDeleteNoteListener;
    }

    public void createDeleteAllSelectedNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delet_select_notes, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_delete_all_selected_notes).setOnClickListener(v -> {
            dialogDeleteNoteListener.dialogDeleteCallback(true);
            dialogDelete.cancel();
        });
        dialogDelete.show();
    }

    public void createDeleteNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delete_note, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        view.findViewById(R.id.tv_delete_note).setOnClickListener(v -> {
            dialogDeleteNoteListener.dialogDeleteCallback(true);
            dialogDelete.cancel();
        });

        MaterialCheckBox checkBox = view.findViewById(R.id.checkbox_detail_delete_note);
        checkBox.setChecked(App.getInstance().isConfirmed());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setCheckConfirmed(isChecked));
        dialogDelete.show();
    }

    public void createDeleteAllNotesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delete_all_notes, null);
        builder.setView(view);
        AlertDialog dialogDelete = builder.create();
        if (dialogDelete.getWindow() != null) {
            dialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_delete_all_notes_forever).setOnClickListener(v -> {
            dialogDeleteNoteListener.dialogDeleteCallback(true);
            dialogDelete.cancel();
        });
        dialogDelete.show();
    }

}
