package com.appsforlife.mynotes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.Support;
import com.appsforlife.mynotes.databinding.ItemDetailColorPaletteBinding;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.appsforlife.mynotes.listeners.ColorPaletteListener;

import java.util.List;

public class ColorDetailPaletteAdapter extends RecyclerView.Adapter<ColorDetailPaletteAdapter.ColorPaletteViewHolder> {

    private List<PaletteColor> paletteColors;
    private final ColorPaletteListener colorPaletteListener;
    private final Note note;
    private int lastPosition = -1;


    public ColorDetailPaletteAdapter(List<PaletteColor> paletteColors, ColorPaletteListener colorPaletteListener,
                                     Note note) {
        this.paletteColors = paletteColors;
        this.colorPaletteListener = colorPaletteListener;
        this.note = note;
    }

    @NonNull
    @Override
    public ColorPaletteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemDetailColorPaletteBinding binding = ItemDetailColorPaletteBinding.inflate(layoutInflater, parent, false);
        return new ColorPaletteViewHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull ColorPaletteViewHolder holder, int position) {
        holder.setViewBackground(paletteColors.get(position));
        if (position > lastPosition) {
            Support.startViewAnimation(holder.binding.vItemDetailColor, holder.itemView.getContext(), R.anim.zoom_in);
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

    public class ColorPaletteViewHolder extends RecyclerView.ViewHolder {

        private final ItemDetailColorPaletteBinding binding;

        ColorPaletteViewHolder(ItemDetailColorPaletteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> colorPaletteListener.onColorPaletteClickListener(paletteColors.get(getAbsoluteAdapterPosition()), binding.ivItemDetailColor));

        }

        void setViewBackground(PaletteColor paletteColor) {
            GradientDrawable gradientDrawable = (GradientDrawable) binding.vItemDetailColor.getBackground();
            gradientDrawable.setColor(Color.parseColor(paletteColor.getColor()));

            if (note.getColor().equals(paletteColor.getColor())) {
                binding.ivItemDetailColor.setVisibility(View.VISIBLE);
            } else {
                binding.ivItemDetailColor.setVisibility(View.GONE);
            }
        }
    }
}
