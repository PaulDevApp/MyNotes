package com.appsforlife.mynotes.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.Support;
import com.appsforlife.mynotes.adapters.ColorDetailPaletteAdapter;
import com.appsforlife.mynotes.databinding.ActivityDetailBinding;
import com.appsforlife.mynotes.databinding.LayoutLinkPreviewBinding;
import com.appsforlife.mynotes.databinding.LayoutPaletteBinding;
import com.appsforlife.mynotes.dialogs.ClickLinkDialog;
import com.appsforlife.mynotes.dialogs.DeleteDialog;
import com.appsforlife.mynotes.dialogs.DeleteImageDialog;
import com.appsforlife.mynotes.dialogs.ImagePickerDialog;
import com.appsforlife.mynotes.dialogs.ReplaceImageDialog;
import com.appsforlife.mynotes.dialogs.UrlDialog;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.entities.PaletteColor;
import com.appsforlife.mynotes.listeners.ColorPaletteListener;
import com.appsforlife.mynotes.listeners.DialogClickLinkListener;
import com.appsforlife.mynotes.listeners.DialogCreateLinkListener;
import com.appsforlife.mynotes.listeners.DialogDeleteImageListener;
import com.appsforlife.mynotes.listeners.DialogDeleteNoteListener;
import com.appsforlife.mynotes.listeners.DialogReplaceImageListener;
import com.appsforlife.mynotes.util.LinkPreviewUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import static com.appsforlife.mynotes.util.LinkPreviewUtil.setPreviewLink;
import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.App.*;
import static com.appsforlife.mynotes.constants.Constants.*;

