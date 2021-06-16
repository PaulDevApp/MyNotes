package com.appsforlife.mynotes.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.Support;
import com.appsforlife.mynotes.adapters.NotesAdapter;
import com.appsforlife.mynotes.bottomsheet.MainBottomSheetFragment;
import com.appsforlife.mynotes.constants.Constants;
import com.appsforlife.mynotes.databinding.ActivityMainBinding;
import com.appsforlife.mynotes.databinding.LayoutMultiplyBinding;
import com.appsforlife.mynotes.dialogs.DeleteDialog;
import com.appsforlife.mynotes.dialogs.ImagePickerDialog;
import com.appsforlife.mynotes.dialogs.PaletteDialog;
import com.appsforlife.mynotes.dialogs.UrlDialog;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.listeners.DialogCreateLinkListener;
import com.appsforlife.mynotes.listeners.DialogDeleteNoteListener;
import com.appsforlife.mynotes.listeners.DialogPaletteListener;
import com.appsforlife.mynotes.listeners.NoteListener;
import com.appsforlife.mynotes.listeners.NoteLongListener;
import com.appsforlife.mynotes.listeners.NoteSelectListener;
import com.appsforlife.mynotes.model.MainViewModel;
import com.appsforlife.mynotes.util.LinkPreviewUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class MainActivity extends AppCompatActivity implements NoteListener, NoteSelectListener,
        NoteLongListener, DialogDeleteNoteListener, MainBottomSheetFragment.BottomSheetSetSortingListener,
        MainBottomSheetFragment.BottomSheetSetViewListener, DialogPaletteListener, DialogCreateLinkListener,
        MainBottomSheetFragment.BottomSheetSettingsListener {

    private NotesAdapter notesAdapter;
    private ArrayList<Note> notesFromDB;

    private ActivityMainBinding mainBinding;
    private LayoutMultiplyBinding multiplyBinding;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;

    private ImagePickerDialog imagePickerDialog;
    private UrlDialog urlDialog;
    private DeleteDialog deleteDialog;
    private PaletteDialog paletteDialog;

    private boolean isFind;
    private boolean isClick;
    private boolean isAnim;
    private boolean isSelectedAll;
    public static int countSelected;

    ActivityResultLauncher<Intent> getSpeechResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        ArrayList<String> text = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
                        detailIntent.putExtra(IS_FROM_QUICK_ACTIONS, true);
                        detailIntent.putExtra(QUICK_ACTIONS_TYPE, ACTION_SPEECH);
                        detailIntent.putExtra(SPEECH_STRING, text.get(0) + ".");
                        startActivity(detailIntent);
                        overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
                        throwOff(400);
                    }
                }
            });

    @SuppressLint("SupportAnnotationUsage")
    @AnimRes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Support.setTheme();

        notesFromDB = new ArrayList<>();

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        com.appsforlife.mynotes.databinding.LayoutToolbarBinding toolbarBinding = mainBinding.toolbarMain;
        multiplyBinding = mainBinding.multiply;
        setContentView(mainBinding.getRoot());

        setSupportActionBar(toolbarBinding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        imagePickerDialog = new ImagePickerDialog(this, getPackageManager(), getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        urlDialog = new UrlDialog(this, this);
        deleteDialog = new DeleteDialog(this, this);
        paletteDialog = new PaletteDialog(this, this);

        notesAdapter = new NotesAdapter(this, this, this);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(
                App.getInstance().isChangeView() ? 1 : 2, StaggeredGridLayoutManager.VERTICAL);


        mainBinding.rvNotes.setLayoutManager(staggeredGridLayoutManager);
        mainBinding.rvNotes.setAdapter(notesAdapter);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mainBinding.rvNotes);

        MainViewModel viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getNotes().observe(this, notes -> {
            notesFromDB = new ArrayList<>(notes);
            if (notes.size() > 0) {
                mainBinding.lottieEmptyList.setVisibility(View.GONE);
            } else {
                mainBinding.lottieEmptyList.setVisibility(View.VISIBLE);
            }
            sorting();
        });

        if (App.getInstance().isChangeView()) {
            startItemAnimation(this, mainBinding.rvNotes, R.anim.slide_from_bottom_layout);
        } else {
            startItemAnimation(this, mainBinding.rvNotes, R.anim.fall_dawn_layout);
        }

        setSupportActionBar(mainBinding.bottomAppbar);

        startViewAnimation(mainBinding.fab, this, R.anim.fab_bounce);
        mainBinding.fab.setOnClickListener(v -> {
            if (!isClick) {
                DetailActivity.start(MainActivity.this, null);
                overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
                isClick = true;
                throwOff(400);

            }
        });

        multiplyBinding.svSearch.setFocusable(false);
        multiplyBinding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                findNote(newText);
                return true;
            }
        });

        multiplyBinding.ivToolbarClose.setOnClickListener(v -> {
            for (Note note : notesFromDB) {
                note.setSelected(false);
            }
            throwOff(0);
        });

        multiplyBinding.ivMainFavoriteOn.setOnClickListener(v -> {
            for (Note note : notesFromDB) {
                if (note.isSelected()) {
                    if (!note.isFavorite()) {
                        note.setFavorite(true);
                        note.setSelected(false);
                        App.getInstance().getNoteDao().update(note);
                    }
                }
            }
            throwOff(0);
        });

        multiplyBinding.ivMainFavoriteOff.setOnClickListener(v -> {
            for (Note note : notesFromDB) {
                if (note.isSelected()) {
                    if (note.isFavorite()) {
                        note.setFavorite(false);
                        note.setSelected(false);
                        App.getInstance().getNoteDao().update(note);
                    }
                }
            }
            throwOff(0);
        });

        multiplyBinding.ivPaletteDialog.setOnClickListener(v -> paletteDialog.createPaletteDialog());

        multiplyBinding.ivSelectedAll.setOnClickListener(v -> {
            if (!isSelectedAll) {
                isSelectedAll = true;
                for (Note note : notesFromDB) {
                    if (!note.isSelected()) {
                        note.setSelected(true);
                        countSelected++;
                    }
                }
                if (multiplyBinding.ivToolbarDelete.getVisibility() == View.GONE) {
                    multiplyBinding.ivToolbarDelete.setVisibility(View.VISIBLE);
                    startViewAnimation(multiplyBinding.ivToolbarDelete, this, R.anim.appearance);
                }
            } else {
                isSelectedAll = false;
                for (Note note : notesFromDB) {
                    if (note.isSelected()) {
                        note.setSelected(false);
                    }
                }
                countSelected = 0;
                multiplyBinding.ivToolbarDelete.setVisibility(View.GONE);
                startViewAnimation(multiplyBinding.ivToolbarDelete, this, R.anim.disappearance);
            }
            multiplyBinding.tvToolbarCount.setText(String.valueOf(countSelected));
            notesAdapter.notifyDataSetChanged();
        });


        mainBinding.rvNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy < 1) {
                    mainBinding.ivScrollToTop.setVisibility(View.GONE);
                    if (isAnim) {
                        startViewAnimation(mainBinding.ivScrollToTop, getApplicationContext(), R.anim.disappearance);
                        isAnim = false;
                    }

                } else if (dy > 1) {
                    mainBinding.ivScrollToTop.setVisibility(View.VISIBLE);
                    if (!isAnim) {
                        startViewAnimation(mainBinding.ivScrollToTop, getApplicationContext(), R.anim.slide_right);
                        isAnim = true;
                    }
                }
            }
        });

        mainBinding.ivScrollToTop.setOnClickListener(view -> {
            mainBinding.rvNotes.smoothScrollToPosition(0);
            mainBinding.bottomAppbar.performShow();
        });


        multiplyBinding.ivToolbarDelete.setOnClickListener(v -> deleteDialog.createDeleteAllSelectedNotesDialog());

        mainBinding.bottomAppbar.setNavigationOnClickListener(v -> {
            MainBottomSheetFragment blankFragment = new MainBottomSheetFragment();
            blankFragment.show(getSupportFragmentManager(), blankFragment.getTag());
        });

    }

    private void findNote(String keyword) {
        if (!keyword.trim().isEmpty()) {
            isFind = true;
            ArrayList<Note> temp = new ArrayList<>();
            for (Note note : notesFromDB) {
                note.setSelected(false);
                if (note.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || (note.getWebLink() != null && note.getWebLink().toLowerCase().contains(keyword.toLowerCase()))
                        || note.getText().toLowerCase().contains(keyword.toLowerCase())
                        || note.getDateTime().toLowerCase().contains(keyword.toLowerCase())) {
                    temp.add(note);
                }
            }
            notesAdapter.setItems(temp);
            if (temp.size() == 0 && mainBinding.lottieEmptyList.getVisibility() == View.GONE) {
                mainBinding.lottieEmptySearch.setVisibility(View.VISIBLE);
            } else {
                mainBinding.lottieEmptySearch.setVisibility(View.GONE);
            }
        } else {
            mainBinding.lottieEmptySearch.setVisibility(View.GONE);
            sorting();
            isFind = false;
        }
    }

    private void sorting() {
        if (App.getInstance().isIncludeDone()) {
            ArrayList<Note> notes = new ArrayList<>();
            for (Note note : notesFromDB) {
                if (!note.isDone()) {
                    if (!App.getInstance().getSelectedColor().equals(All_COLORS) && App.getInstance().isIncludePicture()) {
                        if (note.getColor().equals(App.getInstance().getSelectedColor()) && !note.getImagePath().trim().isEmpty()) {
                            notes.add(note);
                        }
                    } else if (!App.getInstance().getSelectedColor().equals(All_COLORS) && !App.getInstance().isIncludePicture()) {
                        if (note.getColor().equals(App.getInstance().getSelectedColor())) {
                            notes.add(note);
                        }
                    } else if (App.getInstance().isIncludePicture() && App.getInstance().getSelectedColor().equals(All_COLORS)) {
                        if (!note.getImagePath().trim().isEmpty()) {
                            notes.add(note);
                        }
                    } else {
                        notes.add(note);
                    }
                }
            }
            notesAdapter.setItems(notes);
        } else if (!App.getInstance().getSelectedColor().equals(All_COLORS)) {
            ArrayList<Note> notes = new ArrayList<>();
            for (Note note : notesFromDB) {
                if (note.getColor().equals(App.getInstance().getSelectedColor())) {
                    if (App.getInstance().isIncludePicture()) {
                        if (!note.getImagePath().trim().isEmpty()) {
                            notes.add(note);
                        }
                    } else {
                        notes.add(note);
                    }
                }
            }
            notesAdapter.setItems(notes);
        } else if (App.getInstance().isIncludePicture()) {
            ArrayList<Note> notes = new ArrayList<>();
            for (Note note : notesFromDB) {
                if (!note.getImagePath().trim().isEmpty()) {
                    notes.add(note);
                }
            }
            notesAdapter.setItems(notes);
        } else {
            notesAdapter.setItems(notesFromDB);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appbar_add_image:
                imagePickerDialog.createImagePickerDialog();
                break;
            case R.id.appbar_add_link:
                urlDialog.createMainUrlDialog();
                break;
            case R.id.appbar_add_note_speech:
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault().getLanguage());
                try {
                    getSpeechResult.launch(intent);
                } catch (Exception e) {
                    getToast(this, R.string.speech_message);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                        intent.putExtra(IS_FROM_QUICK_ACTIONS, true);
                        intent.putExtra(QUICK_ACTIONS_TYPE, ACTION_IMAGE);
                        intent.putExtra(IMAGE_PATH, selectedImageUri.toString());
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
                        throwOff(400);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(IS_FROM_QUICK_ACTIONS, true);
            intent.putExtra(QUICK_ACTIONS_TYPE, ACTION_CAMERA);
            intent.putExtra(CAMERA_PATH, imagePickerDialog.getPhotoPath());
            startActivity(intent);
            overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
            throwOff(400);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        multiplyBinding.svSearch.setFocusable(false);
        closeKeyboard();
        isClick = false;
        sorting();
        mainBinding.lottieEmptySearch.setVisibility(View.GONE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        closeKeyboard();
    }

    @Override
    public void onBackPressed() {
        if (isSelect) {
            isSelect = false;
            throwOff(0);
        } else if (isFind) {
            multiplyBinding.svSearch.onActionViewCollapsed();
            multiplyBinding.svSearch.onActionViewExpanded();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    private void throwOff(int i) {
        isSelect = false;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            discharge();
            notesAdapter.notifyDataSetChanged();
        }, i);
    }

    private void closeKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private final ItemTouchHelper.SimpleCallback simpleCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView,
                                      @NonNull RecyclerView.ViewHolder viewHolder,
                                      @NonNull RecyclerView.ViewHolder target) {

                    return false;
                }

                @SuppressLint("ShowToast")
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (isSelect) {
                        isSelect = false;
                        for (Note note : notesFromDB) {
                            note.setSelected(false);
                        }
                        multiplyBinding.clMultiSelectLayout.setVisibility(View.GONE);
                        multiplyBinding.svSearch.setVisibility(View.VISIBLE);
                        startViewAnimation(multiplyBinding.svSearch, getApplicationContext(), R.anim.appearance);
                        multiplyBinding.tvToolbarCount.setText("");
                        countSelected = 0;
                    }
                    Note note = notesAdapter.getNoteAt(viewHolder.getAbsoluteAdapterPosition());
                    note.setSelected(false);
                    int position = viewHolder.getAbsoluteAdapterPosition();
                    switch (direction) {
                        case ItemTouchHelper.LEFT:
                            App.getInstance().getNoteDao().deleteNote(notesAdapter.getNoteAt(position));
                            Snackbar.make(findViewById(R.id.main), getResources().getString(R.string.remove), Snackbar.LENGTH_SHORT)
                                    .setActionTextColor(getColor(R.color.colorSweepDone))
                                    .setTextColor(getColor(R.color.colorPrimaryDay))
                                    .setAnchorView(R.id.fab)
                                    .setBackgroundTint(getColor(R.color.colorAppBarNight))
                                    .setAction(getResources().getString(R.string.to_return), v -> App.getInstance().getNoteDao().insertNote(note)).show();
                            break;
                        case ItemTouchHelper.RIGHT:
                            note.setDone(!note.isDone());
                            App.getInstance().getNoteDao().update(notesAdapter.getNoteAt(position));
                            notesAdapter.notifyItemChanged(position);
                            break;
                    }

                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder, float dX,
                                        float dY, int actionState, boolean isCurrentlyActive) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftActionIcon(R.drawable.ic_delete_sweep)
                            .addSwipeRightActionIcon(R.drawable.ic_rule)
                            .create()
                            .decorate();
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };

    @Override
    public void onNoteClicked(Note note, RelativeLayout noteLayout) {
        if (!isClick) {
            DetailActivity.start(this, note, noteLayout);
            isClick = true;
        }
    }

    @Override
    public void onNoteLongListener(Note note, View selectedView) {
        if (!isSelect) {
            isSelect = true;
            isSelectedAll = false;
            selectedView.setVisibility(View.VISIBLE);
            note.setSelected(true);
            countSelected = 1;
            discharge();
        } else {
            note.setSelected(false);
            selectedView.setVisibility(View.GONE);
            throwOff(0);
        }
    }

    @Override
    public void onNoteSelectListener(Note note, View selectedView) {
        if (note.isSelected()) {
            note.setSelected(false);
            selectedView.setVisibility(View.GONE);
            countSelected--;
        } else {
            selectedView.setVisibility(View.VISIBLE);
            note.setSelected(true);
            countSelected++;
        }
        multiplyBinding.tvToolbarCount.setText(String.valueOf(countSelected));
        if (countSelected == 0) {
            multiplyBinding.ivToolbarDelete.setVisibility(View.GONE);
            startViewAnimation(multiplyBinding.ivToolbarDelete, this, R.anim.disappearance);
        } else {
            multiplyBinding.ivToolbarDelete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LinkPreviewUtil.dispose();
    }

    @Override
    public void dialogDeleteCallback(boolean confirm) {
        if (confirm) {
            for (Note note : notesFromDB) {
                if (note.isSelected()) {
                    App.getInstance().getNoteDao().deleteNote(note);
                }
            }
            isSelect = false;
            discharge();
        }
    }

    @Override
    public void onOnClick(boolean isClick) {
        if (isClick) {
            sorting();
        }
    }

    @Override
    public void onChangeView(boolean isChange) {
        if (isChange) {
            staggeredGridLayoutManager.setSpanCount(1);
        } else {
            staggeredGridLayoutManager.setSpanCount(2);
        }
        notesAdapter.notifyItemRangeChanged(notesFromDB.size(), notesAdapter.getItemCount());
    }

    @Override
    public void onSelectColor(String color) {
        for (Note note : notesFromDB) {
            if (note.isSelected()) {
                note.setSelected(false);
                note.setColor(color);
                App.getInstance().getNoteDao().update(note);
            }
        }
        isSelect = false;
        discharge();
        notesAdapter.notifyDataSetChanged();
    }

    private void discharge() {
        if (isSelect) {
            multiplyBinding.svSearch.setVisibility(View.GONE);
            multiplyBinding.clMultiSelectLayout.setVisibility(View.VISIBLE);
            multiplyBinding.ivToolbarDelete.setVisibility(View.VISIBLE);
            startViewAnimation(multiplyBinding.ivSelectedAll, this, R.anim.appearance);
            startViewAnimation(multiplyBinding.ivMainFavoriteOn, this, R.anim.appearance);
            startViewAnimation(multiplyBinding.ivMainFavoriteOff, this, R.anim.appearance);
            startViewAnimation(multiplyBinding.ivToolbarDelete, this, R.anim.slide_right);
            startViewAnimation(multiplyBinding.ivToolbarClose, this, R.anim.slide_left);
            startViewAnimation(multiplyBinding.tvToolbarCount, this, R.anim.appearance);
            startViewAnimation(multiplyBinding.ivPaletteDialog, this, R.anim.appearance);
            multiplyBinding.tvToolbarCount.setText(String.valueOf(countSelected));
        } else {
            for (Note note : notesFromDB) {
                note.setSelected(false);
            }
            multiplyBinding.clMultiSelectLayout.setVisibility(View.GONE);
            multiplyBinding.svSearch.setVisibility(View.VISIBLE);
            startViewAnimation(multiplyBinding.svSearch, this, R.anim.appearance);
            multiplyBinding.tvToolbarCount.setText("");
            countSelected = 0;
        }
    }

    @Override
    public void onCreateLink(String link) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(IS_FROM_QUICK_ACTIONS, true);
        intent.putExtra(QUICK_ACTIONS_TYPE, ACTION_URL);
        intent.putExtra(ACTION_URL, link);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in, R.anim.activity_static_animation);
        throwOff(0);
    }

    @Override
    public void onClickSettings(boolean isClick) {
        if (isClick) {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.activity_slide_from_bottom, R.anim.activity_slide_to_top);
            throwOff(400);
        }
    }
}