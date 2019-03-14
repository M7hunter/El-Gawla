package it_geeks.info.gawla_app.views.Round;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.R;

public class GawlaTimeDown implements Animation.AnimationListener {

    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();

    private Animation animation1;
    private Animation animation2;
    private Animation animation3;
    private Animation animation4;
    private int currentNumberTens;
    private int currentNumber;

    public static boolean statusTime;
    Context context;

    public GawlaTimeDown(Context context, List<ImageView> upDivsList, List<ImageView> downDivsList, List<Integer> drawablesUp, List<Integer> drawablesDown, String typeOnTime) {
        this.context = context;

        if (typeOnTime == "second") {
            for (int i = 0; i < 4; i++) {
                this.upDivsList.add(upDivsList.get(i));
                this.downDivsList.add(downDivsList.get(i));
            }
        } else if (typeOnTime == "minute") {
            for (int i = 4; i < 8; i++) {
                this.upDivsList.add(upDivsList.get(i));
                this.downDivsList.add(downDivsList.get(i));
            }
        } else if (typeOnTime == "hour") {
            for (int i = 8; i < 12; i++) {
                this.upDivsList.add(upDivsList.get(i));
                this.downDivsList.add(downDivsList.get(i));
            }
        }
        for (int i = 0; i < 12; i++) {
            this.drawablesUp.add(drawablesUp.get(i));
            this.drawablesDown.add(drawablesDown.get(i));
        }
    }

    // tick number
    public void NumberTick(long millisUntilFinished) {
        try {

            int num = (int) millisUntilFinished;
            int firstDigit = 0, secondDigit = 0;
            if (String.valueOf(num).length() == 2) {
                firstDigit = Integer.parseInt(Integer.toString(num).substring(0, 1));
                secondDigit = Integer.parseInt(Integer.toString(num).substring(1, 2));
                currentNumber = secondDigit;
                currentNumberTens = firstDigit;
            } else {
                firstDigit = Integer.parseInt(Integer.toString(num).substring(0, 1));
                currentNumber = firstDigit;
                currentNumberTens = 0;
            }


            if (num < 10) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(firstDigit));

                downDivsList.get(2).setImageResource(drawablesDown.get(0));
                upDivsList.get(2).setImageResource(drawablesUp.get(0));
                upDivsList.get(3).setImageResource(drawablesUp.get(0));
                downDivsList.get(3).setImageResource(drawablesDown.get(0));
                if (num == 9) {
                    tensanmi();
                }
            }

            if (num >= 10 && num < 20) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(secondDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(secondDigit));

                upDivsList.get(2).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(3).setImageResource(drawablesDown.get(firstDigit));
                if (num == 19) {
                    tensanmi();
                } else {
                    upDivsList.get(3).setImageResource(drawablesUp.get(firstDigit));
                    downDivsList.get(2).setImageResource(drawablesDown.get(firstDigit));
                }
            }

            if (num >= 20 && num < 30) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(secondDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(secondDigit));

                upDivsList.get(2).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(3).setImageResource(drawablesDown.get(firstDigit));
                if (num == 29) {
                    tensanmi();
                } else {
                    upDivsList.get(3).setImageResource(drawablesUp.get(firstDigit));
                    downDivsList.get(2).setImageResource(drawablesDown.get(firstDigit));
                }
            }

            if (num >= 30 && num < 40) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(secondDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(secondDigit));

                upDivsList.get(2).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(3).setImageResource(drawablesDown.get(firstDigit));
                if (num == 39) {
                    tensanmi();
                } else {
                    upDivsList.get(3).setImageResource(drawablesUp.get(firstDigit));
                    downDivsList.get(2).setImageResource(drawablesDown.get(firstDigit));
                }
            }

            if (num >= 40 && num < 50) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(secondDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(secondDigit));

                upDivsList.get(2).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(3).setImageResource(drawablesDown.get(firstDigit));
                if (num == 49) {
                    tensanmi();
                } else {
                    upDivsList.get(3).setImageResource(drawablesUp.get(firstDigit));
                    downDivsList.get(2).setImageResource(drawablesDown.get(firstDigit));
                }
            }

            if (num >= 50 && num < 60) {
                anmi(num);
                upDivsList.get(0).setImageResource(drawablesUp.get(secondDigit));
                downDivsList.get(1).setImageResource(drawablesDown.get(secondDigit));

                upDivsList.get(2).setImageResource(drawablesUp.get(firstDigit));
                downDivsList.get(3).setImageResource(drawablesDown.get(firstDigit));
                if (num == 59) {
                    upDivsList.get(3).setImageResource(drawablesUp.get(currentNumberTens));
                    downDivsList.get(2).setImageResource(drawablesDown.get(currentNumberTens));
                    tensanmi();
                } else {
                    upDivsList.get(3).setImageResource(drawablesUp.get(firstDigit));
                    downDivsList.get(2).setImageResource(drawablesDown.get(firstDigit));
                }
            }


        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
            Crashlytics.logException(e);
        }
    }

    // anmie for basice number
    private void anmi(int n) {

        if (n < 60) {
            animation1 = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
            animation1.setAnimationListener(this);
            upDivsList.get(1).setVisibility(View.VISIBLE);
            upDivsList.get(1).startAnimation(animation1);

        }

    }

    // anmie for tens number
    private void tensanmi() {
        animation3 = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
        animation3.setAnimationListener(this);
        upDivsList.get(3).setVisibility(View.VISIBLE);
        upDivsList.get(3).startAnimation(animation3);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation1) {
            upDivsList.get(1).setVisibility(View.INVISIBLE);
            downDivsList.get(1).setVisibility(View.VISIBLE);
            animation2 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation2.setAnimationListener(this);
            downDivsList.get(1).startAnimation(animation2);
        } else if (animation == animation2) {
            downDivsList.get(1).setVisibility(View.INVISIBLE);
            upDivsList.get(1).setImageResource(drawablesUp.get(currentNumber));
            downDivsList.get(0).setImageResource(drawablesDown.get(currentNumber));

        }

        if (animation == animation3) {
            upDivsList.get(3).setVisibility(View.INVISIBLE);
            downDivsList.get(3).setVisibility(View.VISIBLE);
            animation4 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation4.setAnimationListener(this);
            downDivsList.get(3).startAnimation(animation4);

        } else if (animation == animation4) {
            downDivsList.get(3).setVisibility(View.INVISIBLE);
            upDivsList.get(3).setImageResource(drawablesUp.get(currentNumberTens));
            downDivsList.get(2).setImageResource(drawablesDown.get(currentNumberTens));
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
