package com.appsforlife.mynotes.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.adapters.NotesAdapter;
import com.appsforlife.mynotes.entities.Note;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.constants.Constants.*;

import java.util.ArrayList;

public class PaletteDialog {

    private final Activity activity;

    public PaletteDialog(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("NonConstantResourceId")
    public void createPaletteDialog(ArrayList<Note> notes, SearchView svSearch, ConstraintLayout rlMultiSelectLayout,
                                    ImageView ivSelectedAll, ImageView ivFavorite, ImageView ivFavoriteOff, TextView tvToolbarCount,
                                    ImageView ivDelete, ImageView ivClose, ImageView ivPickColor, NotesAdapter notesAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_color_palette, null);
        builder.setView(view);
        AlertDialog paletteDialog = builder.create();
        if (paletteDialog.getWindow() != null) {
            paletteDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }

        view.findViewById(R.id.v_default).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_DEFAULT, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_yellow).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_YELLOW, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_karina).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_KARINA, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_orange).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_ORANGE, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_blue).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_BLUE, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_red).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_RED, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_green).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_GREEN, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_turquoise).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_TURQUOISE, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_green_grey).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_GREEN_GREY, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_light_pink).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_LIGHT_PINK, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_light_green).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_LIGHT_GREEN, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_light_blue).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_LIGHT_BLUE, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_grey).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_GREY, ivClose, ivPickColor, paletteDialog));
        view.findViewById(R.id.v_light_yellow).setOnClickListener(v -> closeDialog(notes, svSearch,
                rlMultiSelectLayout, ivSelectedAll, ivFavorite, ivFavoriteOff,
                tvToolbarCount, ivDelete, notesAdapter, COLOR_LIGHT_YELLOW, ivClose, ivPickColor, paletteDialog));
        paletteDialog.show();
    }

    private void closeDialog(ArrayList<Note> notes, SearchView svSearch, ConstraintLayout rlMultiSelectLayout,
                             ImageView ivSelectedAll, ImageView ivFavorite, ImageView ivFavoriteOff, TextView tvToolbarCount,
                             ImageView ivDelete, NotesAdapter notesAdapter, String color,
                             ImageView ivClose, ImageView ivPickColor, AlertDialog paletteDialog) {
        for (Note note : notes) {
            if (note.isSelected()) {
                note.setSelected(false);
                note.setColor(color);
                App.getInstance().getNoteDao().update(note);
            }
        }
        isSelect = false;
        discharge(activity, svSearch, rlMultiSelectLayout,
                ivSelectedAll, ivFavorite, ivFavoriteOff, tvToolbarCount, ivDelete, ivClose, tvToolbarCount, ivPickColor, notes);
        notesAdapter.notifyDataSetChanged();
        paletteDialog.cancel();
    }

}
