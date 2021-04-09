package com.appsforlife.mynotes.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.constants.Constants;
import static com.appsforlife.mynotes.constants.Constants.*;

import java.io.File;
import java.io.IOException;

public class ImagePickerDialog {

    private final Activity activity;
    private final PackageManager packageManager;
    private final File storageDir;
    private String photoPath;

    public ImagePickerDialog(Activity activity, PackageManager packageManager, File storageDir) {
        this.activity = activity;
        this.packageManager = packageManager;
        this.storageDir = storageDir;
    }

    public void createImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_dialog_add_image, null);
        builder.setView(view);
        AlertDialog dialogAddImage = builder.create();
        if (dialogAddImage.getWindow() != null) {
            dialogAddImage.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        view.findViewById(R.id.tv_dialog_add_image).setOnClickListener(v -> {
            addImage();
            dialogAddImage.cancel();
        });
        view.findViewById(R.id.tv_dialog_add_photo).setOnClickListener(v -> {
            addPhoto();
            dialogAddImage.cancel();
        });
        dialogAddImage.show();
    }

    private void addImage() {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, Constants.REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            createImage();
        }
    }

    private void addPhoto() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            createPhoto();
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void createImage() {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void createPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            File photoFile;
            photoFile = createPhotoFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity,
                        "com.appsforlife.mynotes.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    public File createPhotoFile() {
        File image = null;
        try {
            image = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg", storageDir);
            setPhotoPath(image.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
