package edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class MusicItem implements Parcelable {

    private final String uuid;
    private final String title;
    private final String author;
    private final String description;
    private final String thumbnail;


    public MusicItem(String uuid, String title, String author, String description, String thumbnail) {
        this.uuid = uuid;
        this.title = title;
        this.author = author;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    protected MusicItem(Parcel in) {
        uuid = in.readString();
        title = in.readString();
        author = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<MusicItem> CREATOR = new Creator<MusicItem>() {
        @Override
        public MusicItem createFromParcel(Parcel in) {
            return new MusicItem(in);
        }

        @Override
        public MusicItem[] newArray(int size) {
            return new MusicItem[size];
        }
    };

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public String toString() {
        return "MusicItem{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uuid);
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(description);
        parcel.writeString(thumbnail);
    }
}
