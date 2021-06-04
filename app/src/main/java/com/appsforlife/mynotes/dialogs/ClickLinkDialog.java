package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.listeners.DialogClickLinkListener;

import static com.appsforlife.mynotes.Support.*;

public class ClickLinkDialog {

    private final Activity activity;
    private final DialogClickLinkListener dialogClickLinkListener;

    public ClickLinkDialog(Activity activity, DialogClickLinkListener dialogClickLinkListener) {
        this.activity = activity;
        this.dialogClickLinkListener = dialogClickLinkListener;
    }

    public void createClickLinkDialog(String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_click_link, null);
        builder.setView(view);
        AlertDialog dialogClick = builder.create();
        if (dialogClick.getWindow() != null) {
            dialogClick.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        view.findViewById(R.id.iv_dialog_open).setOnClickListener(v -> {
            if (dialogClickLinkListener != null){
                dialogClickLinkListener.onClickLink(true);
            }
        });

        view.findViewById(R.id.iv_dialog_open).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_open_ulr);
            return true;
        });

        final TextView textViewShowLink = view.findViewById(R.id.tv_dialog_show_link);
        textViewShowLink.setText(url);

        view.findViewById(R.id.iv_dialog_edit_url).setOnClickListener(v->{
            if (dialogClickLinkListener != null){
                dialogClickLinkListener.onEditLink(true);
                dialogClick.cancel();
            }
        });

        view.findViewById(R.id.iv_dialog_edit_url).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_edit_ulr);
            return true;
        });

        view.findViewById(R.id.iv_dialog_share).setOnClickListener(v -> {
            if (dialogClickLinkListener != null){
                dialogClickLinkListener.onShareLink(true);
            }
        });

        view.findViewById(R.id.iv_dialog_share).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_share_ulr);
            return true;
        });

        view.findViewById(R.id.iv_dialog_copy).setOnClickListener(v -> {
            if (dialogClickLinkListener != null){
                dialogClickLinkListener.onCopyLink(true);
            }
        });

        view.findViewById(R.id.iv_dialog_copy).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_copy_ulr);
            return true;
        });


        view.findViewById(R.id.iv_dialog_delete).setOnClickListener(v -> {
            if (dialogClickLinkListener != null){
                dialogClickLinkListener.onDeleteLink(true);
                dialogClick.cancel();
            }
        });

        view.findViewById(R.id.iv_dialog_delete).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_delete_ulr);
            return true;
        });

        dialogClick.show();
    }
}
