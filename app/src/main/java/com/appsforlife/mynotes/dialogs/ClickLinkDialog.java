package com.appsforlife.mynotes.dialogs;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.entities.Note;

import static com.appsforlife.mynotes.Support.*;

import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;

public class ClickLinkDialog {

    private final Activity activity;
    private final ClipboardManager clipboard;

    public ClickLinkDialog(Activity activity, ClipboardManager clipboard) {
        this.activity = activity;
        this.clipboard = clipboard;
    }

    public void createClickLinkDialog(Note note, TextView tvUrl, RichLinkViewTwitter linkPreview,
                                      UrlDialog urlDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_click_link, null);
        builder.setView(view);
        AlertDialog dialogClick = builder.create();
        if (dialogClick.getWindow() != null) {
            dialogClick.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.iv_dialog_open).setOnClickListener(v -> {
            if (tvUrl.getText().toString().startsWith("www")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + tvUrl.getText().toString().trim()));
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvUrl.getText().toString().trim()));
                activity.startActivity(intent);
            }
        });

        view.findViewById(R.id.iv_dialog_open).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_open_ulr);
            return true;
        });

        final TextView textViewShowLink = view.findViewById(R.id.tv_dialog_show_link);
        textViewShowLink.setText(tvUrl.getText().toString());

        view.findViewById(R.id.iv_dialog_edit_url).setOnClickListener(v->{
            urlDialog.createDetailUrlDialog(tvUrl);
            dialogClick.cancel();
        });

        view.findViewById(R.id.iv_dialog_edit_url).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_edit_ulr);
            return true;
        });

        view.findViewById(R.id.iv_dialog_share).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, note.getWebLink());
            Intent chosenIntent = Intent.createChooser(intent, "");
            activity.startActivity(chosenIntent);
        });

        view.findViewById(R.id.iv_dialog_share).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_share_ulr);
            return true;
        });

        view.findViewById(R.id.iv_dialog_copy).setOnClickListener(v -> {
            ClipData clip = ClipData.newPlainText("", tvUrl.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            getToast(activity, R.string.link_copied);
        });

        view.findViewById(R.id.iv_dialog_copy).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_copy_ulr);
            return true;
        });


        view.findViewById(R.id.iv_dialog_delete).setOnClickListener(v -> {
            note.setWebLink("");
            tvUrl.setText("");
            tvUrl.setVisibility(View.GONE);
            linkPreview.setVisibility(View.GONE);
            dialogClick.cancel();
        });

        view.findViewById(R.id.iv_dialog_delete).setOnLongClickListener(v -> {
            getToast(activity, R.string.toast_helper_delete_ulr);
            return true;
        });

        dialogClick.show();
    }
}
