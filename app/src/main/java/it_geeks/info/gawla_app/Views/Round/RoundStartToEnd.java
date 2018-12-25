package it_geeks.info.gawla_app.Views.Round;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import java.util.Calendar;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.Repositry.Models.RoundStartToEndModel;

public class RoundStartToEnd {
    
    Context context;
    RoundStartToEndModel roundStartToEndModel;
    CountDownTimer countDownTimer;
    int joinStatus ;
    public RoundStartToEnd(Context context, RoundStartToEndModel roundStartToEndModel) {
        this.context = context;
        this.roundStartToEndModel = roundStartToEndModel;
    }
    public void start(){
        long start = Common.Instance(context).formatTimeToMillis("01:10:00");
        long end = Common.Instance(context).formatTimeToMillis("01:43:30");
        long value = end - start;
        countDownBeforRoundStart(value);
    }

    public void stop(){
        countDownTimer.cancel();
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
    }

    // before round start and open joine
    private void countDownBeforRoundStart(long value) {
        try{
            final int[] mMinute= {0},mHour = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {

                    Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    Log.d("mo7", "doSecond: " + hour +" : "+ minute + " : " + second);

                    GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"second");
                    gawlaTimeDownSecond.NumberTick(second);


                    if (mMinute[0] != minute){
                        GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"minute");
                        gawlaTimeDownMinute.NumberTick(minute);
                    }
                    mMinute[0] = minute;

                }

                public void onFinish() {
                    roundStartToEndModel.getRound_notification_text().setText("Round Opened , You can Joine Now .");
                    roundStartToEndModel.getBtnJoinRound().setVisibility(View.VISIBLE);
                    countDownGoldenCard();
                }

            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    // joine Round Opened
    private void countDownGoldenCard(){
        long start = Common.Instance(context).formatTimeToMillis("01:30:00");
        long end = Common.Instance(context).formatTimeToMillis("01:30:30");
        long value = end - start;
        try{
            final int[] mMinute = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {

                    Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

              //      Log.d("mo7", "doSecond: " + hour +" : "+ minute + " : " + second);

                    GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"second");
                    gawlaTimeDownSecond.NumberTick(second);


                    if (mMinute[0] != minute){
                        GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"minute");
                        gawlaTimeDownMinute.NumberTick(minute);
                    }
                    mMinute[0] = minute;

                }

                public void onFinish() {
                    roundStartToEndModel.getBtnJoinRound().setVisibility(View.INVISIBLE);
                    countDownWating();
                    if (joinStatus == 2){
                        roundStartToEndModel.getRound_notification_text().setText(" Waite Round Start ...");
                    }else {
                        roundStartToEndModel.getRound_notification_text().setText("Round Closed , But you can still joine with the Golden Card .");
                    }

                }
            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

    // joine closed , use Golden Card
    private void countDownWating(){
        long start = Common.Instance(context).formatTimeToMillis("01:30:00");
        long end = Common.Instance(context).formatTimeToMillis("01:30:30");
        long value = end - start;
        try{
            final int[] mMinute = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {

                    Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

               //     Log.d("mo7", "doSecond: " + hour +" : "+ minute + " : " + second);

                    GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"second");
                    gawlaTimeDownSecond.NumberTick(second);


                    if (mMinute[0] != minute){
                        GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"minute");
                        gawlaTimeDownMinute.NumberTick(minute);
                    }
                    mMinute[0] = minute;

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

    // add deal to product
    private void countDownAddDealToProduct(){
        long start = Common.Instance(context).formatTimeToMillis("01:30:00");
        long end = Common.Instance(context).formatTimeToMillis("01:30:30");
        long value = end - start;
        try{
            final int[] mMinute = {0};
            countDownTimer = new CountDownTimer(value, 1000) {
                public void onTick(final long millisUntilFinished) {

                    Calendar calendar = Common.Instance(context).formatMillisToTime(millisUntilFinished);
                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

              //      Log.d("mo7", "doSecond: " + hour +" : "+ minute + " : " + second);

                    GawlaTimeDown gawlaTimeDownSecond = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"second");
                    gawlaTimeDownSecond.NumberTick(second);


                    if (mMinute[0] != minute){
                        GawlaTimeDown gawlaTimeDownMinute = new GawlaTimeDown(context,roundStartToEndModel.getUpDivsList(),roundStartToEndModel.getDownDivsList(),roundStartToEndModel.getDrawablesUp(),roundStartToEndModel.getDrawablesDown(),"minute");
                        gawlaTimeDownMinute.NumberTick(minute);
                    }
                    mMinute[0] = minute;

                }

                public void onFinish() {
                    roundStartToEndModel.getAddOfferLayout().setVisibility(View.INVISIBLE);
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    roundStartToEndModel.getRound_notification_text().setText("Round Finished : Winner Mohamed Abdelhady with deal 550 EGP");
                }

            }.start();
        }catch (Exception e){
            Log.e("Mo7",e.getMessage());
        }

    }

}
