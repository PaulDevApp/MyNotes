package com.appsforlife.mynotes.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.listeners.DialogPaletteListener;
import static com.appsforlife.mynotes.constants.Constants.*;

public class PaletteDialog  {

    private final Activity activity;
    private final DialogPaletteListener dialogPaletteListener;

    public PaletteDialog(Activity activity, DialogPaletteListener dialogPaletteListener) {
        this.activity = activity;
        this.dialogPaletteListener = dialogPaletteListener;
    }

    @SuppressLint("NonConstantResourceId")
    public void createPaletteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_color_palette, null);
        builder.setView(view);
        AlertDialog paletteDialog = builder.create();
        if (paletteDialog.getWindow() != null) {
            paletteDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        view.findViewById(R.id.v_default).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_DEFAULT);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_yellow).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_YELLOW);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_karina).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_KARINA);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_orange).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_ORANGE);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_blue).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_BLUE);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_red).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_RED);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_green).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_GREEN);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_turquoise).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_TURQUOISE);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_green_grey).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_GREEN_GREY);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_light_pink).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_LIGHT_PINK);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_light_green).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_LIGHT_GREEN);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_light_blue).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_LIGHT_BLUE);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_grey).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_GREY);
                paletteDialog.cancel();
            }
        });
        view.findViewById(R.id.v_light_yellow).setOnClickListener(v -> {
            if (dialogPaletteListener != null){
                dialogPaletteListener.onSelectColor(COLOR_LIGHT_YELLOW);
                paletteDialog.cancel();
            }
        });
        paletteDialog.show();
    }

}
