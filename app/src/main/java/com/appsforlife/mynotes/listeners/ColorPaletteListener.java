package com.appsforlife.mynotes.listeners;


import android.widget.ImageView;

import com.appsforlife.mynotes.entities.PaletteColor;


public interface ColorPaletteListener {
    void onColorPaletteClickListener(PaletteColor paletteColor, ImageView imageViewColor);
}
