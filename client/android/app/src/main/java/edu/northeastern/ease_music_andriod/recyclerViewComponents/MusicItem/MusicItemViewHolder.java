package edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

import edu.northeastern.ease_music_andriod.R;

public class MusicItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView author;
    private final TextView description;

    public MusicItemViewHolder(@NonNull View itemView, OnMusicItemClickListener listener) {
        super(itemView);

        thumbnail = itemView.findViewById(R.id.music_thumbnail);
        title = itemView.findViewById(R.id.music_title);
        author = itemView.findViewById(R.id.music_author);
        description = itemView.findViewById(R.id.music_description);

        // layout on click
        LinearLayout layout = itemView.findViewById(R.id.music_text_layout);
        layout.setOnClickListener(view -> listener.onTextClick(view, getAdapterPosition()));

        // toggle browser
        ImageView browserIcon = itemView.findViewById(R.id.toggle_browser);
        browserIcon.setOnClickListener(view -> listener.onBrowserIconClick(view, getAdapterPosition()));
    }

    public void bindData(MusicItem item) {
        Picasso.get()
                .load(item.getThumbnail())
                .resize(50, 50)
                .centerCrop()
                .into(thumbnail);

        title.setText(item.getTitle());
        author.setText(item.getAuthor());
        description.setText(item.getDescription());
    }

    public interface OnMusicItemClickListener {
        void onTextClick(View view, int position);

        void onBrowserIconClick(View view, int position);
    }

}
