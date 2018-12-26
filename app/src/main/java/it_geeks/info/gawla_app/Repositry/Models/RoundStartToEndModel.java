package it_geeks.info.gawla_app.Repositry.Models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RoundStartToEndModel {
    private List<ImageView> upDivsList ;
    private List<ImageView> downDivsList ;
    private List<Integer> drawablesUp ;
    private List<Integer> drawablesDown ;
    Button btnJoinRound;
    LinearLayout addOfferLayout;
    TextView round_notification_text;

    public RoundStartToEndModel(List<ImageView> upDivsList, List<ImageView> downDivsList, List<Integer> drawablesUp, List<Integer> drawablesDown,  Button btnJoinRound, LinearLayout addOfferLayout ,TextView round_notification_text) {
        this.upDivsList = upDivsList;
        this.downDivsList = downDivsList;
        this.drawablesUp = drawablesUp;
        this.drawablesDown = drawablesDown;
        this.btnJoinRound = btnJoinRound;
        this.addOfferLayout = addOfferLayout;
        this.round_notification_text = round_notification_text;
    }

    public RoundStartToEndModel() {
    }

    public TextView getRound_notification_text() {
        return round_notification_text;
    }

    public void setRound_notification_text(TextView round_notification_text) {
        this.round_notification_text = round_notification_text;
    }

    public List<ImageView> getUpDivsList() {
        return upDivsList;
    }

    public void setUpDivsList(List<ImageView> upDivsList) {
        this.upDivsList = upDivsList;
    }

    public List<ImageView> getDownDivsList() {
        return downDivsList;
    }

    public void setDownDivsList(List<ImageView> downDivsList) {
        this.downDivsList = downDivsList;
    }

    public List<Integer> getDrawablesUp() {
        return drawablesUp;
    }

    public void setDrawablesUp(List<Integer> drawablesUp) {
        this.drawablesUp = drawablesUp;
    }

    public List<Integer> getDrawablesDown() {
        return drawablesDown;
    }

    public void setDrawablesDown(List<Integer> drawablesDown) {
        this.drawablesDown = drawablesDown;
    }

    public Button getBtnJoinRound() {
        return btnJoinRound;
    }

    public void setBtnJoinRound(Button btnJoinRound) {
        this.btnJoinRound = btnJoinRound;
    }

    public LinearLayout getAddOfferLayout() {
        return addOfferLayout;
    }

    public void setAddOfferLayout(LinearLayout addOfferLayout) {
        this.addOfferLayout = addOfferLayout;
    }
}
