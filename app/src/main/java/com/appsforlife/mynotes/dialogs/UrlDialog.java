package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.activities.DetailNoteActivity;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class UrlDialog {

    private final Activity activity;

    public UrlDialog(Activity activity) {
        this.activity = activity;
    }

    public void createDetailUrlDialog(TextView tvUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_add_link, null);
        builder.setView(view);

        AlertDialog dialogURL = builder.create();
        if (dialogURL.getWindow() != null) {
            dialogURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        final EditText editTextAddUrl = view.findViewById(R.id.et_add_url);
        editTextAddUrl.requestFocus();
        editTextAddUrl.setText(tvUrl.getText());

        view.findViewById(R.id.tv_add_url).setOnClickListener(v -> {
            if (editTextAddUrl.getText().toString().trim().isEmpty()) {
                getToast(activity, R.string.toast_enter_url);
            } else if (!Patterns.WEB_URL.matcher(editTextAddUrl.getText().toString()).matches()) {
                getToast(activity, R.string.toast_enter_valid_url);
            } else {
                tvUrl.setText(editTextAddUrl.getText().toString());
                tvUrl.setVisibility(View.VISIBLE);
                startViewAnimation(tvUrl, activity, R.anim.appearance);
                getToast(activity, R.string.toast_valid_url);
                dialogURL.dismiss();
            }
        });
        dialogURL.show();
    }

    public void createMainUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_add_link, null);
        builder.setView(view);

        AlertDialog dialogUrl = builder.create();
        if (dialogUrl.getWindow() != null) {
            dialogUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        final EditText editTextAddUrl = view.findViewById(R.id.et_add_url);
        editTextAddUrl.requestFocus();

        view.findViewById(R.id.tv_add_url).setOnClickListener(v -> {
            if (editTextAddUrl.getText().toString().trim().isEmpty()) {
                getToast(activity, R.string.toast_enter_url);
            } else if (!Patterns.WEB_URL.matcher(editTextAddUrl.getText().toString()).matches()) {
                getToast(activity, R.string.toast_enter_valid_url);
            } else {
                dialogUrl.dismiss();
                Intent intent = new Intent(activity, DetailNoteActivity.class);
                intent.putExtra(IS_FROM_QUICK_ACTIONS, true);
                intent.putExtra(QUICK_ACTIONS_TYPE, ACTION_URL);
                intent.putExtra(ACTION_URL, editTextAddUrl.getText().toString());
                activity.startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                activity.overridePendingTransition(R.anim.activity_zoom_in, R.anim.activity_static_animation);
            }
        });
        dialogUrl.show();
    }
}