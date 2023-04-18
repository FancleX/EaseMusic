package edu.northeastern.ease_music_andriod.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import edu.northeastern.ease_music_andriod.fragments.LoginFragment;
import edu.northeastern.ease_music_andriod.fragments.MusicFragment;
import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.fragments.SearchFragment;
import edu.northeastern.ease_music_andriod.fragments.TitleFragment;
import edu.northeastern.ease_music_andriod.fragments.UserProfileFragment;
import edu.northeastern.ease_music_andriod.utils.DataCache;
import edu.northeastern.ease_music_andriod.utils.MusicPlayer;
import edu.northeastern.ease_music_andriod.utils.RequestAPIs;
import edu.northeastern.ease_music_andriod.utils.UserService;

public class DashBoardActivity extends AppCompatActivity implements MusicPlayer.CallbackActivity {

    // ================ fields ================
    private static final String TAG = "DashBoard Activity";
    private final DataCache dataCache = DataCache.getInstance();

    // ================ views ================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        musicPlayer.attachCallbackActivity(this);
        musicPlayer.attachRootContext(getApplicationContext());
        musicPlayer.attachOnDownloadCompletedCallback(new MusicPlayer.OnDownloadCompletedCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Successfully Downloaded", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to download: " + error, Toast.LENGTH_SHORT).show());
            }
        });

        UserService.getInstance().setOnRequestResultListener(new UserService.OnRequestResultListener() {
            @Override
            public void onSuccess(String message, RequestAPIs.APILabel label) {
                runOnUiThread(() ->
                        {
                            Toast.makeText(
                                    DashBoardActivity.this,
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();

                            if (label.equals(RequestAPIs.APILabel.SIGNIN)) {
                                replaceFragment(new UserProfileFragment(), false);
                            }
                        }
                );
            }

            @Override
            public void onError(String error, RequestAPIs.APILabel label) {
                runOnUiThread(() ->
                        Toast.makeText(
                        DashBoardActivity.this,
                        error,
                        Toast.LENGTH_SHORT
                ).show());
            }
        });

        // register top panel
        replaceTopPanelFragment(new TitleFragment());

        // register bottom nav
        replaceFragment(new SearchFragment(this), true);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        MenuItem menuItem = bottomNav.getMenu().findItem(R.id.music);
        menuItem.setEnabled(false);

        bottomNav.setOnItemSelectedListener(item -> {
            final int id = item.getItemId();

            if (id == R.id.search)
                replaceFragment(new SearchFragment(this), false);
            else if (id == R.id.music) {
                if (MusicPlayer.getInstance().getMusicUuid() != null) {
                    replaceFragment(new MusicFragment(), false);
                }
            } else if (id == R.id.home)
                if (!dataCache.getUserCache().isUserLogin())
                    replaceFragment(new LoginFragment(), false);
                else
                    replaceFragment(new UserProfileFragment(), false);

            return true;
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry lastEntry = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.getBackStackEntryCount() - 1);
            String name = lastEntry.getName();

            BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

            if ("SearchFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.search);
                item.setChecked(true);
            } else if ("MusicFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.music);
                item.setChecked(true);
            } else if ("LoginFragment".equals(name) || "SignUpFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.home);
                item.setChecked(true);
            }

        } else {
            BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

            MenuItem item = navigationView.getMenu().findItem(R.id.search);
            item.setChecked(true);
        }
    }

    private void replaceFragment(Fragment fragment, boolean isDefault) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame_layout, fragment);

        if (!isDefault)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    private void replaceTopPanelFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.top_view_panel, fragment);
        fragmentTransaction.commit();
    }

    private String getFragmentLabel(Fragment fragment) {
        if (fragment instanceof SearchFragment) {
            return "SearchFragment";
        } else if (fragment instanceof MusicFragment) {
            return "MusicFragment";
        } else if (fragment instanceof LoginFragment) {
            return "LoginFragment";
        }

        return null;
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }
}