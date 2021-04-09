package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.entities.Note;
import com.google.android.material.checkbox.MaterialCheckBox;

import static com.appsforlife.mynotes.Support.deleteImage;

public class ReplaceImageDialog {

    private final Activity activity;

    public ReplaceImageDialog(Activity activity) {
        this.activity = activity;
    }

    public void createReplaceImageDialog(Note note, ImagePickerDialog imagePickerDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_replace_image, null);
        builder.setView(view);
        AlertDialog dialogReplace = builder.create();
        if (dialogReplace.getWindow() != null) {
            dialogReplace.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_replace).setOnClickListener(v -> {
            deleteImage(note.getImagePath(), activity);
            dialogReplace.cancel();
            imagePickerDialog.createImagePickerDialog();
        });

        MaterialCheckBox checkBox = view.findViewById(R.id.checkbox_detail_replace_image);
        checkBox.setChecked(App.getInstance().isReplace());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setCheckReplace(isChecked));
        dialogReplace.show();
    }
}
