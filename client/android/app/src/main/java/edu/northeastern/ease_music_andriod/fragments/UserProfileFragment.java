package edu.northeastern.ease_music_andriod.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Objects;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.musicItem.MusicItemAdapter;
import edu.northeastern.ease_music_andriod.recyclerViewComponents.userFavoriteItem.FavoriteItemAdapter;
import edu.northeastern.ease_music_andriod.utils.DataCache;

public class UserProfileFragment extends Fragment {

    // ================ fields ================
    private final DataCache dataCache = DataCache.getInstance();
    private static final String AUDIO_DIR = "AUDIOS";
    private FavoriteItemAdapter favoriteItemAdapter;

    // ================ views ================
    private Button signOutButton;
    private ImageView profileImage;
    private MaterialCardView cardView;
    private TextView email, favoriteCount, downloadCount;
    private RecyclerView favoriteRecycler;
    private Animation top2BotAnim, bot2TopAnim;
    private RelativeLayout headerLayout, bottomLayout;
    private LinearLayout cardsLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_user_profile, container, false);

        signOutButton = root.findViewById(R.id.sign_out);
        profileImage = root.findViewById(R.id.profile_image);
        cardView = root.findViewById(R.id.favorite_card);
        email = root.findViewById(R.id.user_profile_email);
        favoriteCount = root.findViewById(R.id.favorite_count);
        downloadCount = root.findViewById(R.id.download_count);
        favoriteRecycler = root.findViewById(R.id.user_profile_recycler);
        headerLayout = root.findViewById(R.id.user_profile_header_layout);
        bottomLayout = root.findViewById(R.id.user_profile_bottom);
        cardsLayout = root.findViewById(R.id.user_profile_cards_layout);

        signOutButton.setOnClickListener(v -> switchToLoginPage());
        email.setText(dataCache.getUserCache().getUsername());
        favoriteCount.setText(String.valueOf(dataCache.getUserCache().getFavorites().size()));
        downloadCount.setText(String.valueOf(getDownloadCount()));

        // add animations
        top2BotAnim = AnimationUtils.loadAnimation(getContext(), R.anim.top_to_bottom_move_animation);
        bot2TopAnim = AnimationUtils.loadAnimation(getContext(), R.anim.botton_to_top_move_animation);

        headerLayout.setAnimation(top2BotAnim);
        cardsLayout.setAnimation(bot2TopAnim);
        bottomLayout.setAnimation(bot2TopAnim);

        favoriteRecycler.setVisibility(View.GONE);
        favoriteRecycler.setHasFixedSize(true);
        favoriteItemAdapter = new FavoriteItemAdapter();
        favoriteRecycler.setAdapter(favoriteItemAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        favoriteRecycler.setLayoutManager(layoutManager);
        favoriteItemAdapter.updateData();

        cardView.setOnClickListener(v -> favoriteRecycler.setVisibility(View.VISIBLE));
        return root;
    }

    private int getDownloadCount() {
        File dir = new File(requireContext().getExternalFilesDir(null), AUDIO_DIR);
        File[] files = dir.listFiles((file, name) -> name.endsWith(".mp3"));

        if (files != null)
            return files.length;

        return 0;
    }



    private void switchToLoginPage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout, new LoginFragment());
        fragmentTransaction.commit();

        dataCache.getUserCache().clearUserCache();
    }
}