package it_geeks.info.gawla_app.repository.Models;

import android.widget.ImageView;

import java.util.List;

public class RoundStartToEndModel {

    private List<ImageView> upDivsList;
    private List<ImageView> downDivsList;
    private List<Integer> drawablesUp;
    private List<Integer> drawablesDown;

    public RoundStartToEndModel(List<ImageView> upDivsList, List<ImageView> downDivsList, List<Integer> drawablesUp, List<Integer> drawablesDown) {
        this.upDivsList = upDivsList;
        this.downDivsList = downDivsList;
        this.drawablesUp = drawablesUp;
        this.drawablesDown = drawablesDown;
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
}
