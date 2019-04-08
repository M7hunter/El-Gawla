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
        try {
            updateState();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public void setUserJoin(boolean state) {
        roundRemainingTime.setUserJoin(state);
    }

    public void stopCountDown() {
        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    private void updateState() {
        ((SalonActivity) context).checkOnTime();
        if (roundRemainingTime.getRound_state().trim().equals("open")) {
            if (roundRemainingTime.isOpen_hall_state()) {
                onRest(roundRemainingTime.getOpen_hall_value(), context.getResources().getString(R.string.before_free_join));

            } else if (roundRemainingTime.isFree_join_state()) {
                onFreeJoin();

            } else if (roundRemainingTime.isPay_join_state()) {
                onPayJoin();

            } else if (roundRemainingTime.isFirst_round_state()) {
                onFirstRound();

            } else if (roundRemainingTime.isFirst_rest_state()) {
                onRest(roundRemainingTime.getFirst_rest_value(), context.getResources().getString(R.string.rest_time));

            } else if (roundRemainingTime.isSecond_round_state()) {
                onSecondRound();

            } else if (roundRemainingTime.isSecond_rest_state()) {
                onRest(roundRemainingTime.getSecond_rest_value(), context.getResources().getString(R.string.second_rest_time));

            } else if (roundRemainingTime.isClose_hall_state()) {
                onClosed();
            }

        } else {
            onClosed();
        }
    }

    private void onRest(long value, String message) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(message);
    }

    private void onFreeJoin() {
        startCountDown(roundRemainingTime.getFree_join_value());
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.free_join));
        if (!roundRemainingTime.isUserJoin()) {
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }
    }

    private void onPayJoin() {
        startCountDown(roundRemainingTime.getPay_join_value());
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.card_join_time));
        ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
    }

    private void onFirstRound() {
        startCountDown(roundRemainingTime.getFirst_round_value());
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.first_round_time));
        ((SalonActivity) context).hideGoldenLayout();
        if (roundRemainingTime.isUserJoin()) {
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        }
    }

    private void onSecondRound() {
        startCountDown(roundRemainingTime.getSecond_round_value());
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.second_round_time));
        ((SalonActivity) context).selectDetailsTab();
    }

    private void onClosed() {
        ((SalonActivity) context).tvSalonMessage.setText(context.getResources().getString(R.string.closed));
        ((SalonActivity) context).updateLatestActivity(context.getResources().getString(R.string.activity_empty_hint));
    }

    private void startCountDown(long value) {
        try {
            stopCountDown();
            countDownTimer = new CountDownTimer(value * 1000, 1000) {

                public void onTick(final long millisUntilFinished) {
                    animateOnTick(millisUntilFinished / 1000);
                }

                public void onFinish() {
                    ((SalonActivity) context).getRemainingTimeFromServer();
                }

            }.start();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void animateOnTick(long millisUntilFinished) {
        long hour = (millisUntilFinished / (60 * 60)) % 24;
        long minute = (millisUntilFinished / 60) % 60;
        long second = millisUntilFinished % 60;

        if (mSecond[0] != second) {
            CountDownAnimator countDownAnimatorSecond = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "second");
            countDownAnimatorSecond.tickNumber(second);
        }
        mSecond[0] = second;

        if (mMinute[0] != minute) {
            CountDownAnimator countDownAnimatorMinute = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "minute");
            countDownAnimatorMinute.tickNumber(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour) {
            CountDownAnimator countDownAnimatorHour = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "hour");
            countDownAnimatorHour.tickNumber(hour);
        }
        mHour[0] = hour;

        if (hour == 0 && minute == 0 && second == 1){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateOnTick(0); // zero time
                }
            },1000);
        }
    }
}