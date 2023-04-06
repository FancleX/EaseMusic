package edu.northeastern.ease_music_andriod.recyclerViewComponents.MusicItem;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;

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

    public void bindData(MusicItem item, int position) {
        Picasso.get()
                .load(item.getThumbnail())
                .resize(50, 50)
                .centerCrop()
                .into(thumbnail);

        title.setText(item.getTitle());
        author.setText(item.getAuthor());
        description.setText(item.getDescription());
        title.setTextColor(Color.parseColor("#595d63"));

        MusicPlayer instance = MusicPlayer.getInstance();
        if (position == instance.getMusicIndex() && item.getUuid().equals(instance.getMusicUuid())) {
            title.setTextColor(Color.parseColor("#39C5BB"));
        }

    }

    public interface OnMusicItemClickListener {
        void onTextClick(View view, int position);

        void onBrowserIconClick(View view, int position);
    }

}
