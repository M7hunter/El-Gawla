package it_geeks.info.gawla_app.Views.Round;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;

public class RoundStartToEnd {

    private List<ImageView> upDivsList = new ArrayList<>();
    private List<ImageView> downDivsList = new ArrayList<>();
    private List<Integer> drawablesUp = new ArrayList<>();
    private List<Integer> drawablesDown = new ArrayList<>();
    private String round_start_time , round_end_time , first_join_time , second_join_time , round_date , round_time , rest_time;
    String currentTime;
    int[] mMinute= {0},mHour = {0};
    Context context;
    RoundStartToEndModel roundStartToEndModel;
    CountDownTimer countDownTimer;
    int joinStatus ;

    public RoundStartToEnd(Context context, RoundStartToEndModel roundStartToEndModel) {
        this.context = context;
        this.roundStartToEndModel = roundStartToEndModel;
    }

    public RoundStartToEnd() {
        for (int i = 0; i < 12 ; i++) {
            this.upDivsList.add(upDivsList.get(i));
            this.downDivsList.add(downDivsList.get(i));
        }
        for (int i = 0; i < 12; i++) {
            this.drawablesUp.add(drawablesUp.get(i));
            this.drawablesDown.add(drawablesDown.get(i));
        }
    }

    public void setTime(String round_start_time,String round_end_time ,String first_join_time ,String second_join_time ,String round_date ,String round_time ,String rest_time){
        this.round_start_time = round_start_time;
        this.round_end_time = round_end_time;
        this.first_join_time = first_join_time;
        this.second_join_time = second_join_time;
        this.round_date = round_date;
        this.round_time = round_time;
        this.rest_time = rest_time;
    }

    public void start(){
        TimeZone tz = TimeZone.getTimeZone("Africa/Cairo");
        Calendar c = Calendar.getInstance(tz);

       // Common.Instance(context).formatDateStringToCalendar(round_date);
        currentTime = String.format(c.get(Calendar.HOUR_OF_DAY)+":"+ c.get(Calendar.MINUTE)+":"+ c.get(Calendar.SECOND));

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(round_start_time+":00");
        long value = end - start;
        countDownBeforeStart(value);
    }

    public void stop(){
        countDownTimer.cancel();
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
    }

    // before round start and open join
    private void countDownBeforeStart(long value) {
        try{
            Toast.makeText(context, "BeforeStart", Toast.LENGTH_LONG).show();
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                     roundStartToEndModel.getRound_notification_text().setText("Round Opened , You can Join Now .");
                    roundStartToEndModel.getBtnJoinRound().setVisibility(View.VISIBLE);
                    countDownToWaiting();
                }

            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    // join Round Opened
    private void countDownToWaiting(){
        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(first_join_time+":00");
        long value = end - start;
        try{
            Toast.makeText(context, "ToWaiting", Toast.LENGTH_LONG).show();
            final int[] mMinute= {0},mHour = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    roundStartToEndModel.getBtnJoinRound().setVisibility(View.INVISIBLE);
                    countDownWaiting();
                    if (joinStatus == 2){
                        roundStartToEndModel.getRound_notification_text().setText(" Waiting Round Start ...");
                    }else {
                        roundStartToEndModel.getRound_notification_text().setText("Round Closed , But you can still join with the Golden Card .");
                    }

                }
            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    // join closed , use Golden Card
    private void countDownWaiting(){
        long start = Common.Instance(context).formatTimeToMillis(currentTime);
       // long end = Common.Instance(context).formatTimeToMillis(second_join_time+":00");
        long end = Common.Instance(context).formatTimeToMillis("");
        long value = end - start;
        try{
            Toast.makeText(context, "Waiting", Toast.LENGTH_LONG).show();
            final int[] mMinute= {0},mHour = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    countDownAddDealToProduct();
                    if (joinStatus  == 2){
                        // display add offer views
                        roundStartToEndModel.getAddOfferLayout().setVisibility(View.VISIBLE);
                        roundStartToEndModel.getRound_notification_text().setText("Round Started , You can add deal now .");
                    }else {
                        roundStartToEndModel.getAddOfferLayout().setVisibility(View.INVISIBLE);
                        roundStartToEndModel.getRound_notification_text().setText("Round Started , And you out .");
                    }
                }
            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    // add deal to product ( Round Time )
    private void countDownAddDealToProduct(){
        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(second_join_time+":00") + Common.Instance(context).formatTimeToMillis(round_time);
        long value = end - start;
        try{
            Toast.makeText(context, "AddDealToProduct", Toast.LENGTH_LONG).show();
            final int[] mMinute= {0},mHour = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    roundStartToEndModel.getAddOfferLayout().setVisibility(View.INVISIBLE);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    countDownRestTime();
                }

            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    //Rest before show the winner
    private void countDownRestTime() {

        long start = Common.Instance(context).formatTimeToMillis(currentTime);
        long end = Common.Instance(context).formatTimeToMillis(second_join_time+":00") + Common.Instance(context).formatTimeToMillis(round_time)+Common.Instance(context).formatTimeToMillis(rest_time);
        long value = end - start;
        try{
            Toast.makeText(context, "RestTime", Toast.LENGTH_LONG).show();
            final int[] mMinute= {0},mHour = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {
                    setTimeDown(millisUntilFinished);
                }

                public void onFinish() {
                    roundStartToEndModel.getRound_notification_text().setText("Round Finished : Winner Mohamed Abdelhady with deal 550 EGP");
                }

            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }


    }

    private void setTimeDown(long millisUntilFinished){
        Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);


        GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"second");
        gawlaTimeDownSecond.NumberTick(second);


        if (mMinute[0] != minute){
            GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"minute");
            gawlaTimeDownMinute.NumberTick(minute);
        }
        mMinute[0] = minute;

        if (mHour[0] != hour){
            GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"hour");
            gawlaTimeDownMinute.NumberTick(hour);
        }
        mHour[0] = hour;
    }


}
