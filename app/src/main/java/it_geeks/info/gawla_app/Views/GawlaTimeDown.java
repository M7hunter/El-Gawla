package it_geeks.info.gawla_app.Views;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import it_geeks.info.gawla_app.R;

public class GawlaTimeDown implements Animation.AnimationListener{

    private List<RelativeLayout> upDivsList = new ArrayList<>();
    private List<RelativeLayout> downDivsList = new ArrayList<>();
    private List<TextView> upNumList = new ArrayList<>();
    private List<TextView> downNumList = new ArrayList<>();

    private Animation animation1;
    private Animation animation2;
    private Animation animation3;
    private Animation animation4;
    private int numNext;
    private int numNextTens;

    Context context;

    public GawlaTimeDown(Context context,List<RelativeLayout> upDivsList,List<RelativeLayout> downDivsList,List<TextView> upNumList,List<TextView> downNumList,String typeOnTime) {
        this.context = context;

        if (typeOnTime == "second") {
            for (int i = 0; i < 4 ; i++) {
                this.upDivsList.add(upDivsList.get(i));
                this.downDivsList.add(downDivsList.get(i));
                this.upNumList.add(upNumList.get(i));
                this.downNumList.add(downNumList.get(i));
            }
        }else if (typeOnTime == "minute"){
            for (int i = 4; i < 8 ; i++) {
                this.upDivsList.add(upDivsList.get(i));
                this.downDivsList.add(downDivsList.get(i));
                this.upNumList.add(upNumList.get(i));
                this.downNumList.add(downNumList.get(i));
            }
        }else if (typeOnTime == "hour") {
                for (int i = 8; i < 12; i++) {
                    this.upDivsList.add(upDivsList.get(i));
                    this.downDivsList.add(downDivsList.get(i));
                    this.upNumList.add(upNumList.get(i));
                    this.downNumList.add(downNumList.get(i));
                }
            }
    }

    // tick number
    public void NumberTick(long millisUntilFinished) {
        int num = (int) millisUntilFinished ;
        String m = String.valueOf(num);
        if (num == 59){
            downNumList.get(2).setText(5+"");
            upNumList.get(2).setText(5+"");
            upNumList.get(3).setText(5+"");
            downNumList.get(3).setText(5+"");
        }
        if (num == 0 || num == 10 || num == 20 || num == 30 || num == 40 || num == 50) {
            int n = 0;
            this.numNext = 9;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 1 || num == 11 || num == 21 || num == 31 || num == 41 || num == 51) {
            int n = 1;
            this.numNext = 0;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 2 || num == 12 || num == 22 || num == 32 || num == 42 || num == 52) {
            int n = 2;
            this.numNext = 1;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 3 || num == 13 || num == 23 || num == 33 || num == 43 || num == 53) {
            int n = 3;
            this.numNext = 2;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 4 || num == 14 || num == 24 || num == 34 || num == 44 || num == 54) {
            int n = 4;
            this.numNext = 3;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 5 || num == 15 || num == 25 || num == 35 || num == 45 || num == 55) {
            int n = 5;
            this.numNext = 4;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 6 || num == 16 || num == 26 || num == 36 || num == 46 || num == 56) {
            int n = 6;
            this.numNext = 5;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 7 || num == 17 || num == 27 || num == 37 || num == 47 || num == 57) {
            int n = 7;
            this.numNext = 6;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 8 || num == 18 || num == 28 || num == 38 || num == 48 || num == 58) {
            int n = 8;
            this.numNext = 7;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");

        }
        if (num == 9 || num == 19 || num == 29 || num == 39 || num == 49 || num == 59) {
            int n = 9;
            this.numNext = 8;
            anmi(Integer.parseInt(m),n);
            upNumList.get(0).setText(numNext+"");
            upNumList.get(1).setText(n+"");
            downNumList.get(1).setText(numNext+"");
        }

        ///////////////

        if (num == 10 ) {
            int n = 1;
            this.numNextTens = 0;
            anmi(Integer.parseInt(m),n);
            upNumList.get(2).setText(numNextTens+"");
            upNumList.get(3).setText(n+"");
            downNumList.get(3).setText(numNextTens+"");

        }
        if (num == 20) {
            int n = 2;
            this.numNextTens = 1;
            anmi(Integer.parseInt(m),n);
            upNumList.get(2).setText(numNextTens+"");
            upNumList.get(3).setText(n+"");
            downNumList.get(3).setText(numNextTens+"");

        }
        if (num == 30) {
            int n = 3;
            this.numNextTens = 2;
            anmi(Integer.parseInt(m),n);
            upNumList.get(2).setText(numNextTens+"");
            upNumList.get(3).setText(n+"");
            downNumList.get(3).setText(numNextTens+"");

        }
        if (num == 40) {
            int n = 4;
            this.numNextTens = 3;
            anmi(Integer.parseInt(m),n);
            upNumList.get(2).setText(numNextTens+"");
            upNumList.get(3).setText(n+"");
            downNumList.get(3).setText(numNextTens+"");

        }
        if (num == 50) {
            int n = 5;
            this.numNextTens = 4;
            anmi(Integer.parseInt(m),n);
            upNumList.get(2).setText(numNextTens+"");
            upNumList.get(3).setText(n+"");
            downNumList.get(3).setText(numNextTens+"");
        }

    }

    // anmie for basice number
    private void anmi(int n,int singleNum) {

        if (n == 10) {
            tensanmi();
        }else
        if (n == 20) {
            tensanmi();
        }else
        if (n == 30) {
            tensanmi();
        }else
        if (n == 40) {
            tensanmi();
        }else
        if (n == 50) {
            tensanmi();
        }
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
        if (animation == animation1){
            upDivsList.get(1).setVisibility(View.INVISIBLE);
            downDivsList.get(1).setVisibility(View.VISIBLE);
            animation2 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation2.setAnimationListener(this);
            downDivsList.get(1).startAnimation(animation2);

        }else if (animation == animation2){
            downDivsList.get(1).setVisibility(View.INVISIBLE);
            downNumList.get(0).setText(this.numNext+"");

        }

        if (animation == animation3){
            upDivsList.get(3).setVisibility(View.INVISIBLE);
            downDivsList.get(3).setVisibility(View.VISIBLE);
            animation4 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation4.setAnimationListener(this);
            downDivsList.get(3).startAnimation(animation4);

        }else if(animation == animation4){
            downDivsList.get(3).setVisibility(View.INVISIBLE);
            downNumList.get(2).setText(this.numNextTens+"");
            upNumList.get(2).setText(this.numNextTens+"");
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
