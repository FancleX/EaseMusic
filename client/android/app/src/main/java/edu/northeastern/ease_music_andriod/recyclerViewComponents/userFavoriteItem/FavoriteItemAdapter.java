package edu.northeastern.ease_music_andriod.recyclerViewComponents.userFavoriteItem;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem.MusicItem;
import edu.northeastern.ease_music_andriod.utils.DataCache;

public class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemViewHolder> {

    private ArrayList<MusicItem> favoriteList;

    @NonNull
    @Override
    public FavoriteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavoriteItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_profile_recyler_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteItemViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);
        holder.bindData(favoriteList.get(position), position);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        favoriteList = DataCache.getInstance().getUserCache().getFavorites();
        notifyDataSetChanged();
    }
}
