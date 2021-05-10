package com.appsforlife.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.Support;
import com.appsforlife.mynotes.databinding.ActivitySplashScreenBinding;


public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding splashScreenBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Support.setTheme();

        splashScreenBinding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(splashScreenBinding.getRoot());

        Support.startViewAnimation(splashScreenBinding.tvSplashLogo, this, R.anim.fall_down_item);
        Support.startViewAnimation(splashScreenBinding.ivSplash, this, R.anim.fall_down_item);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    splashScreenBinding.tvSplashLogo, "logo");
            startActivity(intent, options.toBundle());
            finish();
        }, 800);
    }
}