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


    private RelativeLayout div_up1, div_down1, div_up2, div_down2, div_up3, div_down3, div_up4, div_down4;
    private TextView Num_Up1 , Num_down1, Num_Up2, Num_down2 ,Num_Up3 , Num_down3, Num_Up4, Num_down4;

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
      //  if (typeOnTime == "second") {
            this.context = context;
            this.div_up1 = upDivsList.get(1);
            this.div_down1 = downDivsList.get(1);
            this.div_up2 = upDivsList.get(2);
            this.div_down2 = downDivsList.get(2);
            this.div_up3 = upDivsList.get(3);
            this.div_down3 = downDivsList.get(3);
            this.div_up4 = upDivsList.get(4);
            this.div_down4 = downDivsList.get(4);

            this.Num_Up1 = upNumList.get(1);
            this.Num_down1 = downNumList.get(1);
            this.Num_Up2 = upNumList.get(2);
            this.Num_down2 = downNumList.get(2);
            this.Num_Up3 = upNumList.get(3);
            this.Num_down3 = downNumList.get(3);
            this.Num_Up4 = upNumList.get(4);
            this.Num_down4 = downNumList.get(4);
      //  }
    }

    // tick number
    public void NumberTick(long millisUntilFinished) {
        int num = (int) millisUntilFinished ;
        String m = String.valueOf(num);
        if (num == 59){
            Num_down3.setText(5+"");
            Num_Up3.setText(5+"");
            Num_Up4.setText(5+"");
            Num_down4.setText(5+"");
        }
        if (num == 0 || num == 10 || num == 20 || num == 30 || num == 40 || num == 50) {
            int n = 0;
            this.numNext = 9;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 1 || num == 11 || num == 21 || num == 31 || num == 41 || num == 51) {
            int n = 1;
            this.numNext = 0;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 2 || num == 12 || num == 22 || num == 32 || num == 42 || num == 52) {
            int n = 2;
            this.numNext = 1;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 3 || num == 13 || num == 23 || num == 33 || num == 43 || num == 53) {
            int n = 3;
            this.numNext = 2;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 4 || num == 14 || num == 24 || num == 34 || num == 44 || num == 54) {
            int n = 4;
            this.numNext = 3;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 5 || num == 15 || num == 25 || num == 35 || num == 45 || num == 55) {
            int n = 5;
            this.numNext = 4;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 6 || num == 16 || num == 26 || num == 36 || num == 46 || num == 56) {
            int n = 6;
            this.numNext = 5;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 7 || num == 17 || num == 27 || num == 37 || num == 47 || num == 57) {
            int n = 7;
            this.numNext = 6;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 8 || num == 18 || num == 28 || num == 38 || num == 48 || num == 58) {
            int n = 8;
            this.numNext = 7;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");

        }
        if (num == 9 || num == 19 || num == 29 || num == 39 || num == 49 || num == 59) {
            int n = 9;
            this.numNext = 8;
            anmi(Integer.parseInt(m),n);
            Num_Up1.setText(numNext+"");
            Num_Up2.setText(n+"");
            Num_down2.setText(numNext+"");
        }

        ///////////////

        if (num == 10 ) {
            int n = 1;
            this.numNextTens = 0;
            anmi(Integer.parseInt(m),n);
            Num_Up3.setText(numNextTens+"");
            Num_Up4.setText(n+"");
            Num_down4.setText(numNextTens+"");

        }
        if (num == 20) {
            int n = 2;
            this.numNextTens = 1;
            anmi(Integer.parseInt(m),n);
            Num_Up3.setText(numNextTens+"");
            Num_Up4.setText(n+"");
            Num_down4.setText(numNextTens+"");

        }
        if (num == 30) {
            int n = 3;
            this.numNextTens = 2;
            anmi(Integer.parseInt(m),n);
            Num_Up3.setText(numNextTens+"");
            Num_Up4.setText(n+"");
            Num_down4.setText(numNextTens+"");

        }
        if (num == 40) {
            int n = 4;
            this.numNextTens = 3;
            anmi(Integer.parseInt(m),n);
            Num_Up3.setText(numNextTens+"");
            Num_Up4.setText(n+"");
            Num_down4.setText(numNextTens+"");

        }
        if (num == 50) {
            int n = 5;
            this.numNextTens = 4;
            anmi(Integer.parseInt(m),n);
            Num_Up3.setText(numNextTens+"");
            Num_Up4.setText(n+"");
            Num_down4.setText(numNextTens+"");
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
            div_up2.setVisibility(View.VISIBLE);
            div_up2.startAnimation(animation1);

        }

    }
    // anmie for tens number
    private void tensanmi() {
        animation3 = AnimationUtils.loadAnimation(context, R.anim.flip_point_to_middle);
        animation3.setAnimationListener(this);
        div_up4.setVisibility(View.VISIBLE);
        div_up4.startAnimation(animation3);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation1){
            div_up2.setVisibility(View.INVISIBLE);
            div_down2.setVisibility(View.VISIBLE);
            animation2 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation2.setAnimationListener(this);
            div_down2.startAnimation(animation2);

        }else if (animation == animation2){
            div_down2.setVisibility(View.INVISIBLE);
            Num_down1.setText(this.numNext+"");

        }

        if (animation == animation3){
            div_up4.setVisibility(View.INVISIBLE);
            div_down4.setVisibility(View.VISIBLE);
            animation4 = AnimationUtils.loadAnimation(context, R.anim.flip_point_from_middle);
            animation4.setAnimationListener(this);
            div_down4.startAnimation(animation4);

        }else if(animation == animation4){
            div_down4.setVisibility(View.INVISIBLE);
            Num_down3.setText(this.numNextTens+"");
            Num_Up3.setText(this.numNextTens+"");
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
