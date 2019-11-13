package it_geeks.info.elgawla.util.Floating;

import android.app.Activity;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.dynamicanimation.animation.DynamicAnimation;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;

public class FloatingView {

    private View view;
    private Activity activity;
    private GestureDetector gestureDetector;
    private Dynamic dynamic;
    private int screenHeight, screenWidth;

    public FloatingView(View view, Activity activity) {
        this.view = view;
        this.activity = activity;
        dynamic = new Dynamic(view);
    }

    public void initViewListeners(final ClickInterface.SnackAction action) {
        getScreenDimensions();

        DynamicAnimation.OnAnimationEndListener endListener = new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                handleWithScreenBorders(view);
            }
        };

        dynamic.flingAnimationX.addEndListener(endListener);
        dynamic.flingAnimationY.addEndListener(endListener);
        dynamic.springAnimationX.addEndListener(endListener);
        dynamic.springAnimationY.addEndListener(endListener);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dynamic.flingAnimationX.setMinValue(0f - view.getWidth()).setMaxValue(screenWidth + view.getWidth());
                dynamic.flingAnimationY.setMinValue(0f - view.getHeight()).setMaxValue(screenHeight + view.getHeight());
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                dynamic.springAnimationX.start();
                dynamic.springAnimationY.start();

                action.onClick();

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                dynamic.flingAnimationX.setStartVelocity(velocityX);
                dynamic.flingAnimationY.setStartVelocity(velocityY);

                try
                {
                    dynamic.springAnimationX.cancel();
                    dynamic.springAnimationY.cancel();

                    dynamic.flingAnimationX.start();
                    dynamic.flingAnimationY.start();
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }

                return true;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            float lastX, lastY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);

                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE)
                {// move smoothly
                    float deltaX = motionEvent.getRawX() - lastX;
                    float deltaY = motionEvent.getRawY() - lastY;

                    view.setTranslationX(deltaX + view.getTranslationX());
                    view.setTranslationY(deltaY + view.getTranslationY());

                    handleWithScreenBorders(view);
                }

                lastX = motionEvent.getRawX();
                lastY = motionEvent.getRawY();

                return true;
            }
        });
    }

    private void handleWithScreenBorders(View view) {
        // if x of the left border || in the left half of screen
        if (view.getX() < 0 || (view.getX() + (view.getWidth() / 2)) < (screenWidth / 2))
        {
            view.animate().translationX(0).setDuration(200).start();
        }

        // if x of the right border || in the right half of screen
        if ((view.getX() + view.getWidth()) > screenWidth || (view.getX() + (view.getWidth() / 2)) > (screenWidth / 2))
        {
            view.animate().translationX(screenWidth - view.getWidth()).setDuration(200).start();
        }

        // if y of the up border
        if (view.getY() < 0)
        {
            view.animate().translationY(0).setDuration(200).start();
        }

        // if y of the bottom border
        if (view.getY() > (screenHeight - (view.getHeight() / 2)))
        {
            view.animate().translationY(screenHeight - view.getHeight()).setDuration(200).start();
        }
    }

    private void getScreenDimensions() {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }
}
