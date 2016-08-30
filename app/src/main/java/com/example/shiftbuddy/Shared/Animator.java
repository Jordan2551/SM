package com.example.shiftbuddy.Shared;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.shiftbuddy.MainActivity;
import com.example.shiftbuddy.R;

/**
 * Created by jorda_000 on 8/17/2016.
 */
public class Animator {

    public static void fade(View view, boolean fadeIn) {

        Animation animation = null;

        if (fadeIn)
            animation = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.fadein);

        else
            animation = AnimationUtils.loadAnimation(MainActivity.getContext(), R.anim.fadeout);

        view.startAnimation(animation);

    }

}
