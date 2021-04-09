package com.appsforlife.mynotes.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "notes")
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "date_time_edited")
    private String dateTimeEdited;

    @ColumnInfo(name = "text")
    private String text;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "web_link")
    private String webLink;

    @ColumnInfo(name = "done")
    private boolean done;

    @ColumnInfo(name = "selected")
    private boolean selected = false;

    @ColumnInfo(name = "favorite")
    private boolean favorite;

    public Note() {
    }


    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        dateTime = in.readString();
        dateTimeEdited = in.readString();
        text = in.readString();
        imagePath = in.readString();
        color = in.readString();
        webLink = in.readString();
        done = in.readByte() != 0;
        selected = in.readByte() != 0;
        favorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(dateTime);
        dest.writeString(dateTimeEdited);
        dest.writeString(text);
        dest.writeString(imagePath);
        dest.writeString(color);
        dest.writeString(webLink);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (favorite ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id &&
                done == note.done &&
                selected == note.selected &&
                favorite == note.favorite &&
                Objects.equals(title, note.title) &&
                Objects.equals(dateTime, note.dateTime) &&
                Objects.equals(dateTimeEdited, note.dateTimeEdited) &&
                Objects.equals(text, note.text) &&
                Objects.equals(imagePath, note.imagePath) &&
                Objects.equals(color, note.color) &&
                Objects.equals(webLink, note.webLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, dateTime, dateTimeEdited, text, imagePath, color, webLink, done, selected, favorite);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTimeEdited() {
        return dateTimeEdited;
    }

    public void setDateTimeEdited(String dateTimeEdited) {
        this.dateTimeEdited = dateTimeEdited;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
