package com.appsforlife.mynotes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.appsforlife.mynotes.listeners.ColorPaletteListener;

import static com.appsforlife.mynotes.App.*;

import java.util.List;

public class ColorDetailPaletteAdapter extends RecyclerView.Adapter<ColorDetailPaletteAdapter.ColorPaletteViewHolder> {

    private List<PaletteColor> paletteColors;
    private final ColorPaletteListener colorPaletteListener;


    public ColorDetailPaletteAdapter(List<PaletteColor> paletteColors, ColorPaletteListener colorPaletteListener) {
        this.paletteColors = paletteColors;
        this.colorPaletteListener = colorPaletteListener;
    }

    @NonNull
    @Override
    public ColorPaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorPaletteViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_detail_color_palette, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ColorPaletteViewHolder holder, int position) {
        holder.setViewBackground(paletteColors.get(position));
    }

    @Override
    public int getItemCount() {
        return paletteColors.size();
    }

    public void setPaletteColors(List<PaletteColor> paletteColors) {
        this.paletteColors = paletteColors;
    }

    public class ColorPaletteViewHolder extends RecyclerView.ViewHolder {

        private final FrameLayout viewBackground;
        private final ImageView imageViewColor;

        ColorPaletteViewHolder(@NonNull View itemView) {
            super(itemView);

            viewBackground = itemView.findViewById(R.id.v_item_detail_color);
            imageViewColor = itemView.findViewById(R.id.iv_item_detail_color);

            itemView.setOnClickListener(v -> colorPaletteListener.onColorPaletteClickListener(paletteColors.get(getAdapterPosition()), imageViewColor));

        }

        void setViewBackground(PaletteColor paletteColor) {
            GradientDrawable gradientDrawable = (GradientDrawable) viewBackground.getBackground();
            gradientDrawable.setColor(Color.parseColor(paletteColor.getColor()));

            if (colorPicker.equals(paletteColor.getColor())){
                imageViewColor.setVisibility(View.VISIBLE);
            }else {
                imageViewColor.setVisibility(View.GONE);
            }
        }
    }
}
