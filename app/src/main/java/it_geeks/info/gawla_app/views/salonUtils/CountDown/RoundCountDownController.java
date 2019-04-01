package it_geeks.info.gawla_app.views.salonUtils.CountDown;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.RoundRemainingTime;
import it_geeks.info.gawla_app.repository.Models.HalvesModel;
import it_geeks.info.gawla_app.views.SalonActivity;

public class RoundCountDownController {

    private RoundRemainingTime roundRemainingTime;
    private HalvesModel halvesModel;

    private CountDownTimer countDownTimer;
    private Context context;
    private long[] mSecond = {0}, mMinute = {0}, mHour = {0};

    public RoundCountDownController(Context context, View parent) {
        this.context = context;
        this.halvesModel = new HalvesModel(context, parent);
    }

    public void setRoundRemainingTime(RoundRemainingTime roundRemainingTime) {
        this.roundRemainingTime = roundRemainingTime;
    }

    public void setUserJoin(boolean state) {
        roundRemainingTime.setUserJoin(state);
    }

    public void stopCountDown() {
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    public void updateCountDown() {
        int oneSecond = 1000;

        ((SalonActivity) context).checkOnTime();
        if (roundRemainingTime.getRound_state().trim().equals("open")) {
            if (roundRemainingTime.isOpen_hall_state()) {
                onRest(roundRemainingTime.getOpen_hall_value() * oneSecond, context.getResources().getString(R.string.before_free_join));

            } else if (roundRemainingTime.isFree_join_state()) {
                free_join_status(roundRemainingTime.getFree_join_value() * oneSecond);

            } else if (roundRemainingTime.isPay_join_state()) {
                pay_join_value(roundRemainingTime.getPay_join_value() * oneSecond);

            } else if (roundRemainingTime.isFirst_round_state()) {
                first_round_value(roundRemainingTime.getFirst_round_value() * oneSecond);

            } else if (roundRemainingTime.isFirst_rest_state()) {
                onRest(roundRemainingTime.getFirst_rest_value() * oneSecond, context.getResources().getString(R.string.rest_time));

            } else if (roundRemainingTime.isSecond_round_state()) {
                second_round_value(roundRemainingTime.getSecond_round_value() * oneSecond);

            } else if (roundRemainingTime.isSecond_rest_state()) {
                onRest(roundRemainingTime.getSecond_rest_value() * oneSecond, context.getResources().getString(R.string.second_rest_time));

            } else if (roundRemainingTime.isClose_hall_state()) {
                close_hall_value();
            }

        } else {
            ((SalonActivity) context).updateLatestActivity(context.getResources().getString(R.string.closed));
            ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.closed));
        }
    }

    // join Round Opened
    private void free_join_status(long value) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.free_join));
        if (!roundRemainingTime.isUserJoin()) {
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }
    }

    // join closed , use Golden Card
    private void pay_join_value(long value) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.card_join_time));
        ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
    }

    // add deal to product ( Round Time )
    private void first_round_value(long value) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.first_round_time));
        ((SalonActivity) context).hideGoldenLayout();
        if (roundRemainingTime.isUserJoin()) {
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        }
    }

    //Rest show the winner
    private void onRest(long value, String message) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(message);
    }

    // Second Round when user restart the round
    private void second_round_value(long value) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.second_round_time));
        ((SalonActivity) context).selectDetailsTab();
    }

    private void close_hall_value() {
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.closed));
        ((SalonActivity) context).updateLatestActivity(context.getResources().getString(R.string.activity_empty_hint));
    }

    private void startCountDown(long value) {
        try {
            stopCountDown();
            countDownTimer = new CountDownTimer(value, 1000) {

                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished / 1000);
                }

                public void onFinish() {
                    ((SalonActivity) context).getRemainingTimeOfRound();
                }

            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void setTimeDown(long millisUntilFinished) {
        long hour = (millisUntilFinished / (60 * 60)) % 24;
        long minute = (millisUntilFinished / 60) % 60;
        long second = millisUntilFinished % 60;

        if (mSecond[0] != second) {
            CountDownAnimator countDownAnimatorSecond = new CountDownAnimator(context, halvesModel.getUpperHalvesIdsList(), halvesModel.getLowerHalvesIdsList(), halvesModel.getUpperHalvesDrawablesList(), halvesModel.getLowerHalvesDrawablesList(), "second");
            countDownAnimatorSecond.NumberTick(second);
        }
        mSecond[0] = second;

        if (mMinute[0] != minute) {
            CountDownAnimator countDownAnimatorMinute = new CountDownAnimator(context, halvesModel.getUpperHalvesIdsList(), halvesModel.getLowerHalvesIdsList(), halvesModel.getUpperHalvesDrawablesList(), halvesModel.getLowerHalvesDrawablesList(), "minute");
            countDownAnimatorMinute.NumberTick(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour) {
            CountDownAnimator countDownAnimatorHour = new CountDownAnimator(context, halvesModel.getUpperHalvesIdsList(), halvesModel.getLowerHalvesIdsList(), halvesModel.getUpperHalvesDrawablesList(), halvesModel.getLowerHalvesDrawablesList(), "hour");
            countDownAnimatorHour.NumberTick(hour);
        }
        mHour[0] = hour;

        if (hour == 0 && minute == 0 && second == 1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setTimeDown(0); // zero time
                }
            },1000);
        }
    }
}