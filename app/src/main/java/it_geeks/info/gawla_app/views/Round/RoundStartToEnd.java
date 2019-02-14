package it_geeks.info.gawla_app.views.Round;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Models.RoundRealTimeModel;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;
import it_geeks.info.gawla_app.views.SalonActivity;

public class RoundStartToEnd {

    boolean open_hall_status , free_join_status , pay_join_status , first_round_status , first_rest_status , second_round_status, seconed_rest_status , close_hall_status ;
    int open_hall_value ,free_join_value ,pay_join_value ,first_round_value ,first_rest_value , seconed_round_value ,seconed_rest_value , close_hall_value;
    String round_status;

    int[] mSecond = {0}, mMinute = {0}, mHour = {0} , mDay = {0};
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
        seconed_rest_status = roundRealTimeModel.isSeconed_rest_status();
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
        Log.e("Mo7",open_hall_status +" "+ free_join_status +" "+ pay_join_status +" "+ first_round_status +" "+ first_rest_status +" "+ second_round_status +" "+ seconed_rest_status +" "+ close_hall_status);
        ((SalonActivity) context).hideConfirmationLayout();
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
               first_rest_value(first_rest_value * milli);
           }else if (second_round_status){
               ((SalonActivity) context).timeState = "second_round_status";
               seconed_round_value(seconed_round_value * milli);
           }else if (seconed_rest_status){
               seconed_rest_value(seconed_rest_value * milli);
           }else if (close_hall_status){
               close_hall_value();
           }
       }else {
           ((SalonActivity) context).tvRoundActivity.setText(round_status);
       }

    }

    // before round start and open join
    private void open_hall_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Time to To Free Join");
        ((SalonActivity) context).tvRoundActivity.setText("wait round to start");
        DoCountDown(value);
    }


    // join Round Opened
    private void free_join_status(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Free Join");
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText(" You are joined");
        } else {
            ((SalonActivity) context).tvRoundActivity.setText("You can Join Now");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.VISIBLE);
        }
        DoCountDown(value);
    }

    // join closed , use Golden Card
    private void pay_join_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Card Join Time");
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText("You are joined");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).tvRoundActivity.setText("You can use golden card to join now");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        }
        DoCountDown(value);
    }

    // add deal to product ( Round Time )
    private void first_round_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("First Round Time");
        if (joinStatus == 2) {
            ((SalonActivity) context).tvRoundActivity.setText("round stared add offers to win");
            ((SalonActivity) context).btnJoinRound.setVisibility(View.GONE);
        } else {
            ((SalonActivity) context).tvRoundActivity.setText("round stared");
            ((SalonActivity) context).cancelConfirmation();
        }
        DoCountDown(value);
    }

    //Rest show the winner
    private void first_rest_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Rest Time");
        ((SalonActivity) context).tvRoundActivity.setText("rest time");
        DoCountDown(value);
    }
    // Second Round when user restart the round
    private void seconed_round_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Second Round time");
        ((SalonActivity) context).tvRoundActivity.setText("Second Round time");
        DoCountDown(value);
    }
    // second Rest
    private void seconed_rest_value(long value) {
        ((SalonActivity)context).tvSalonTime.setText("Second Rest time");
        ((SalonActivity) context).tvRoundActivity.setText("Second rest time");
        DoCountDown(value);
    }

    private void close_hall_value() {
        ((SalonActivity)context).tvSalonTime.setText("Round Closed");
        ((SalonActivity) context).tvRoundActivity.setText("Round Closed");
    }

            ///////////////////////////////////////////////////////

    private void DoCountDown(long value) {
        try {
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
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
        Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (calendar.get(Calendar.HOUR_OF_DAY) == 2) {
            hour = 0;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 1) {
            hour = 23;
        } else if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            hour = 22;
        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY) - 2;
        }
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

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
