package com.appsforlife.mynotes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.databinding.ActivityPictureBinding;
import com.bumptech.glide.Glide;

import static com.appsforlife.mynotes.Support.setDarkTheme;
import static com.appsforlife.mynotes.constants.Constants.PICTURE_PATH;

public class PictureActivity extends AppCompatActivity {

    private ActivityPictureBinding binding;
    private boolean isVisible;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDarkTheme();

        binding = ActivityPictureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        String picturePath = intent.getStringExtra(PICTURE_PATH);
        Glide.with(this).load(picturePath).into(binding.ivPicture);

        binding.ivPicture.setOnClickListener(v -> {
            if (!isVisible) {
                binding.clDetailPictureToolbar.setVisibility(View.VISIBLE);
                isVisible = true;
            } else {
                binding.clDetailPictureToolbar.setVisibility(View.GONE);
                isVisible = false;
            }
        });

        binding.ivBackToDetail.setOnClickListener(v -> onBackPressed());

        binding.ivShareImage.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) binding.ivPicture.getDrawable()).getBitmap();
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, picturePath, null);
            Uri uri = Uri.parse(path);
            Intent intentPicture = new Intent(Intent.ACTION_SEND);
            intentPicture.setType("image/jpeg");
            intentPicture.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intentPicture, "Share Image"));
        });

        binding.ivOpenInGallery.setOnClickListener(v -> {
            intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(picturePath), "image/*");
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_static_animation, R.anim.zoom_out);
    }
}
