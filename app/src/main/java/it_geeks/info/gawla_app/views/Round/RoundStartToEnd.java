package it_geeks.info.gawla_app.views.Round;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.RoundRealTimeModel;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.SalonActivity;

public class RoundStartToEnd {

    boolean open_hall_status , free_join_status , pay_join_status , first_round_status , first_rest_status , second_round_status, second_rest_status, close_hall_status ;
    int open_hall_value ,free_join_value ,pay_join_value ,first_round_value ,first_rest_value , seconed_round_value ,seconed_rest_value , close_hall_value;
    String round_status;

    long[] mSecond = {0}, mMinute = {0}, mHour = {0};
    Context context;
    RoundStartToEndModel roundStartToEndModel;
    CountDownTimer countDownTimer;
    int joinStatus;

    public RoundStartToEnd(Context context, RoundStartToEndModel roundStartToEndModel) {
        this.context = context;
        this.roundStartToEndModel = roundStartToEndModel;
    }

    public void setTime(RoundRealTimeModel roundRealTimeModel) {

        open_hall_status = roundRealTimeModel.isOpen_hall_status();
        free_join_status = roundRealTimeModel.isFree_join_status();
        pay_join_status = roundRealTimeModel.isPay_join_status();
        first_round_status = roundRealTimeModel.isFirst_round_status();
        first_rest_status = roundRealTimeModel.isFirst_rest_status();
        second_round_status = roundRealTimeModel.isSeconed_round_status();
        second_rest_status = roundRealTimeModel.isSeconed_rest_status();
        close_hall_status = roundRealTimeModel.isClose_hall_status();

        open_hall_value = roundRealTimeModel.getOpen_hall_value();
        free_join_value = roundRealTimeModel.getFree_join_value();
        pay_join_value = roundRealTimeModel.getPay_join_value();
        first_round_value = roundRealTimeModel.getFirst_round_value();
        first_rest_value = roundRealTimeModel.getFirst_rest_value();
        seconed_round_value = roundRealTimeModel.getSeconed_round_value();
        seconed_rest_value = roundRealTimeModel.getSeconed_rest_value();
        close_hall_value = roundRealTimeModel.getClose_hall_value();

        round_status = roundRealTimeModel.getRound_status();
    }

    public void stop() {
        countDownTimer.cancel();
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
        Log.d("M7", "status: " + joinStatus);
    }

    public void start() {
        int milli = 1000;
        ((SalonActivity) context).checkOnTime();
        ((SalonActivity) context).notificationCard.setVisibility(View.VISIBLE);
       if (round_status.trim().equals("open")){
           if (open_hall_status){
               open_hall_value(open_hall_value * milli);
           }else if (free_join_status){
               free_join_status(free_join_value * milli);
           }else if (pay_join_status){
               pay_join_value(pay_join_value * milli);
           }else if (first_round_status){
               ((SalonActivity) context).timeState = "first_round_status";
               first_round_value(first_round_value * milli);
           }else if (first_rest_status){
               ((SalonActivity) context).timeState = "first_rest_status";
               first_rest_value(first_rest_value * milli);
           }else if (second_round_status){
               ((SalonActivity) context).timeState = "second_round_status";
               seconed_round_value(seconed_round_value * milli);
           }else if (second_rest_status){
               ((SalonActivity) context).timeState = "second_rest_status";
               seconed_rest_value(seconed_rest_value * milli);
           }else if (close_hall_status){
               ((SalonActivity) context).timeState = "close_hall_status";
               close_hall_value();
           }
       }else {
           ((SalonActivity) context).tvRoundActivity.setText(round_status);
       }

    }

    // before round start and open join
    private void open_hall_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.open_hall));
        ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.wait_round_to_start));
        DoCountDown(value);
    }

    // join Round Opened
    private void free_join_status(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.free_join));
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.you_are_joined));
        } else {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.you_can_join));
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }
        DoCountDown(value);
    }

    // join closed , use Golden Card
    private void pay_join_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.card_join_time));
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.you_are_joined));
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.can_use_golden_card));
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        }
        DoCountDown(value);
    }

    // add deal to product ( Round Time )
    private void first_round_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.first_round_time));
        ((SalonActivity)context).useRoundCard.setVisibility(View.GONE);
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.round_started_add_offer));
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.round_started));
        }
        DoCountDown(value);
    }

    //Rest show the winner
    private void first_rest_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.rest_time));
        ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.rest_time));
        DoCountDown(value);
    }
    // Second Round when user restart the round
    private void seconed_round_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.second_round_time));
        ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.second_round_time));
        DoCountDown(value);
    }
    // second Rest
    private void seconed_rest_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.second_rest_time));
        ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.second_rest_time));
        DoCountDown(value);
    }

    private void close_hall_value() {
        ((SalonActivity)context).tvSalonTime.setText(context.getResources().getString(R.string.round_closed));
        ((SalonActivity) context).tvRoundActivity.setText(context.getResources().getString(R.string.round_closed));
        ((SalonActivity) context).notificationCard.setVisibility(View.GONE);
    }

            ///////////////////////////////////////////////////////

    private void DoCountDown(long value) {
        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished/1000);
                }

                public void onFinish() {
                    ((SalonActivity)context).getRemainingTimeOfRound();
                }

            }.start();
        } catch (Exception e) {
            Log.e("Mo7", e.getMessage());
        }
    }

    private void setTimeDown(long millisUntilFinished) {

        long second = millisUntilFinished % 60;
        long minute = (millisUntilFinished / 60) % 60;
        long hour = (millisUntilFinished / (60 * 60)) % 24;

        if (mSecond[0] != second) {
            GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "second");
            gawlaTimeDownSecond.NumberTick(second);
        }
        mSecond[0] = second;

        if (mMinute[0] != minute) {
            GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "minute");
            gawlaTimeDownMinute.NumberTick(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour) {
            GawlaTimeDown gawlaTimeDownHour = new GawlaTimeDown(context, roundStartToEndModel.getUpDivsList(), roundStartToEndModel.getDownDivsList(), roundStartToEndModel.getDrawablesUp(), roundStartToEndModel.getDrawablesDown(), "hour");
            gawlaTimeDownHour.NumberTick(hour);
        }
        mHour[0] = hour;


    }

}
