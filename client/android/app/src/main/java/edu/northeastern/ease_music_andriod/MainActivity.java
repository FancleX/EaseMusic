package edu.northeastern.ease_music_andriod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    // ================ fields ================
    private static final int SPLASH_SCREEN_DELAY = 4000;


    // ================ views ================
    private Animation top2BotAnim, bot2TopAnim;
    private ImageView logoImage;
    private TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // add animations
        top2BotAnim = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom_move_animation);
        bot2TopAnim = AnimationUtils.loadAnimation(this, R.anim.botton_to_top_move_animation);

        logoImage = findViewById(R.id.logo_image);
        logo = findViewById(R.id.logo);
        slogan = findViewById(R.id.slogan);

        logoImage.setAnimation(top2BotAnim);
        logo.setAnimation(bot2TopAnim);
        slogan.setAnimation(bot2TopAnim);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_SCREEN_DELAY);
    }
}