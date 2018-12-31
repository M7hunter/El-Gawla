package it_geeks.info.gawla_app.Views.Round;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.Views.SalonActivity;

public class RoundStartToEnd {

    private String round_start_time, round_end_time, first_join_time, second_join_time, round_date, round_time, rest_time;
    String currentTime;
    int[] mMinute = {0}, mHour = {0};
    Context context;
    RoundStartToEndModel roundStartToEndModel;
    CountDownTimer countDownTimer;
    int joinStatus;

    public RoundStartToEnd(Context context, RoundStartToEndModel roundStartToEndModel) {
        this.context = context;
        this.roundStartToEndModel = roundStartToEndModel;
    }

    public void setTime(String round_start_time, String round_end_time, String first_join_time, String second_join_time, String round_date, String round_time, String rest_time) {
        this.round_start_time = round_start_time + ":00";
        this.round_end_time = round_end_time + ":00";
        this.first_join_time = first_join_time + ":00";
        this.second_join_time = second_join_time + ":00";
        this.round_date = round_date;
        this.round_time = "00:" + round_time;
        this.rest_time = "00:" + rest_time;
    }

    public void stop() {
        countDownTimer.cancel();
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
        Log.d("M7", "status: " + joinStatus);
    }

    public void start() {
        TimeZone tz = TimeZone.getTimeZone("Africa/Cairo");
        Calendar c = Calendar.getInstance(tz);

        currentTime = String.format(c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND));

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(first_join_time);
        long value = end - start;

        countDownBeforeJoin(value);
    }

    // before round start and open join
    private void countDownBeforeJoin(long value) {

        ((SalonActivity) context).round_notification_text.setText("wait round to start .");

        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    countDown_FirstJoinTime();
                }

            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }

    }

    // join Round Opened
    private void countDown_FirstJoinTime() {
        if (joinStatus == 2) {
            ((SalonActivity) context).round_notification_text.setText(" You are joined .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).round_notification_text.setText("You can Join Now .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(second_join_time);
        long value = end - start;
        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    countDown_SecondJoinTime();
                }
            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }

    }

    // join closed , use Golden Card
    private void countDown_SecondJoinTime() {
        if (joinStatus == 2) {
            ((SalonActivity) context).round_notification_text.setText("You are joined .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).round_notification_text.setText("You can use golden card to join now .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        }

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(round_start_time);
        long value = end - start;
        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    countDown_RoundTime();
                }
            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }

    }

    // add deal to product ( Round Time )
    private void countDown_RoundTime() {
        if (joinStatus == 2) {
            ((SalonActivity) context).round_notification_text.setText("round stared add offers to win .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
            ((SalonActivity) context).hideConfirmationLayout();
        } else {
            ((SalonActivity) context).round_notification_text.setText("round stared .");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
            ((SalonActivity) context).cancelConfirmation();
        }

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(round_end_time);
        long value = end - start;
        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {

                    countDownRestTime();
                }

            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }
    }

    //Rest before show the winner
    private void countDownRestTime() {

        ((SalonActivity) context).round_notification_text.setText("rest time .");

        AlertDialog.Builder alert = new AlertDialog.Builder(context);

//        long start = Common.Instance(context).formatTimeToMillis(currentTime);
//        long end = start + Common.Instance(context).formatTimeToMillis(rest_time);
//        long value = end - start;
        try {
            countDownTimer = new CountDownTimer(60000, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    ((SalonActivity) context).round_notification_text.setText("round ended .");
                }

            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }
    }

    private void setTimeDown(long millisUntilFinished) {
        Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
        int hour = 0;
        if (calendar.get(Calendar.HOUR_OF_DAY) == 2) {
            hour = 0;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 1) {
            hour = 23;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            hour = 22;
        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY) - 2;
        }

        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);


        GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "second");
        gawlaTimeDownSecond.NumberTick(second);

        Log.e("Mo7", hour + " " + minute + " " + second + "");

        if (mMinute[0] != minute) {
            GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "minute");
            gawlaTimeDownMinute.NumberTick(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour) {
            GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "hour");
            gawlaTimeDownMinute.NumberTick(hour);
        }
        mHour[0] = hour;
    }


}
