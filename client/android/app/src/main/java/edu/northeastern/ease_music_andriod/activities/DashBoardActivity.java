package edu.northeastern.ease_music_andriod.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.ease_music_andriod.fragments.HomeFragment;
import edu.northeastern.ease_music_andriod.fragments.LoginFragment;
import edu.northeastern.ease_music_andriod.fragments.MiniPlayerFragment;
import edu.northeastern.ease_music_andriod.fragments.MusicFragment;
import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.fragments.SearchFragment;
import edu.northeastern.ease_music_andriod.fragments.TitleFragment;

public class DashBoardActivity extends AppCompatActivity {

    // ================ fields ================

    // ================ views ================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        // register top panel
        replaceTopPanelFragment(new TitleFragment());

        // register bottom nav
        replaceFragment(new SearchFragment());
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            final int id = item.getItemId();

            if (id == R.id.search)
                replaceFragment(new SearchFragment());
            else if (id == R.id.music)
                replaceFragment(new MusicFragment());
            else if (id == R.id.login)
                replaceFragment(new LoginFragment());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void replaceTopPanelFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.top_view_panel, fragment);
        fragmentTransaction.commit();
    }
}