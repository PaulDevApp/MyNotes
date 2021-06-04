package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.listeners.DialogCreateLinkListener;

import static com.appsforlife.mynotes.Support.*;

public class UrlDialog {

    private final Activity activity;
    private final DialogCreateLinkListener dialogCreateLinkListener;

    public UrlDialog(Activity activity, DialogCreateLinkListener dialogCreateLinkListener) {
        this.activity = activity;
        this.dialogCreateLinkListener = dialogCreateLinkListener;
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
                if (dialogCreateLinkListener != null){
                    dialogCreateLinkListener.onCreateLink(editTextAddUrl.getText().toString());
                    dialogURL.dismiss();
                }
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
                if (dialogCreateLinkListener != null){
                    dialogCreateLinkListener.onCreateLink(editTextAddUrl.getText().toString());
                    dialogUrl.dismiss();
                }
            }
        });
        dialogUrl.show();
    }
}