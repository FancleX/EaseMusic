package edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem;

public class MusicItem {

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
}