@SuppressLint("ResourceAsColor,SetTextI18n")
public class DetailActivity extends AppCompatActivity implements ColorPaletteListener,
        DialogDeleteImageListener, DialogReplaceImageListener, DialogDeleteNoteListener,
        DialogClickLinkListener, DialogCreateLinkListener {

    private ActivityDetailBinding detailBinding;
    private LayoutPaletteBinding paletteBinding;
    private LayoutLinkPreviewBinding previewBinding;

    private Note note;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private String oldTitle, oldText, oldWebLink, oldColor, oldImagePath;
    private String imagePath;

    private final ArrayList<PaletteColor> paletteColors = new ArrayList<>();
    private ColorDetailPaletteAdapter colorDetailPaletteAdapter;

    private ImagePickerDialog imagePickerDialog;
    private UrlDialog urlDialog;
    private ClickLinkDialog clickLinkDialog;
    private DeleteDialog deleteDialog;
    private ReplaceImageDialog replaceImageDialog;
    private DeleteImageDialog deleteImageDialog;

    private boolean isFromGallery, isCheck, isFavorite, prevDone, prevFavorite;


    public static void start(Activity caller, Note note, RelativeLayout noteLayout) {
        Intent intent = new Intent(caller, DetailActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(caller, noteLayout, noteLayout.getTransitionName());
        if (note != null) {
            intent.putExtra(NOTE, note);
        }
        caller.startActivity(intent, options.toBundle());
    }

    public static void start(Activity caller, Note note) {
        Intent intent = new Intent(caller, DetailActivity.class);
        if (note != null) {
            intent.putExtra(NOTE, note);
        }
        caller.startActivity(intent);
    }

    ActivityResultLauncher<Intent> getSpeechResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        ArrayList<String> text = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        detailBinding.etInputText.setText(detailBinding.etInputText.getText().toString().trim()
                                + " " + text.get(0) + ".");
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Support.setTheme();

        detailBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        paletteBinding = detailBinding.palette;
        previewBinding = detailBinding.includePreviewLink;
        setContentView(detailBinding.getRoot());

        setNoteTextSize();

        imagePickerDialog = new ImagePickerDialog(this, getPackageManager(), getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        urlDialog = new UrlDialog(this, this);
        clickLinkDialog = new ClickLinkDialog(this, this);
        deleteDialog = new DeleteDialog(this, this);
        replaceImageDialog = new ReplaceImageDialog(this, this);
        deleteImageDialog = new DeleteImageDialog(this, this);

        if (getIntent().hasExtra(NOTE)) {
            note = getIntent().getParcelableExtra(NOTE);
            getPreviousNote();
            colorPicker = note.getColor();
        } else {
            note = new Note();
            colorPicker = COLOR_DEFAULT;
            detailBinding.tvTextDateTimeCreated.setText(getDate());
            detailBinding.etInputTitle.setFocusableInTouchMode(true);
            detailBinding.etInputTitle.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        if (getIntent().getBooleanExtra(IS_FROM_QUICK_ACTIONS, false)) {
            String type = getIntent().getStringExtra(QUICK_ACTIONS_TYPE);
            if (type != null) {
                switch (type) {
                    case ACTION_IMAGE:
                        isFromGallery = true;
                        imagePath = getIntent().getStringExtra(IMAGE_PATH);
                        setPhoto(imagePath);
                        startViewAnimation(detailBinding.ivShowPhoto, this, R.anim.appearance);
                        startViewAnimation(detailBinding.ivDeleteImage, this, R.anim.appearance);
                        break;
                    case ACTION_URL:
                        previewBinding.tvSiteUrl.setText(getIntent().getStringExtra(ACTION_URL));
                        note.setWebLink(getIntent().getStringExtra(ACTION_URL));
                        setPreviewLink(getIntent().getStringExtra(ACTION_URL),
                                previewBinding.ivSiteImage, previewBinding.tvSiteName, previewBinding.tvSiteDescription);
                        previewBinding.clPreviewLink.setVisibility(View.VISIBLE);
                        previewBinding.tvSiteUrl.setVisibility(View.VISIBLE);
                        break;
                    case ACTION_CAMERA:
                        isFromGallery = false;
                        imagePath = getIntent().getStringExtra(CAMERA_PATH);
                        setPhoto(imagePath);
                        startViewAnimation(detailBinding.ivShowPhoto, this, R.anim.appearance);
                        startViewAnimation(detailBinding.ivDeleteImage, this, R.anim.appearance);
                        break;
                    case ACTION_SPEECH:
                        detailBinding.etInputText.setText(getIntent().getStringExtra(SPEECH_STRING));
                        break;
                }
            }
        }

        Intent receivedIntent = getIntent();
        String action = receivedIntent.getAction();
        String type = receivedIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(receivedIntent);
            } else if (type.startsWith("image/")) {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_CODE_SHARE_CREATE_STORAGE_PERMISSION);
                } else {
                    handleSendImage(receivedIntent);
                }
            }
        }

        if (imagePath == null) {
            imagePath = "";
            oldImagePath = imagePath;
        }

        initPalette();

        setCheckImageDone();
        setFavoriteImage();
        updateStrokeOut(note, detailBinding.etInputTitle, detailBinding.etInputText);

        detailBinding.ivDeleteImage.setOnClickListener(v -> {
                    if (!App.getInstance().isDelete()) {
                        if (!checkFile(imagePath)) {
                            deleteImageDialog.createDeleteImageDialog();
                        } else {
                            clearImage();
                        }
                    } else {
                        clearImage();
                    }
                }
        );

        detailBinding.ivBackToMain.setOnClickListener(v -> onBackPressed());

        detailBinding.ivDeleteNote.setOnClickListener(v -> {
            if (!App.getInstance().isConfirmed()) {
                deleteDialog.createDeleteNoteDialog();
            } else {
                App.getInstance().getNoteDao().deleteNote(note);
                finish();
            }
        });

        previewBinding.clPreviewLink.setOnClickListener(v -> clickLinkDialog.createClickLinkDialog(
                note.getWebLink()));

        detailBinding.ivSpeech.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().getLanguage());
            try {
                getSpeechResult.launch(intent);
            } catch (Exception e) {
                getToast(this, R.string.speech_message);
            }
        });

        setBackgroundNoteColor(detailBinding.rlDetail, note.getColor());
    }

    private void copy() {
        Note note = new Note();
        note.setTitle(detailBinding.etInputTitle.getText().toString());
        note.setText(detailBinding.etInputText.getText().toString());
        note.setWebLink(previewBinding.tvSiteUrl.getText().toString());
        note.setDone(isCheck);
        note.setFavorite(isFavorite);
        note.setDateTime(getDate());
        note.setColor(colorPicker);
        note.setImagePath(imagePath);

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            saveImage(note);
        }

        if (checkSomeText(note)) {
            App.getInstance().getNoteDao().insertNote(note);
            getToast(this, R.string.note_copied);
        } else {
            getToast(this, R.string.note_is_empty);
        }
    }

    private void clearImage() {
        detailBinding.ivShowPhoto.setImageBitmap(null);
        detailBinding.ivShowPhoto.setVisibility(View.GONE);
        detailBinding.ivDeleteImage.setVisibility(View.GONE);
        imagePath = "";
        note.setImagePath(imagePath);
    }

    private void handleSendImage(Intent intent) {
        isFromGallery = true;
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        imagePath = imageUri.toString();
        setPhoto(imagePath);
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            if (sharedText.startsWith("www") || sharedText.startsWith("https://")) {
                previewBinding.tvSiteUrl.setText(sharedText);
                setPreviewLink(getIntent().getStringExtra(ACTION_URL),
                        previewBinding.ivSiteImage, previewBinding.tvSiteName, previewBinding.tvSiteDescription);
                previewBinding.clPreviewLink.setVisibility(View.VISIBLE);
                previewBinding.tvSiteUrl.setVisibility(View.VISIBLE);
                note.setWebLink(sharedText);
            } else {
                detailBinding.etInputText.setText(sharedText);
            }
        }
    }

    private void setFavoriteImage() {
        if (!isFavorite) {
            paletteBinding.ivFavorite.setImageResource(R.drawable.ic_lock_open);
        } else {
            paletteBinding.ivFavorite.setImageResource(R.drawable.ic_lock_close);
        }
    }

    private void setPhoto(String imageUri) {
        detailBinding.ivShowPhoto.setVisibility(View.VISIBLE);
        detailBinding.ivDeleteImage.setVisibility(View.VISIBLE);
        detailBinding.ivShowPhoto.setImageURI(Uri.parse(imageUri));
    }

    private void setCheckImageDone() {
        if (!isCheck) {
            paletteBinding.ivDone.setImageResource(R.drawable.ic_unchecked);
        } else {
            paletteBinding.ivDone.setImageResource(R.drawable.ic_check_circle);
        }
    }

    private boolean checkSomeText(Note note) {
        return !detailBinding.etInputTitle.getText().toString().trim().isEmpty() ||
                !detailBinding.etInputText.getText().toString().trim().isEmpty()
                || (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) || (note.getWebLink() != null &&
                !note.getWebLink().trim().isEmpty());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getPreviousNote() {
        detailBinding.etInputTitle.setText(note.getTitle());
        detailBinding.etInputText.setText(note.getText());
        detailBinding.tvTextDateTimeCreated.setText(note.getDateTime());
        detailBinding.tvTextDateTimeEdited.setText(note.getDateTimeEdited());

        detailBinding.tvDateInfoCreated.setVisibility(View.VISIBLE);
        detailBinding.ivDeleteNote.setVisibility(View.VISIBLE);

        paletteBinding.ivDone.setVisibility(View.VISIBLE);
        paletteBinding.ivAddNote.setVisibility(View.VISIBLE);

        startViewAnimation(detailBinding.tvDateInfoCreated, this, R.anim.appearance);
        startViewAnimation(detailBinding.ivDeleteNote, this, R.anim.appearance);


        if (detailBinding.tvTextDateTimeEdited.getText().toString().length() > 0) {
            detailBinding.tvTextDateTimeEdited.setVisibility(View.VISIBLE);
            startViewAnimation(detailBinding.tvTextDateTimeEdited, this, R.anim.appearance);
            detailBinding.tvDateInfoEdited.setVisibility(View.VISIBLE);
            startViewAnimation(detailBinding.tvDateInfoEdited, this, R.anim.appearance);
        }

        if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) {
            imagePath = note.getImagePath();
            if (checkFile(imagePath)) {
                detailBinding.ivDeleteImage.setImageDrawable(getDrawable(R.drawable.photo_error_icon));
                detailBinding.ivDeleteImage.setVisibility(View.VISIBLE);
                detailBinding.ivShowPhoto.setVisibility(View.VISIBLE);
            } else {
                setPhoto(imagePath);
                detailBinding.ivShowPhoto.setOnClickListener(v -> {
                    Intent detailPicture = new Intent(this, PictureActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, detailBinding.ivShowPhoto, "image");
                    detailPicture.putExtra(PICTURE_PATH, imagePath);
                    startActivity(detailPicture, options.toBundle());
                    overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
                });
            }
            oldImagePath = imagePath;
        }

        if (note.getWebLink() != null && !note.getWebLink().trim().isEmpty()) {
            previewBinding.tvSiteUrl.setText(note.getWebLink());
            setPreviewLink(note.getWebLink(), previewBinding.ivSiteImage,
                    previewBinding.tvSiteName, previewBinding.tvSiteDescription);
            previewBinding.tvSiteUrl.setVisibility(View.VISIBLE);
            previewBinding.clPreviewLink.setVisibility(View.VISIBLE);
        } else {
            note.setWebLink("");
        }
        oldWebLink = note.getWebLink();

        oldTitle = note.getTitle();
        oldText = note.getText();
        oldImagePath = imagePath;
        oldColor = note.getColor();
        isCheck = note.isDone();
        isFavorite = note.isFavorite();
        prevDone = note.isDone();
        prevFavorite = note.isFavorite();

    }

    private void initialNote() {
        note.setTitle(detailBinding.etInputTitle.getText().toString().trim());
        note.setText(detailBinding.etInputText.getText().toString().trim());
        note.setDateTime(detailBinding.tvTextDateTimeCreated.getText().toString().trim());
        note.setColor(colorPicker);
        note.setImagePath(imagePath);
    }

    private void initPalette() {
        bottomSheetBehavior = BottomSheetBehavior.from(paletteBinding.llPalette);

        colorDetailPaletteAdapter = new ColorDetailPaletteAdapter(paletteColors, this);
        paletteBinding.rvColorPalette.setAdapter(colorDetailPaletteAdapter);
        colorDetailPaletteAdapter.setPaletteColors(getColors(paletteColors));

        paletteBinding.tvChangeNoteColor.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        paletteBinding.ivFavorite.setOnClickListener(v -> {
            if (!note.isFavorite()) {
                note.setFavorite(true);
                isFavorite = true;
            } else {
                note.setFavorite(false);
                isFavorite = false;
            }
            setFavoriteImage();
        });

        paletteBinding.ivFavorite.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivFavorite, detailBinding.rlDetail,
                    R.string.tooltips_favorite);
            return true;
        });


        paletteBinding.ivShare.setOnClickListener(v -> {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                shareNote(this, detailBinding.etInputTitle.getText().toString(),
                        detailBinding.etInputText.getText().toString(), previewBinding.tvSiteUrl.getText().toString(),
                        getContentResolver(), imagePath, detailBinding.ivShowPhoto);
            } else {
                shareNote(this, detailBinding.etInputTitle.getText().toString(),
                        detailBinding.etInputText.getText().toString(), previewBinding.tvSiteUrl.getText().toString());
            }
        });

        paletteBinding.ivShare.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivShare, detailBinding.rlDetail,
                    R.string.tooltips_share);
            return true;
        });

        paletteBinding.ivAddNote.setOnClickListener(v -> copy());

        paletteBinding.ivAddNote.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddNote, detailBinding.rlDetail,
                    R.string.tooltips_make_a_copy);
            return true;
        });

        paletteBinding.ivAddPhotoCreate.setOnClickListener(v -> {
            if (!App.getInstance().isReplace()) {
                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    if (checkFile(imagePath)) {
                        imagePickerDialog.createImagePickerDialog();
                    } else {
                        replaceImageDialog.createReplaceImageDialog();
                    }
                } else {
                    imagePickerDialog.createImagePickerDialog();
                }
            } else {
                imagePickerDialog.createImagePickerDialog();
            }
        });

        paletteBinding.ivAddPhotoCreate.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddPhotoCreate, detailBinding.rlDetail,
                    R.string.tooltips_add_picture);
            return true;
        });

        paletteBinding.ivAddWebCreate.setOnClickListener(v -> urlDialog.createDetailUrlDialog(previewBinding.tvSiteUrl));

        paletteBinding.ivAddWebCreate.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddWebCreate, detailBinding.rlDetail,
                    R.string.tooltips_add_url);
            return true;
        });

        paletteBinding.ivDone.setOnClickListener(v -> {
            if (!note.isDone()) {
                note.setDone(true);
                isCheck = true;
            } else {
                note.setDone(false);
                isCheck = false;
            }
            setCheckImageDone();
            updateStrokeOut(note, detailBinding.etInputTitle, detailBinding.etInputText);
        });
        paletteBinding.ivDone.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivDone, detailBinding.rlDetail,
                    R.string.tooltips_add_complete);
            return true;
        });

        paletteBinding.ivCopyTextNote.setOnClickListener(v -> {
            if (!detailBinding.etInputTitle.getText().toString().trim().isEmpty()
                    || !detailBinding.etInputText.getText().toString().trim().isEmpty()
                    || !previewBinding.tvSiteUrl.getText().toString().trim().isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", detailBinding.etInputTitle.getText().toString().trim()
                        + "\n" + detailBinding.etInputText.getText().toString().trim() + "\n" +
                        previewBinding.tvSiteUrl.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                getToast(this, R.string.copy_text_note);
            } else {
                getToast(this, R.string.no_text_to_copy);
            }
        });

        paletteBinding.ivCopyTextNote.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivCopyTextNote, detailBinding.rlDetail,
                    R.string.tooltips_copy_text_note);
            return true;
        });

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        setColorIndicator(colorPicker, paletteBinding.ivColorIndicator, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerDialog.createImage();
            } else {
                getToast(this, R.string.permission_denied);
            }
        } else if (requestCode == REQUEST_CODE_CAMERA_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerDialog.createPhoto();
            } else {
                getToast(this, R.string.permission_denied);
            }
        } else if (requestCode == REQUEST_CODE_SHARE_CREATE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = getIntent();
                String action = intent.getAction();
                String type = intent.getType();
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if (type.startsWith("image/")) {
                        handleSendImage(intent);
                    }
                }
            } else {
                getToast(this, R.string.permission_denied);
            }
        }
    }

    private void saveImage(Note note) {
        if (isFromGallery) {
            Bitmap finalBitmap = ((BitmapDrawable) detailBinding.ivShowPhoto.getDrawable()).getBitmap();
            File imageFile = imagePickerDialog.createPhotoFile();
            try {
                FileOutputStream out = new FileOutputStream(imageFile);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imagePath = imageFile.getAbsolutePath();
            note.setImagePath(imageFile.getAbsolutePath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                isFromGallery = true;
                imagePath = data.getData().toString();
                setPhoto(imagePath);
                startViewAnimation(detailBinding.ivShowPhoto, this, R.anim.appearance);
                startViewAnimation(detailBinding.ivDeleteImage, this, R.anim.appearance);
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            isFromGallery = false;
            imagePath = imagePickerDialog.getPhotoPath();
            setPhoto(imagePath);
            startViewAnimation(detailBinding.ivShowPhoto, this, R.anim.appearance);
            startViewAnimation(detailBinding.ivDeleteImage, this, R.anim.appearance);
        }
    }

    private boolean isUpdate() {
        return !oldTitle.equals(note.getTitle().trim())
                || !oldText.equals(note.getText())
                || !oldImagePath.equals(imagePath)
                || !oldColor.equals(note.getColor().trim())
                || !oldWebLink.equals(note.getWebLink())
                || prevDone != note.isDone()
                || prevFavorite != note.isFavorite();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            addNote();
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_static_animation, R.anim.zoom_out);
        }
    }

    private void addNote() {
        initialNote();
        if (getIntent().hasExtra(NOTE)) {
            if (isUpdate()) {
                if (checkSomeText(note)) {
                    saveImage(note);
                    note.setDateTimeEdited(getDate());
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        App.getInstance().getNoteDao().update(note);
                        getToast(this, R.string.note_updated);
                    }, 600);
                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        App.getInstance().getNoteDao().deleteNote(note);
                        getToast(this, R.string.empty_note_deleted);
                    }, 600);
                }
            }
        } else {
            if (checkSomeText(note)) {
                saveImage(note);
                App.getInstance().getNoteDao().insertNote(note);
                getToast(this, R.string.note_saved);
            }
        }
    }

    @Override
    public void onColorPaletteClickListener(PaletteColor paletteColor, ImageView imageViewColor) {
        colorPicker = paletteColor.getColor();
        imageViewColor.setVisibility(View.VISIBLE);
        setColorIndicator(colorPicker, paletteBinding.ivColorIndicator, this);
        colorDetailPaletteAdapter.notifyDataSetChanged();
        setBackgroundNoteColor(detailBinding.rlDetail, colorPicker);
    }

    private void setNoteTextSize() {
        switch (App.getInstance().getTextSize()) {
            case 1:
                detailBinding.etInputTitle.setTextSize(23);
                detailBinding.etInputText.setTextSize(19);
                detailBinding.tvTextDateTimeCreated.setTextSize(12);
                detailBinding.tvTextDateTimeEdited.setTextSize(12);
                detailBinding.tvDateInfoCreated.setTextSize(12);
                detailBinding.tvDateInfoEdited.setTextSize(12);
                break;
            case 2:
                detailBinding.etInputTitle.setTextSize(27);
                detailBinding.etInputText.setTextSize(23);
                detailBinding.tvTextDateTimeCreated.setTextSize(14);
                detailBinding.tvTextDateTimeEdited.setTextSize(14);
                detailBinding.tvDateInfoCreated.setTextSize(14);
                detailBinding.tvDateInfoEdited.setTextSize(14);
                break;
            case 3:
                detailBinding.etInputTitle.setTextSize(31);
                detailBinding.etInputText.setTextSize(27);
                detailBinding.tvTextDateTimeCreated.setTextSize(16);
                detailBinding.tvTextDateTimeEdited.setTextSize(16);
                detailBinding.tvDateInfoCreated.setTextSize(16);
                detailBinding.tvDateInfoEdited.setTextSize(16);
                break;
            case 4:
                detailBinding.etInputTitle.setTextSize(35);
                detailBinding.etInputText.setTextSize(31);
                detailBinding.tvTextDateTimeCreated.setTextSize(18);
                detailBinding.tvTextDateTimeEdited.setTextSize(18);
                detailBinding.tvDateInfoCreated.setTextSize(18);
                detailBinding.tvDateInfoEdited.setTextSize(18);
                break;
        }
    }

    @Override
    public void dialogDeleteImageCallback(boolean confirm) {
        if (confirm) {
            clearImage();
        }
    }

    @Override
    public void dialogReplaceCallback(boolean confirm) {
        if (confirm) {
            imagePickerDialog.createImagePickerDialog();
        }
    }

    @Override
    public void dialogDeleteCallback(boolean confirm) {
        if (confirm) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                App.getInstance().getNoteDao().deleteNote(note);
                getToast(this, R.string.note_deleted);
            }, 300);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinkPreviewUtil.dispose();
    }

    @Override
    public void onClickLink(boolean isOpen) {
        if (isOpen) {
            if (previewBinding.tvSiteUrl.getText().toString().startsWith("www")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + previewBinding.tvSiteUrl.getText().toString().trim()));
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(previewBinding.tvSiteUrl.getText().toString().trim()));
                startActivity(intent);
            }
        }

    }

    @Override
    public void onShareLink(boolean isShare) {
        if (isShare) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, note.getWebLink());
            Intent chosenIntent = Intent.createChooser(intent, "");
            startActivity(chosenIntent);
        }
    }

    @Override
    public void onEditLink(boolean isEdit) {
        if (isEdit) {
            urlDialog.createDetailUrlDialog(previewBinding.tvSiteUrl);
            note.setWebLink(previewBinding.tvSiteUrl.getText().toString());
        }
    }

    @Override
    public void onDeleteLink(boolean isDelete) {
        if (isDelete) {
            note.setWebLink("");
            previewBinding.tvSiteUrl.setText("");
            previewBinding.tvSiteUrl.setVisibility(View.GONE);
            previewBinding.clPreviewLink.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCopyLink(boolean isCopied) {
        if (isCopied) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", previewBinding.tvSiteUrl.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            getToast(this, R.string.link_copied);
        }
    }

    @Override
    public void onCreateLink(String link) {
        note.setWebLink(link);
        setPreviewLink(link, previewBinding.ivSiteImage,
                previewBinding.tvSiteName, previewBinding.tvSiteDescription);
        previewBinding.tvSiteUrl.setText(link);
        previewBinding.tvSiteUrl.setVisibility(View.VISIBLE);
        previewBinding.clPreviewLink.setVisibility(View.VISIBLE);
        startViewAnimation(previewBinding.clPreviewLink, this, R.anim.appearance);
        getToast(this, R.string.toast_valid_url);
    }
}
