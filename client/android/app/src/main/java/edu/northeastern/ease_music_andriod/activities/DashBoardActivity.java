package edu.northeastern.ease_music_andriod.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.northeastern.ease_music_andriod.fragments.LoginFragment;
import edu.northeastern.ease_music_andriod.fragments.MusicFragment;
import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.fragments.SearchFragment;
import edu.northeastern.ease_music_andriod.fragments.TitleFragment;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;

public class DashBoardActivity extends AppCompatActivity implements MusicPlayer.CallbackActivity {

    // ================ fields ================

    // ================ views ================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        musicPlayer.attachCallbackActivity(this);
        musicPlayer.attachRootContext(getApplicationContext());

        // register top panel
        replaceTopPanelFragment(new TitleFragment());

        // register bottom nav
        replaceFragment(new SearchFragment(this));
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        MenuItem menuItem = bottomNav.getMenu().findItem(R.id.music);
        menuItem.setEnabled(false);

        bottomNav.setOnItemSelectedListener(item -> {
            final int id = item.getItemId();

            if (id == R.id.search)
                replaceFragment(new SearchFragment(this));
            else if (id == R.id.music) {
                if (MusicPlayer.getInstance().getMusicUuid() != null) {
                    replaceFragment(new MusicFragment());
                }
            } else if (id == R.id.login)
                replaceFragment(new LoginFragment());

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void replaceTopPanelFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.top_view_panel, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }
}