package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.listeners.DialogDeleteImageListener;
import com.google.android.material.checkbox.MaterialCheckBox;

public class DeleteImageDialog {

    private final Activity activity;
    private final DialogDeleteImageListener dialogDeleteImageListener;

    public DeleteImageDialog(Activity activity, DialogDeleteImageListener dialogDeleteImageListener){
        this.activity = activity;
        this.dialogDeleteImageListener = dialogDeleteImageListener;
    }

    public void createDeleteImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_delete_image, null);
        builder.setView(view);
        AlertDialog dialogDeleteImage = builder.create();
        if (dialogDeleteImage.getWindow() != null) {
            dialogDeleteImage.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_delete_image).setOnClickListener(v -> {
            dialogDeleteImageListener.dialogDeleteImageCallback(true);
            dialogDeleteImage.cancel();
        });

        MaterialCheckBox checkBox = view.findViewById(R.id.checkbox_detail_delete_image);
        checkBox.setChecked(App.getInstance().isDelete());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> App.getInstance().setCheckDeleteImage(isChecked));
        dialogDeleteImage.show();
    }


}
