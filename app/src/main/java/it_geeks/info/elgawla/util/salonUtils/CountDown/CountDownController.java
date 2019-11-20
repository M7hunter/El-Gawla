package it_geeks.info.elgawla.util.salonUtils.CountDown;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.util.AudioPlayer;
import it_geeks.info.elgawla.repository.Models.RoundRemainingTime;
import it_geeks.info.elgawla.repository.Models.HalvesModel;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.views.salon.SalonActivity;

public class CountDownController {

    private RoundRemainingTime roundRemainingTime;
    private HalvesModel halvesModel;

    private CountDownTimer countDownTimer;
    private Context context;
    private long[] mSecond = {0}, mMinute = {0}, mHour = {0};
    private boolean pause = false;

    public CountDownController(Context context, View parent) {
        this.context = context;
        this.halvesModel = new HalvesModel(context, parent);
    }

    public void setRoundRemainingTime(RoundRemainingTime roundRemainingTime) {
        this.roundRemainingTime = roundRemainingTime;
        try
        {
            updateState();
        }
        catch (NullPointerException e)
        {
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
        if (roundRemainingTime.getRound_status().trim().equals("open"))
        {
            if (roundRemainingTime.isOpen_hall_state())
            {
                onRest(roundRemainingTime.getOpen_hall_value());
            }
            else if (roundRemainingTime.isFree_join_state())
            {
                onFreeJoin();
            }
            else if (roundRemainingTime.isPay_join_state())
            {
                onPayJoin();
            }
            else if (roundRemainingTime.isRound_state())
            {
                onRound();
            }
            else if (roundRemainingTime.isRest_state())
            {
                onRest(roundRemainingTime.getRest_value());
            }
            else if (roundRemainingTime.isClose_hall_state())
            {
                onClosed();
            }
            EventsManager.sendSalonLevelEvent(context, 0, roundRemainingTime.getRound_status());
        }
        else
        {
            onClosed();
        }
    }

    private void onRest(long value) {
        startCountDown(value);
        ((SalonActivity) context).tvSalonMessage.setText(roundRemainingTime.getMessage());
    }

    private void onFreeJoin() {
        startCountDown(roundRemainingTime.getFree_join_value());
        ((SalonActivity) context).tvSalonMessage.setText(roundRemainingTime.getMessage());
        if (!roundRemainingTime.isUserJoin())
        {
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }
    }

    private void onPayJoin() {
        startCountDown(roundRemainingTime.getPay_join_value());
        ((SalonActivity) context).tvSalonMessage.setText(roundRemainingTime.getMessage());
        ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
    }

    private void onRound() {
        startCountDown(roundRemainingTime.getRound_value());
        ((SalonActivity) context).tvSalonMessage.setText(roundRemainingTime.getMessage());

        ((SalonActivity) context).hideGoldenLayout();
        ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
    }

    private void onClosed() {
        ((SalonActivity) context).tvSalonMessage.setText(roundRemainingTime.getMessage());
        ((SalonActivity) context).updateLatestActivity(context.getResources().getString(R.string.activity_empty_hint));
    }

    private void startCountDown(long value) {
        try
        {
            stopCountDown();
            countDownTimer = new CountDownTimer(value * 1000, 1000) {
                public void onTick(final long millisUntilFinished) {
                    if (!pause)
                    {
                        if (!AudioPlayer.getInstance().isPlaying())
                        {
                            AudioPlayer.getInstance().play(context, R.raw.large_clock_tick);
                        }
                    }
                    animateOnTick(millisUntilFinished / 1000);
                }

                public void onFinish() {
                    ((SalonActivity) context).getRemainingTimeFromServer();
                }

            }.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    private void animateOnTick(long millisUntilFinished) {
        long hour = (millisUntilFinished / (60 * 60)) % 24;
        long minute = (millisUntilFinished / 60) % 60;
        long second = millisUntilFinished % 60;

        if (mSecond[0] != second)
        {
            CountDownAnimator countDownAnimatorSecond = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "second");
            countDownAnimatorSecond.tickNumber(second);
        }
        mSecond[0] = second;

        if (mMinute[0] != minute)
        {
            CountDownAnimator countDownAnimatorMinute = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "minute");
            countDownAnimatorMinute.tickNumber(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour)
        {
            CountDownAnimator countDownAnimatorHour = new CountDownAnimator(context, halvesModel.getUpperViewsList(), halvesModel.getLowerViewsList(), halvesModel.getUpperDrawablesResList(), halvesModel.getLowerDrawablesResList(), "hour");
            countDownAnimatorHour.tickNumber(hour);
        }
        mHour[0] = hour;

        if (hour == 0 && minute == 0 && second == 1)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    animateOnTick(0); // zero time
                }
            }, 1000);
        }
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }
}