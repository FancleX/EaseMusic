package edu.northeastern.ease_music_andriod.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Date;

import edu.northeastern.ease_music_andriod.R;
import edu.northeastern.ease_music_andriod.fragments.LoginFragment;
import edu.northeastern.ease_music_andriod.fragments.MiniPlayerFragment;
import edu.northeastern.ease_music_andriod.fragments.MusicFragment;
import edu.northeastern.ease_music_andriod.fragments.SearchFragment;
import edu.northeastern.ease_music_andriod.fragments.TitleFragment;
import edu.northeastern.ease_music_andriod.fragments.UserProfileFragment;
import edu.northeastern.ease_music_andriod.utils.DBHandler;
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

                            if (label.equals(RequestAPIs.APILabel.SIGNIN) || label.equals(RequestAPIs.APILabel.SIGNUP)) {
                                try (DBHandler dbHandler = new DBHandler(getApplicationContext())) {
                                    dbHandler.insertOrUpdateUserProfile(dataCache.getUserCache().getUsername(), null, new Date(System.currentTimeMillis()));
                                    replaceFragment(new UserProfileFragment(), true);
                                }
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
        if (!musicPlayer.isReady())
            replaceTopPanelFragment(new TitleFragment());
        else
            replaceTopPanelFragment(new MiniPlayerFragment());

        // register bottom nav
        replaceFragment(new SearchFragment(this), false);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            final int id = item.getItemId();

            if (id == R.id.search) {
                if (bottomNav.getMenu().findItem(R.id.search).isChecked())
                    return false;

                replaceFragment(new SearchFragment(this), true);
            } else if (id == R.id.music) {
                if (MusicPlayer.getInstance().isReady()) {
                    if (bottomNav.getMenu().findItem(R.id.music).isChecked())
                        return false;

                    replaceFragment(new MusicFragment(), true);
                } else {
                    Toast.makeText(this, "Please select a music first", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (id == R.id.home) {
                if (bottomNav.getMenu().findItem(R.id.home).isChecked())
                    return false;

                if (!dataCache.getUserCache().isUserLogin())
                    replaceFragment(new LoginFragment(), true);
                else
                    replaceFragment(new UserProfileFragment(), true);
            }

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

            Log.i(TAG, "fragment name: " + name);

            if ("SearchFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.search);
                item.setChecked(true);
            } else if ("MusicFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.music);
                item.setChecked(true);
            } else if ("LoginFragment".equals(name) || "SignUpFragment".equals(name) || "UserProfileFragment".equals(name)) {
                MenuItem item = navigationView.getMenu().findItem(R.id.home);
                item.setChecked(true);
            }

        } else {
            BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);

            MenuItem item = navigationView.getMenu().findItem(R.id.search);
            item.setChecked(true);
        }
    }

    private void replaceFragment(Fragment fragment, boolean addToStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame_layout, fragment);

        if (addToStack)
            fragmentTransaction.addToBackStack(getFragmentLabel(fragment));

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
        } else if (fragment instanceof UserProfileFragment) {
            return "UserProfileFragment";
        }

        return null;
    }

    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }
}