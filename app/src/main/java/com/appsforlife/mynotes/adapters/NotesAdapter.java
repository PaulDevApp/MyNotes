package com.appsforlife.mynotes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.appsforlife.mynotes.App;
import com.appsforlife.mynotes.R;
import com.appsforlife.mynotes.entities.Note;
import com.appsforlife.mynotes.listeners.NoteListener;
import com.appsforlife.mynotes.listeners.NoteLongListener;
import com.appsforlife.mynotes.listeners.NoteSelectListener;
import com.bumptech.glide.Glide;

import java.util.List;

import io.github.ponnamkarthik.richlinkpreview.RichLinkViewTwitter;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

import static com.appsforlife.mynotes.Support.*;
import static com.appsforlife.mynotes.constants.Constants.*;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final SortedList<Note> sortedList;
    private final NoteListener noteListener;
    private final NoteLongListener noteLongListener;
    private final NoteSelectListener noteSelectListener;
    private final Context context;

    public NotesAdapter(NoteListener noteListener, NoteLongListener noteLongListener,
                        NoteSelectListener noteSelectListener, Context context) {
        this.noteListener = noteListener;
        this.noteLongListener = noteLongListener;
        this.noteSelectListener = noteSelectListener;
        this.context = context;
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
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_note, parent, false));
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

        TextView title, text, dateTime, webLink;
        RelativeLayout relativeLayoutNote;
        RichLinkViewTwitter richLinkView;
        ImageView checkImage, checkLink, imageViewFavorite, itemImageView;
        Note note;
        FrameLayout itemBackground;
        View selectedView;
        View doneView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_item_text);
            title = itemView.findViewById(R.id.tv_item_text_title);
            dateTime = itemView.findViewById(R.id.tv_item_date_time);
            relativeLayoutNote = itemView.findViewById(R.id.rl_note);
            richLinkView = itemView.findViewById(R.id.linkPreviewItem);
            webLink = itemView.findViewById(R.id.tv_web_item);
            checkImage = itemView.findViewById(R.id.iv_check_image);
            checkLink = itemView.findViewById(R.id.iv_check_link);
            itemBackground = itemView.findViewById(R.id.item_background);
            selectedView = itemView.findViewById(R.id.v_color_selected);
            imageViewFavorite = itemView.findViewById(R.id.iv_item_favorite);
            itemImageView = itemView.findViewById(R.id.iv_item_image);
            doneView = itemView.findViewById(R.id.v_color_done);


            itemView.setOnClickListener(v -> {
                if (isSelect) {
                    noteSelectListener.onNoteSelectListener(sortedList.get(getAbsoluteAdapterPosition()), selectedView);
                } else {
                    noteListener.onNoteClicked(sortedList.get(getAbsoluteAdapterPosition()), relativeLayoutNote);
                }
            });

            itemView.setOnLongClickListener(v -> {
                noteLongListener.onNoteLongListener(sortedList.get(getAbsoluteAdapterPosition()), selectedView);
                return true;
            });

        }

        @SuppressLint("ResourceAsColor")
        void setNote(Note note) {
            this.note = note;

            if (note.isSelected()) {
                selectedView.setVisibility(View.VISIBLE);
            } else {
                selectedView.setVisibility(View.GONE);
            }

            if (note.isDone()){
                doneView.setVisibility(View.VISIBLE);
            }else {
                doneView.setVisibility(View.GONE);
            }

            if (note.isFavorite()) {
                imageViewFavorite.setVisibility(View.VISIBLE);
            } else {
                imageViewFavorite.setVisibility(View.GONE);
            }

            if (!note.getTitle().trim().isEmpty()) {
                title.setVisibility(View.VISIBLE);
                title.setText(note.getTitle());
            } else {
                title.setVisibility(View.GONE);
            }

            if (!note.getText().trim().isEmpty()) {
                text.setVisibility(View.VISIBLE);
                text.setText(note.getText());
            } else {
                text.setVisibility(View.GONE);
            }

            updateStrokeOut(note, title, text);

            dateTime.setText(note.getDateTime());
            webLink.setText(note.getWebLink());

            if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty() && !App.getInstance().isVisible()) {
                Glide.with(context).load(note.getImagePath()).into(itemImageView);
                itemImageView.setVisibility(View.VISIBLE);
            } else {
                itemImageView.setVisibility(View.GONE);
            }

            if (note.getImagePath() != null && !note.getImagePath().trim().isEmpty()) {
                checkImage.setVisibility(View.VISIBLE);
            } else {
                checkImage.setVisibility(View.GONE);
            }

            if (webLink.getText().toString().trim().isEmpty()) {
                webLink.setVisibility(View.GONE);
                checkLink.setVisibility(View.GONE);
                richLinkView.setVisibility(View.GONE);
            } else {
                webLink.setVisibility(View.VISIBLE);
                checkLink.setVisibility(View.VISIBLE);
                richLinkView.setVisibility(View.GONE);
                if (!webLink.getText().toString().startsWith("www") && !App.getInstance().isPreview()) {
                    richLinkView.setVisibility(View.VISIBLE);
                    richLinkView.setLink(note.getWebLink(), new ViewListener() {
                        @Override
                        public void onSuccess(boolean status) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                }
            }

            GradientDrawable gradientDrawable = (GradientDrawable) itemBackground.getBackground();
            if (note.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor(COLOR_DEFAULT));
            }
        }
    }
}
