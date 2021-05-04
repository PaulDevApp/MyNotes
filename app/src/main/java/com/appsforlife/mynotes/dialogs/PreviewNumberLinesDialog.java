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

import static com.appsforlife.mynotes.Support.getToast;
import static com.appsforlife.mynotes.constants.Constants.*;

public class PreviewNumberLinesDialog {

    private final Activity activity;

    public PreviewNumberLinesDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("NewApi")
    public void createPreviewNumberLinesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_preview_count_lines, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        TextView number1 = view.findViewById(R.id.tv_lines_1);
        TextView number3 = view.findViewById(R.id.tv_lines_3);
        TextView number5 = view.findViewById(R.id.tv_lines_5);
        TextView number7 = view.findViewById(R.id.tv_lines_7);
        TextView number10 = view.findViewById(R.id.tv_lines_10);

        switch (App.getInstance().getCountLines()) {
            case COUNT_1:
                number1.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case COUNT_3:
                number3.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case COUNT_5:
                number5.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case COUNT_7:
                number7.setTextAppearance(R.style.TextDialogAppearance);
                break;
            case COUNT_10:
                number10.setTextAppearance(R.style.TextDialogAppearance);
                break;
        }

        number1.setOnClickListener(v -> {
            App.getInstance().setCountLines(COUNT_1);
            getToast(activity, R.string.ok);
            dialog.cancel();
        });
        number3.setOnClickListener(v -> {
            App.getInstance().setCountLines(COUNT_3);
            getToast(activity, R.string.ok);
            dialog.cancel();
        });
        number5.setOnClickListener(v -> {
            App.getInstance().setCountLines(COUNT_5);
            getToast(activity, R.string.ok);
            dialog.cancel();
        });
        number7.setOnClickListener(v -> {
            App.getInstance().setCountLines(COUNT_7);
            getToast(activity, R.string.ok);
            dialog.cancel();
        });
        number10.setOnClickListener(v -> {
            App.getInstance().setCountLines(COUNT_10);
            getToast(activity, R.string.ok);
            dialog.cancel();
        });


        dialog.show();
    }
}
