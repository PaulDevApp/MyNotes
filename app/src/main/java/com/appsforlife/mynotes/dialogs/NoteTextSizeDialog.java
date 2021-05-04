package com.appsforlife.mynotes.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class NoteTextSizeDialog {

    private final Activity activity;

    public NoteTextSizeDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("NewApi")
    public void createNoteTextSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_text_size, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        TextView textSize1 = view.findViewById(R.id.tv_text_size_1);
        TextView textSize2 = view.findViewById(R.id.tv_text_size_2);
        TextView textSize3 = view.findViewById(R.id.tv_text_size_3);
        TextView textSize4 = view.findViewById(R.id.tv_text_size_4);

        switch ((int) App.getInstance().getTextSize()) {
            case (int) TEXT_SIZE_1:
                textSize1.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case (int) TEXT_SIZE_2:
                textSize2.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case (int) TEXT_SIZE_3:
                textSize3.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case (int) TEXT_SIZE_4:
                textSize4.setTextAppearance(R.style.TextDialogAppearance);
                break;
        }

        textSize1.setOnClickListener(v -> {
            App.getInstance().setTextSize((int) TEXT_SIZE_1);
            getToast(activity, R.string.ok);
            dialog.dismiss();
        });
        textSize2.setOnClickListener(v -> {
            App.getInstance().setTextSize((int) TEXT_SIZE_2);
            getToast(activity, R.string.ok);
            dialog.dismiss();
        });
        textSize3.setOnClickListener(v -> {
            App.getInstance().setTextSize((int) TEXT_SIZE_3);
            getToast(activity, R.string.ok);
            dialog.dismiss();
        });
        textSize4.setOnClickListener(v -> {
            App.getInstance().setTextSize((int) TEXT_SIZE_4);
            getToast(activity, R.string.ok);
            dialog.dismiss();
        });


        dialog.show();
    }
}
