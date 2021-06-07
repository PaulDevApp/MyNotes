package com.appsforlife.mynotes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.appsforlife.mynotes.constants.Constants.*;


public final class Support {

    public static boolean isSelect;

    public static void startViewAnimation(View view, Context context, int anim) {
        if (App.getInstance().isSwitchAnim()) {
            Animation animation = AnimationUtils.loadAnimation(context, anim);
            view.startAnimation(animation);
        }
    }

    @SuppressLint("SupportAnnotationUsage")
    @AnimRes
    public static void startItemAnimation(Context context, RecyclerView recyclerView, int resAnim) {
        if (App.getInstance().isSwitchAnim()) {
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(context, resAnim);
            recyclerView.setLayoutAnimation(animation);
        }
    }

    public static void setTheme() {
        if (App.getInstance().getThemeMode().equals(AUTO_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (App.getInstance().getThemeMode().equals(LIGHT_MODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    public static void updateStrokeOut(Note note, TextView title, TextView text) {
        if (!note.isDone()) {
            title.setPaintFlags(title.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            text.setPaintFlags(text.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            title.setHint(R.string.enter_title);
            text.setHint(R.string.enter_text);
        } else {
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            text.setPaintFlags(text.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if (text.length() == 0) {
                text.setHint("");
            }
            if (title.length() == 0) {
                title.setHint("");
            }
        }
    }

    public static void setColorIndicator(String color, ImageView indicator, Activity activity) {
        switch (color) {
            case COLOR_DEFAULT:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorDefault));
                break;
            case COLOR_YELLOW:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorYellow));
                break;
            case COLOR_KARINA:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorKarina));
                break;
            case COLOR_ORANGE:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorOrange));
                break;
            case COLOR_BLUE:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorBlue));
                break;
            case COLOR_RED:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorRed));
                break;
            case COLOR_GREEN:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorGreen));
                break;
            case COLOR_TURQUOISE:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorTurquoise));
                break;
            case COLOR_GREEN_GREY:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorGreenGrey));
                break;
            case COLOR_LIGHT_PINK:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorLightPink));
                break;
            case COLOR_LIGHT_GREEN:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorLightGreen));
                break;
            case COLOR_LIGHT_BLUE:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorLightBlue));
                break;
            case COLOR_GREY:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorGrey));
                break;
            case COLOR_LIGHT_YELLOW:
                indicator.setColorFilter(ContextCompat.getColor(activity, R.color.colorLightYellow));
                break;
        }
    }

    public static ArrayList<PaletteColor> getColors(ArrayList<PaletteColor> paletteColors) {
        paletteColors.add(new PaletteColor(COLOR_DEFAULT));
        paletteColors.add(new PaletteColor(COLOR_YELLOW));
        paletteColors.add(new PaletteColor(COLOR_KARINA));
        paletteColors.add(new PaletteColor(COLOR_ORANGE));
        paletteColors.add(new PaletteColor(COLOR_BLUE));
        paletteColors.add(new PaletteColor(COLOR_RED));
        paletteColors.add(new PaletteColor(COLOR_GREEN));
        paletteColors.add(new PaletteColor(COLOR_TURQUOISE));
        paletteColors.add(new PaletteColor(COLOR_GREEN_GREY));
        paletteColors.add(new PaletteColor(COLOR_LIGHT_PINK));
        paletteColors.add(new PaletteColor(COLOR_LIGHT_GREEN));
        paletteColors.add(new PaletteColor(COLOR_LIGHT_BLUE));
        paletteColors.add(new PaletteColor(COLOR_GREY));
        paletteColors.add(new PaletteColor(COLOR_LIGHT_YELLOW));
        return paletteColors;
    }

    public static String getDate() {
        return new SimpleDateFormat("dd MMMM yyyy HH:mm",
                Locale.getDefault()).format(new Date());
    }

    public static void getToast(Activity activity, int message) {
        Toast.makeText(activity, activity.getApplication().getResources().getString(message),
                Toast.LENGTH_SHORT).show();
    }

    public static void shareNote(Activity activity, String title, String text, String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + text + "\n" + url);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        Intent chosenIntent = Intent.createChooser(intent, "");
        activity.startActivity(chosenIntent);
    }

    public static void shareNote(Activity activity, String title, String text, String url,
                                 ContentResolver contentResolver, String picturePath, ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, picturePath, null);
        Uri uri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_TEXT, title + "\n" + text + "\n" + url);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        Intent chosenIntent = Intent.createChooser(intent, "");
        activity.startActivity(chosenIntent);
    }

    public static boolean checkFile(String filePath) {
        File file = new File(filePath);
        return !file.exists();
    }

    public static void deleteImage(String path, Context context) {
        if (path != null && !path.trim().isEmpty()) {
            File fileDelete = new File(path);
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    callBroadCast(context);
                }
            }
        }
    }

    private static void callBroadCast(Context context) {
        MediaScannerConnection.scanFile(context, new String[]{context.getExternalFilesDir(null).getAbsolutePath()}, null, (path, uri) -> {
            Log.e("ExternalStorage", "Scanned " + path + ":");
            Log.e("ExternalStorage", "-> uri=" + uri);
        });
    }

    public static void getToolTipsDetail(Activity activity, View button, ViewGroup layout, int message) {
        ToolTip.Builder builder = new ToolTip.Builder(activity, button, layout,
                activity.getResources().getString(message), ToolTip.POSITION_ABOVE);
        builder.setBackgroundColor(activity.getColor(R.color.colorTransparent));
        builder.setGravity(ToolTip.GRAVITY_CENTER);
        builder.setTextAppearance(R.style.TooltipTextAppearance);
        ToolTipsManager toolTipsManager = new ToolTipsManager();
        toolTipsManager.show(builder.build());
        new Handler(Looper.getMainLooper()).postDelayed(toolTipsManager::dismissAll, 1500);
    }
}
