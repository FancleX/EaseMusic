package edu.northeastern.ease_music_andriod.recyclerViewComponents.userFavoriteItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem.MusicItem;

public class FavoriteItemViewHolder extends RecyclerView.ViewHolder {

    private final ImageView thumbnail;
    private final TextView title;
    private final TextView author;
    private final TextView description;

    public FavoriteItemViewHolder(@NonNull View itemView) {
        super(itemView);

        thumbnail = itemView.findViewById(R.id.music_thumbnail);
        title = itemView.findViewById(R.id.music_title);
        author = itemView.findViewById(R.id.music_author);
        description = itemView.findViewById(R.id.music_description);
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
    }
}
