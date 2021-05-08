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
import android.graphics.Paint;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.LinkPreviewUtil;
import com.appsforlife.mynotes.R;
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
import com.appsforlife.mynotes.listeners.DialogDeleteImageListener;
import com.appsforlife.mynotes.listeners.DialogDeleteNoteListener;
import com.appsforlife.mynotes.listeners.DialogReplaceImageListener;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.tomergoldst.tooltips.ToolTipsManager;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.App.*;
import static com.appsforlife.mynotes.constants.Constants.*;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("ResourceAsColor")
public class DetailNoteActivity extends AppCompatActivity implements ColorPaletteListener,
        DialogDeleteImageListener, DialogReplaceImageListener, DialogDeleteNoteListener {

    private ActivityDetailBinding detailBinding;
    private LayoutPaletteBinding paletteBinding;
    private LayoutLinkPreviewBinding previewBinding;

    private Note note;

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    private String oldTitle, oldText, oldWebLink, oldColor;
    private String imagePath;
    private String oldImagePath;

    private final ArrayList<PaletteColor> paletteColors = new ArrayList<>();
    private ColorDetailPaletteAdapter colorDetailPaletteAdapter;

    private ImagePickerDialog imagePickerDialog;
    private UrlDialog urlDialog;
    private ClickLinkDialog clickLinkDialog;
    private DeleteDialog deleteDialog;
    private ReplaceImageDialog replaceImageDialog;
    private DeleteImageDialog deleteImageDialog;

    private boolean isFromGallery;
    private boolean isCheck;
    private boolean isFavorite;
    private boolean prevDone;
    private boolean prevFavorite;

    private ToolTipsManager toolTipsManager;

    public static void start(Activity caller, Note note, RelativeLayout noteLayout) {
        Intent intent = new Intent(caller, DetailNoteActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(caller, noteLayout, "note");
        if (note != null) {
            intent.putExtra(NOTE, note);
        }
        caller.startActivity(intent, options.toBundle());
    }

    public static void start(Activity caller, Note note) {
        Intent intent = new Intent(caller, DetailNoteActivity.class);
        if (note != null) {
            intent.putExtra(NOTE, note);
        }
        caller.startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDarkTheme();

        detailBinding = ActivityDetailBinding.inflate(getLayoutInflater());
        paletteBinding = detailBinding.palette;
        previewBinding = detailBinding.includePreviewLink;
        setContentView(detailBinding.getRoot());

        setNoteTextSize();

        toolTipsManager = new ToolTipsManager();

        imagePickerDialog = new ImagePickerDialog(this, getPackageManager(), getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        urlDialog = new UrlDialog(this);
        clickLinkDialog = new ClickLinkDialog(this, (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        deleteDialog = new DeleteDialog(this, this);
        replaceImageDialog = new ReplaceImageDialog(this, this);
        deleteImageDialog = new DeleteImageDialog(this, this);

        bottomSheetBehavior = BottomSheetBehavior.from(paletteBinding.llPalette);
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
                        detailBinding.tvUrl.setText(getIntent().getStringExtra(ACTION_URL));
                        detailBinding.tvUrl.setVisibility(View.VISIBLE);
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

        colorDetailPaletteAdapter = new ColorDetailPaletteAdapter(paletteColors, this);
        paletteBinding.rvColorPalette.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        paletteBinding.rvColorPalette.setAdapter(colorDetailPaletteAdapter);
        colorDetailPaletteAdapter.setPaletteColors(getColors(paletteColors));


        setCheckImageDone();
        setFavoriteImage();
        updateStrokeOut(note, detailBinding.etInputTitle, detailBinding.etInputText);

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
                    R.string.tooltips_favorite, toolTipsManager);
            return true;
        });


        paletteBinding.ivShare.setOnClickListener(v -> {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                shareNote(this, detailBinding.etInputTitle.getText().toString(),
                        detailBinding.etInputText.getText().toString(), detailBinding.tvUrl.getText().toString(),
                        getContentResolver(), imagePath, detailBinding.ivShowPhoto);
            } else {
                shareNote(this, detailBinding.etInputTitle.getText().toString(),
                        detailBinding.etInputText.getText().toString(), detailBinding.tvUrl.getText().toString());
            }
        });

        paletteBinding.ivShare.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivShare, detailBinding.rlDetail,
                    R.string.tooltips_share, toolTipsManager);
            return true;
        });

        paletteBinding.ivAddNote.setOnClickListener(v -> copy());

        paletteBinding.ivAddNote.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddNote, detailBinding.rlDetail,
                    R.string.tooltips_make_a_copy, toolTipsManager);
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

        paletteBinding.ivAddPhotoCreate.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddPhotoCreate, detailBinding.rlDetail,
                    R.string.tooltips_add_picture, toolTipsManager);
            return true;
        });

        paletteBinding.ivAddWebCreate.setOnClickListener(v -> urlDialog.createDetailUrlDialog(detailBinding.tvUrl));
        paletteBinding.ivAddWebCreate.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivAddWebCreate, detailBinding.rlDetail,
                    R.string.tooltips_add_url, toolTipsManager);
            return true;
        });

        detailBinding.ivBackToMain.setOnClickListener(v -> onBackPressed());

        detailBinding.ivDeleteNote.setOnClickListener(v -> {
            if (!App.getInstance().isConfirmed()) {
                deleteDialog.createDeleteNoteDialog();
            } else {
                App.getInstance().getNoteDao().deleteNote(note);
                finish();
            }
        });

        detailBinding.tvUrl.setOnClickListener(v -> clickLinkDialog.createClickLinkDialog(
                note, detailBinding.tvUrl, detailBinding.linkPreviewDetail, urlDialog));

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
                    R.string.tooltips_add_complete, toolTipsManager);
            return true;
        });

        paletteBinding.ivCopyTextNote.setOnClickListener(v -> {
            if (!detailBinding.etInputTitle.getText().toString().trim().isEmpty()
                    || !detailBinding.etInputText.getText().toString().trim().isEmpty()
                    || !detailBinding.tvUrl.getText().toString().trim().isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", detailBinding.etInputTitle.getText().toString().trim()
                        + "\n" + detailBinding.etInputText.getText().toString().trim() + "\n" + detailBinding.tvUrl.getText().toString().trim());
                clipboard.setPrimaryClip(clip);
                getToast(this, R.string.copy_text_note);
            } else {
                getToast(this, R.string.no_text_to_copy);
            }
        });

        paletteBinding.ivCopyTextNote.setOnLongClickListener(v -> {
            getToolTipsDetail(this, paletteBinding.ivCopyTextNote, detailBinding.rlDetail,
                    R.string.tooltips_copy_text_note, toolTipsManager);
            return true;
        });

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        detailBinding.tvUrl.setPaintFlags(detailBinding.tvUrl.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        detailBinding.ivSpeech.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().getLanguage());
            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH);
            } catch (Exception e) {
                getToast(this, R.string.speech_message);
            }
        });


    }

    private void copy() {
        Note note = new Note();
        note.setTitle(detailBinding.etInputTitle.getText().toString());
        note.setText(detailBinding.etInputText.getText().toString());
        note.setWebLink(detailBinding.tvUrl.getText().toString());
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
                detailBinding.tvUrl.setVisibility(View.VISIBLE);
                detailBinding.tvUrl.setText(sharedText);
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
        Glide.with(this).load(imageUri).into(detailBinding.ivShowPhoto);
        detailBinding.ivShowPhoto.setVisibility(View.VISIBLE);
        detailBinding.ivDeleteImage.setVisibility(View.VISIBLE);
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

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void getPreviousNote() {
        detailBinding.etInputTitle.setText(note.getTitle());
        detailBinding.etInputText.setText(note.getText());
        detailBinding.tvTextDateTimeCreated.setText(note.getDateTime());
        detailBinding.tvTextDateTimeEdited.setText(note.getDateTimeEdited());

        detailBinding.tvUrl.setVisibility(View.VISIBLE);
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
                Glide.with(this).load(R.drawable.photo_error_icon).into(detailBinding.ivShowPhoto);
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
            detailBinding.tvUrl.setText(note.getWebLink());
            detailBinding.tvUrl.setVisibility(View.VISIBLE);
            oldWebLink = note.getWebLink();

            if (detailBinding.tvUrl.getText().toString().startsWith("https://")) {
                setPreviewLink();
            } else {
                detailBinding.linkPreviewDetail.setVisibility(View.GONE);
            }
        } else {
            note.setWebLink("");
            detailBinding.tvUrl.setText(note.getWebLink());
            detailBinding.tvUrl.setVisibility(View.GONE);
            oldWebLink = note.getWebLink();
        }

        oldTitle = note.getTitle();
        oldText = note.getText();
        oldImagePath = imagePath;
        oldColor = note.getColor();
        isCheck = note.isDone();
        isFavorite = note.isFavorite();
        prevDone = note.isDone();
        prevFavorite = note.isFavorite();

    }

    private void setPreviewLink() {
        LinkPreviewUtil.getJSOUPContent(detailBinding.tvUrl.getText().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            if (result != null) {
                                Elements metaTags = result.getElementsByTag("meta");
                                for (Element element : metaTags) {
                                    if (element.attr("property").equals("og:image")) {
                                        Glide.with(this).load(element.attr("content"))
                                                .into(previewBinding.ivPreviewImageLink);
                                    } else if (element.attr("name").equals("title")) {
                                        previewBinding.tvPreviewTitleLink.setText(element.attr("content"));
                                        previewBinding.tvPreviewTitleLink.setVisibility(View.VISIBLE);
                                    } else if (element.attr("name").equals("description")) {
                                        previewBinding.tvPreviewDescriptionLink.setText(element.attr("content"));
                                        previewBinding.tvPreviewDescriptionLink.setVisibility(View.VISIBLE);
                                        if (previewBinding.tvPreviewTitleLink.getVisibility() == View.GONE) {
                                            previewBinding.tvPreviewDescriptionLink.setMaxLines(3);
                                        }
                                    } else if (element.attr("property").equals("og:url")) {
                                        previewBinding.tvPreviewUrl.setText(element.attr("content"));
                                        previewBinding.tvPreviewUrl.setVisibility(View.VISIBLE);
                                    }
                                    startViewAnimation(detailBinding.linkPreviewDetail, this, R.anim.appearance);
                                    detailBinding.linkPreviewDetail.setVisibility(View.VISIBLE);

                                    detailBinding.linkPreviewDetail.setOnClickListener(v -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(detailBinding.tvUrl.getText().toString()));
                                        startActivity(intent);
                                    });
                                }
                            } else {
                                detailBinding.linkPreviewDetail.setVisibility(View.GONE);
                            }
                        },
                        throwable -> detailBinding.linkPreviewDetail.setVisibility(View.GONE));
    }

    private void initialNote() {
        note.setTitle(detailBinding.etInputTitle.getText().toString().trim());
        note.setText(detailBinding.etInputText.getText().toString().trim());
        note.setDateTime(detailBinding.tvTextDateTimeCreated.getText().toString().trim());
        note.setColor(colorPicker);
        note.setImagePath(imagePath);

        if (detailBinding.tvUrl.getVisibility() == View.VISIBLE) {
            note.setWebLink(detailBinding.tvUrl.getText().toString());
        }

    }

    private void initPalette() {
        paletteBinding.tvChangeNoteColor.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    @SuppressLint("SetTextI18n")
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
        } else if (requestCode == REQUEST_CODE_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                detailBinding.etInputText.setText(detailBinding.etInputText.getText().toString().trim()
                        + " " + text.get(0) + ".");
            }
        }
    }

    private boolean isUpdate() {
        return !oldTitle.equals(note.getTitle().trim())
                || !oldText.equals(note.getText().trim())
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
                    App.getInstance().getNoteDao().deleteNote(note);
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
    }

    private void setNoteTextSize() {
        switch (App.getInstance().getTextSize()) {
            case 1:
                detailBinding.etInputTitle.setTextSize(23);
                detailBinding.tvUrl.setTextSize(17);
                detailBinding.etInputText.setTextSize(19);
                detailBinding.tvTextDateTimeCreated.setTextSize(12);
                detailBinding.tvTextDateTimeEdited.setTextSize(12);
                detailBinding.tvDateInfoCreated.setTextSize(12);
                detailBinding.tvDateInfoEdited.setTextSize(12);
                break;
            case 2:
                detailBinding.etInputTitle.setTextSize(27);
                detailBinding.tvUrl.setTextSize(21);
                detailBinding.etInputText.setTextSize(23);
                detailBinding.tvTextDateTimeCreated.setTextSize(14);
                detailBinding.tvTextDateTimeEdited.setTextSize(14);
                detailBinding.tvDateInfoCreated.setTextSize(14);
                detailBinding.tvDateInfoEdited.setTextSize(14);
                break;
            case 3:
                detailBinding.etInputTitle.setTextSize(31);
                detailBinding.tvUrl.setTextSize(25);
                detailBinding.etInputText.setTextSize(27);
                detailBinding.tvTextDateTimeCreated.setTextSize(16);
                detailBinding.tvTextDateTimeEdited.setTextSize(16);
                detailBinding.tvDateInfoCreated.setTextSize(16);
                detailBinding.tvDateInfoEdited.setTextSize(16);
                break;
            case 4:
                detailBinding.etInputTitle.setTextSize(35);
                detailBinding.tvUrl.setTextSize(29);
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
            App.getInstance().getNoteDao().deleteNote(note);
            finish();
        }
    }
}
