package it_geeks.info.elgawla.util.Floating;

import android.view.View;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

class Dynamic {

    private View view;
    private SpringForce springForce;
    SpringAnimation springAnimationX, springAnimationY;
    FlingAnimation flingAnimationX, flingAnimationY;

    Dynamic(View view) {
        this.view = view;
        springForce = new SpringForce(0f)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM)
                .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);

        spring();
        fling();
    }

    private void spring() {
        springAnimationX = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X).setSpring(springForce);
        springAnimationY = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y).setSpring(springForce);

    }

    private void fling() {
        flingAnimationX = new FlingAnimation(view, DynamicAnimation.X).setFriction(1.1f);
        flingAnimationY = new FlingAnimation(view, DynamicAnimation.Y).setFriction(1.1f);
    }
}
