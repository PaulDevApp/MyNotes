package com.appsforlife.mynotes.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.activities.SettingsActivity;
import com.appsforlife.mynotes.adapters.ColorMainPaletteAdapter;
import com.appsforlife.mynotes.databinding.LayoutBottomMenuBinding;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.appsforlife.mynotes.listeners.ColorPaletteListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;

import static com.appsforlife.mynotes.Support.getColors;
import static com.appsforlife.mynotes.constants.Constants.All_COLORS;

public class MainBottomSheetFragment extends BottomSheetDialogFragment implements ColorPaletteListener {

    private final ArrayList<PaletteColor> paletteColors = new ArrayList<>();
    private ColorMainPaletteAdapter colorMainPaletteAdapter;
    private LayoutBottomMenuBinding binding;
    private BottomSheetSetSortingListener bottomSheetSetSortingListener;
    private BottomSheetSetViewListener bottomSheetSetViewListener;

    public interface BottomSheetSetSortingListener {
        void onOnClick(boolean isClick);
    }

    public interface BottomSheetSetViewListener {
        void onChangeView(boolean isChange);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        bottomSheetSetSortingListener = (BottomSheetSetSortingListener) context;
        bottomSheetSetViewListener = (BottomSheetSetViewListener) context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = LayoutBottomMenuBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        colorMainPaletteAdapter = new ColorMainPaletteAdapter(paletteColors, this);
        binding.rvColorPalette.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvColorPalette.setAdapter(colorMainPaletteAdapter);
        colorMainPaletteAdapter.setPaletteColors(getColors(paletteColors));

        if (App.getInstance().isChangeView()) {
            binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_view_headline, 0);
        } else {
            binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_view_module, 0);
        }

        if (App.getInstance().getSelectedColor().equals(All_COLORS)) {
            binding.tvAllColors.setText(R.string.all);
        } else {
            binding.tvAllColors.setText("");
        }

        binding.tvChangeView.setOnClickListener(v -> {
            if (!App.getInstance().isChangeView()) {
                App.getInstance().setChangeView(true);
                binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_view_headline, 0);
                if (bottomSheetSetViewListener != null) {
                    bottomSheetSetViewListener.onChangeView(true);
                }
            } else {
                App.getInstance().setChangeView(false);
                binding.tvChangeView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_view_module, 0);
                if (bottomSheetSetViewListener != null) {
                    bottomSheetSetViewListener.onChangeView(false);
                }
            }
        });

        binding.tvAllColors.setOnClickListener(v -> {
            binding.tvAllColors.setText(R.string.all);
            colorMainPaletteAdapter.notifyDataSetChanged();
            App.getInstance().setSelectedColor(All_COLORS);
            if (bottomSheetSetSortingListener != null) {
                bottomSheetSetSortingListener.onOnClick(true);
            }
        });

        binding.switchDone.setChecked(App.getInstance().isIncludeDone());
        binding.switchDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            App.getInstance().setIncludeDone(isChecked);
            if (bottomSheetSetSortingListener != null) {
                bottomSheetSetSortingListener.onOnClick(true);
            }
        });

        binding.switchOnlyImage.setChecked(App.getInstance().isIncludePicture());
        binding.switchOnlyImage.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            App.getInstance().setIncludePicture(isChecked);
            if (bottomSheetSetSortingListener != null) {
                bottomSheetSetSortingListener.onOnClick(true);
            }
        }));

        binding.tvSettings.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SettingsActivity.class));
            requireActivity().overridePendingTransition(R.anim.activity_slide_from_bottom, R.anim.activity_slide_to_top);
        });


        return view;
    }

    @Override
    public void onColorPaletteClickListener(PaletteColor paletteColor, ImageView imageViewColor) {
        App.getInstance().setSelectedColor(paletteColor.getColor());
        imageViewColor.setVisibility(View.VISIBLE);
        colorMainPaletteAdapter.notifyDataSetChanged();
        binding.tvAllColors.setText("");

        if (bottomSheetSetSortingListener != null) {
            bottomSheetSetSortingListener.onOnClick(true);
        }
    }
}
