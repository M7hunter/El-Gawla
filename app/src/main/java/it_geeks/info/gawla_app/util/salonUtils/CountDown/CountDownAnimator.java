package it_geeks.info.gawla_app.util.salonUtils.CountDown;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;

public class CountDownAnimator implements Animation.AnimationListener {

    private List<ImageView> upperViewsList = new ArrayList<>(), lowerViewsList = new ArrayList<>();
    private List<Integer> upperDrawablesResList = new ArrayList<>(), lowerDrawablesResList = new ArrayList<>();

    private Animation rightUpperAnim, rightLowerAnim, leftUpperAnim, leftLowerAnim;
    private int rightNumber, leftNumber;
//    private int right = 0;

    private Context context;

    CountDownAnimator(Context context, List<ImageView> upperViewsList, List<ImageView> lowerViewsList, List<Integer> upperDrawablesResList, List<Integer> lowerDrawablesResList, String countType) {
        this.context = context;
        this.upperDrawablesResList.addAll(upperDrawablesResList);
        this.lowerDrawablesResList.addAll(lowerDrawablesResList);

        switch (countType) {
            case "second":
                for (int i = 0; i < 4; i++) {
                    this.upperViewsList.add(upperViewsList.get(i));
                    this.lowerViewsList.add(lowerViewsList.get(i));
                }
                break;
            case "minute":
                for (int i = 4; i < 8; i++) {
                    this.upperViewsList.add(upperViewsList.get(i));
                    this.lowerViewsList.add(lowerViewsList.get(i));
                }
                break;
            case "hour":
                for (int i = 8; i < 12; i++) {
                    this.upperViewsList.add(upperViewsList.get(i));
                    this.lowerViewsList.add(lowerViewsList.get(i));
                }
                break;
        }
    }

    // tick number
    void tickNumber(long millisUntilFinished) {
        try {
            int num = (int) millisUntilFinished;
            int leftDigit, rightDigit;

            if (String.valueOf(num).length() == 2) { // >= 10
                leftDigit = Integer.parseInt(Integer.toString(num).substring(0, 1));
                rightDigit = Integer.parseInt(Integer.toString(num).substring(1, 2));
                leftNumber = leftDigit;
                rightNumber = rightDigit;
            } else { // < 10
                rightDigit = Integer.parseInt(Integer.toString(num).substring(0, 1));
                leftDigit = 0;
                rightNumber = rightDigit;
                leftNumber = leftDigit;
            }

            if (num < 60) {
                animRight();
//                animUpper(1);
//                right = 1;
                upperViewsList.get(0).setImageResource(upperDrawablesResList.get(rightDigit));
                upperViewsList.get(2).setImageResource(upperDrawablesResList.get(leftDigit));
                lowerViewsList.get(1).setImageResource(lowerDrawablesResList.get(rightDigit));
                lowerViewsList.get(3).setImageResource(lowerDrawablesResList.get(leftDigit));

                if (rightDigit == 9) {
                    animLeft();
//                    animUpper(3);
//                    right = 3;
                } else {
                    upperViewsList.get(3).setImageResource(upperDrawablesResList.get(leftDigit));
                    lowerViewsList.get(2).setImageResource(lowerDrawablesResList.get(leftDigit));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void animRight() {
        upperViewsList.get(1).setVisibility(View.VISIBLE);
        rightUpperAnim = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
        rightUpperAnim.setAnimationListener(this);
        upperViewsList.get(1).startAnimation(rightUpperAnim);
    }

    private void animLeft() {
        upperViewsList.get(3).setVisibility(View.VISIBLE);
        leftUpperAnim = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
        leftUpperAnim.setAnimationListener(this);
        upperViewsList.get(3).startAnimation(leftUpperAnim);
    }

    private void animUpper(int i1) {
        upperViewsList.get(i1).setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
        anim.setAnimationListener(this);
        upperViewsList.get(i1).startAnimation(anim);
    }

    private void animLower(int i1) {
        upperViewsList.get(i1).setVisibility(View.INVISIBLE);
        lowerViewsList.get(i1).setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
        anim.setAnimationListener(this);
        lowerViewsList.get(i1).startAnimation(anim);
    }

    private void reInitToAnim(int i1, int i2, int num) {
        lowerViewsList.get(i1).setVisibility(View.INVISIBLE);
        upperViewsList.get(i1).setImageResource(upperDrawablesResList.get(num));
        lowerViewsList.get(i2).setImageResource(lowerDrawablesResList.get(num));
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == rightUpperAnim) {
            upperViewsList.get(1).setVisibility(View.INVISIBLE);
            lowerViewsList.get(1).setVisibility(View.VISIBLE);
            rightLowerAnim = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            rightLowerAnim.setAnimationListener(this);
            lowerViewsList.get(1).startAnimation(rightLowerAnim);
//            animLower(1);
//            right = 2;

        } else if (animation == rightLowerAnim) {
            lowerViewsList.get(1).setVisibility(View.INVISIBLE);
            upperViewsList.get(1).setImageResource(upperDrawablesResList.get(rightNumber));
            lowerViewsList.get(0).setImageResource(lowerDrawablesResList.get(rightNumber));
//            reInitToAnim(1, 0, rightNumber);
        }

        if (animation == leftUpperAnim) {
            upperViewsList.get(3).setVisibility(View.INVISIBLE);
            lowerViewsList.get(3).setVisibility(View.VISIBLE);
            leftLowerAnim = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            leftLowerAnim.setAnimationListener(this);
            lowerViewsList.get(3).startAnimation(leftLowerAnim);
//            animLower(3);
//            right = 4;

        } else if (animation == leftLowerAnim) {
            lowerViewsList.get(3).setVisibility(View.INVISIBLE);
            upperViewsList.get(3).setImageResource(upperDrawablesResList.get(leftNumber));
            lowerViewsList.get(2).setImageResource(lowerDrawablesResList.get(leftNumber));
//            reInitToAnim(3, 2, leftNumber);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
