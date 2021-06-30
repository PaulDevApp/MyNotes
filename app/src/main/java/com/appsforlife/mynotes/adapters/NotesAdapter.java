package com.appsforlife.mynotes.adapters;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.databinding.ItemNoteBinding;
import com.appsforlife.mynotes.databinding.LayoutLinkPreviewBinding;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.listeners.NoteListener;
import com.appsforlife.mynotes.listeners.NoteLongListener;
import com.appsforlife.mynotes.listeners.NoteSelectListener;
import com.bumptech.glide.Glide;
import java.util.List;

import static com.appsforlife.mynotes.util.LinkPreviewUtil.setPreviewLink;
import static com.appsforlife.mynotes.Support.*;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final SortedList<Note> sortedList;
    private final NoteListener noteListener;
    private final NoteLongListener noteLongListener;
    private final NoteSelectListener noteSelectListener;

    public NotesAdapter(NoteListener noteListener, NoteLongListener noteLongListener,
                        NoteSelectListener noteSelectListener) {
        this.noteListener = noteListener;
        this.noteLongListener = noteLongListener;
        this.noteSelectListener = noteSelectListener;
        sortedList = new SortedList<>(Note.class, new SortedList.Callback<Note>() {
            @Override
            public int compare(Note o1, Note o2) {
                if (!o2.isFavorite() && o1.isFavorite()) {
                    return -1;
                }
                if (o2.isFavorite() && !o1.isFavorite()) {
                    return 1;
                }
                if (!o2.isDone() && o1.isDone()) {
                    return 1;
                }
                if (o2.isDone() && !o1.isDone()) {
                    return -1;
                }
                return (o2.getId() - o1.getId());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Note oldItem, Note newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Note item1, Note item2) {
                return item1.getId() == item2.getId();
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemNoteBinding itemNoteBinding = ItemNoteBinding.inflate(layoutInflater, parent, false);
        return new NoteViewHolder(itemNoteBinding);
    }

    public Note getNoteAt(int position) {
        return sortedList.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(sortedList.get(position));

    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void setItems(List<Note> notes) {
        sortedList.replaceAll(notes);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        Note note;
        ItemNoteBinding itemNoteBinding;
        LayoutLinkPreviewBinding previewBinding;

        NoteViewHolder(ItemNoteBinding itemNoteBinding) {
            super(itemNoteBinding.getRoot());
            this.itemNoteBinding = itemNoteBinding;
            previewBinding = itemNoteBinding.previewLink;

            itemView.setOnClickListener(v -> {
                if (isSelect) {
                    noteSelectListener.onNoteSelectListener(sortedList.get(getAbsoluteAdapterPosition()),
                            itemNoteBinding.vColorSelected);
                } else {
                    noteListener.onNoteClicked(sortedList.get(getAbsoluteAdapterPosition()), itemNoteBinding.rlNote);
                }
            });

            itemView.setOnLongClickListener(v -> {
                noteLongListener.onNoteLongListener(sortedList.get(getAbsoluteAdapterPosition()), itemNoteBinding.vColorSelected);
                return true;
            });

        }

        @SuppressLint("ResourceAsColor")
        void setNote(Note note) {
            this.note = note;

            itemNoteBinding.tvItemText.setMaxLines(App.getInstance().getCountLines());

            if (note.isSelected()) {
                itemNoteBinding.vColorSelected.setVisibility(View.VISIBLE);
            } else {
                itemNoteBinding.vColorSelected.setVisibility(View.GONE);
            }

            if (note.isDone()) {
                itemNoteBinding.vColorDone.setVisibility(View.VISIBLE);
            } else {
                itemNoteBinding.vColorDone.setVisibility(View.GONE);
            }

            if (note.isFavorite()) {
                itemNoteBinding.ivItemFavorite.setVisibility(View.VISIBLE);
            } else {
                itemNoteBinding.ivItemFavorite.setVisibility(View.GONE);
            }

            if (!note.getTitle().trim().isEmpty()) {
                itemNoteBinding.tvItemTextTitle.setVisibility(View.VISIBLE);
                itemNoteBinding.tvItemTextTitle.setText(note.getTitle());
            } else {
                itemNoteBinding.tvItemTextTitle.setVisibility(View.GONE);
            }

            if (!note.getText().trim().isEmpty()) {
                itemNoteBinding.tvItemText.setVisibility(View.VISIBLE);
                itemNoteBinding.tvItemText.setText(note.getText());
            } else {
                itemNoteBinding.tvItemText.setVisibility(View.GONE);
            }

            updateStrokeOut(note, itemNoteBinding.tvItemTextTitle, itemNoteBinding.tvItemText);

            itemNoteBinding.tvItemDateTime.setText(note.getDateTime());

            if (TextUtils.isEmpty(note.getImagePath())) {
                itemNoteBinding.ivCheckImage.setVisibility(View.GONE);
                itemNoteBinding.ivItemImage.setVisibility(View.GONE);
            } else {
                itemNoteBinding.ivCheckImage.setVisibility(View.VISIBLE);
                if (!App.getInstance().isVisible()) {
                    itemNoteBinding.ivItemImage.setVisibility(View.VISIBLE);
                    Glide.with(itemNoteBinding.ivItemImage.getContext()).load(note.getImagePath()).into(itemNoteBinding.ivItemImage);
                }
            }

            if (TextUtils.isEmpty(note.getWebLink())) {
                itemNoteBinding.ivCheckLink.setVisibility(View.GONE);
                previewBinding.clPreviewLink.setVisibility(View.GONE);
            } else {
                if (!App.getInstance().isPreview()) {
                    setPreviewLink(note.getWebLink(), previewBinding.ivSiteImage,
                            previewBinding.tvSiteName, previewBinding.tvSiteDescription);
                }
                itemNoteBinding.ivCheckLink.setVisibility(View.VISIBLE);
                previewBinding.tvSiteUrl.setVisibility(View.VISIBLE);
                previewBinding.clPreviewLink.setVisibility(View.VISIBLE);
                previewBinding.tvSiteUrl.setText(note.getWebLink());
            }


            setBackgroundNoteColor(itemNoteBinding.itemBackground, note.getColor());
        }
    }
}
