package com.appsforlife.mynotes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.Support;
import com.appsforlife.mynotes.databinding.ItemMainColorPaletteBinding;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.appsforlife.mynotes.listeners.ColorPaletteListener;

import java.util.List;

public class ColorMainPaletteAdapter extends RecyclerView.Adapter<ColorMainPaletteAdapter.ColorMainPaletteViewHolder> {

    private List<PaletteColor> paletteColors;
    private final ColorPaletteListener colorPaletteListener;
    private int lastPosition = -1;


    public ColorMainPaletteAdapter(List<PaletteColor> paletteColors, ColorPaletteListener colorPaletteListener) {
        this.paletteColors = paletteColors;
        this.colorPaletteListener = colorPaletteListener;
    }

    @NonNull
    @Override
    public ColorMainPaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemMainColorPaletteBinding binding = ItemMainColorPaletteBinding.inflate(layoutInflater, parent, false);
        return new ColorMainPaletteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorMainPaletteViewHolder holder, int position) {
        holder.setViewBackground(paletteColors.get(position));
        if (position > lastPosition) {
            Support.startViewAnimation(holder.binding.vItemMainColor, holder.binding.ivItemMainColor.getContext(), R.anim.zoom_in);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return paletteColors.size();
    }

    public void setPaletteColors(List<PaletteColor> paletteColors) {
        this.paletteColors = paletteColors;
    }

    public class ColorMainPaletteViewHolder extends RecyclerView.ViewHolder {

        private final ItemMainColorPaletteBinding binding;

        ColorMainPaletteViewHolder(ItemMainColorPaletteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> colorPaletteListener.onColorPaletteClickListener(paletteColors.get(getAbsoluteAdapterPosition()),
                    binding.ivItemMainColor));

        }

        void setViewBackground(PaletteColor paletteColor) {
            GradientDrawable gradientDrawable = (GradientDrawable) binding.vItemMainColor.getBackground();
            gradientDrawable.setColor(Color.parseColor(paletteColor.getColor()));

            if (App.getInstance().getSelectedColor().equals(paletteColor.getColor())) {
                binding.ivItemMainColor.setVisibility(View.VISIBLE);
            } else {
                binding.ivItemMainColor.setVisibility(View.GONE);
            }
        }
    }
}