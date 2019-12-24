package com.example.simnetworkanalyser.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.TransitionManager;

import com.example.simnetworkanalyser.R;
import com.example.simnetworkanalyser.ui.main.MainActivity;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ViewGroup logos = findViewById(R.id.ll_splash);

        final ImageView g3 = findViewById(R.id.img_3g);
        final ImageView g4 = findViewById(R.id.img_4g);
        final TextView appName = findViewById(R.id.txt_app_name);

        final Animation slideRightAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        final Animation crossFadeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.cross_fade);
        final Animation slideLeftAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left);

        g3.startAnimation(slideRightAnimation);
        g4.startAnimation(slideLeftAnimation);

        slideLeftAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                TransitionManager.beginDelayedTransition(logos);

                appName.setVisibility(View.VISIBLE);
                appName.startAnimation(crossFadeAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        crossFadeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
}
