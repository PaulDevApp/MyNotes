package com.appsforlife.mynotes.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;

import static com.appsforlife.mynotes.Support.getToast;
import static com.appsforlife.mynotes.constants.Constants.TEXT_SIZE_1;
import static com.appsforlife.mynotes.constants.Constants.TEXT_SIZE_2;
import static com.appsforlife.mynotes.constants.Constants.TEXT_SIZE_3;
import static com.appsforlife.mynotes.constants.Constants.TEXT_SIZE_4;

public class OpenSourceDialog {

    private final Activity activity;

    public OpenSourceDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("NewApi")
    public void createOpenSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_open_source, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        view.findViewById(R.id.tv_icon_com).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://icon-icons.com/ru/"))));

        view.findViewById(R.id.tv_android_asset).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://romannurik.github.io/AndroidAssetStudio/index.html"))));

        view.findViewById(R.id.tv_flaticon).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.flaticon.com/"))));

        view.findViewById(R.id.tv_in_color_balance).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://color.romanuke.com/"))));

        view.findViewById(R.id.tv_color_tool).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://material.io/resources/color/#!/?view.left=0&view.right=0&secondary.color=E0E0E0&primary.color=b71c1c"))));

        view.findViewById(R.id.tv_lottie).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://lottiefiles.com/"))));

        view.findViewById(R.id.tv_preview_link).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ponnamkarthik/RichLinkPreview"))));

        view.findViewById(R.id.tv_tooltips).setOnClickListener(v->
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/tomergoldst/tooltips"))));


        dialog.show();
    }
}
