package it_geeks.info.elgawla.repository.Models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HalvesModel {

    private Context context;
    private View parent;

    private List<ImageView> upperViewsList = new ArrayList<>();
    private List<ImageView> lowerViewsList = new ArrayList<>();
    private List<Integer> upperDrawablesResList = new ArrayList<>();
    private List<Integer> lowerDrawablesResList = new ArrayList<>();

    public HalvesModel(Context context, View parent) {
        this.context = context;
        this.parent = parent;
        initHalves();
    }

    private void initHalves() {
        for (int i = 1; i <= 12; i++) { // init halves ids lists
            upperViewsList.add((ImageView) parent.findViewById(context.getResources().getIdentifier("div_up" + i, "id", context.getPackageName())));
            lowerViewsList.add((ImageView) parent.findViewById(context.getResources().getIdentifier("div_down" + i, "id", context.getPackageName())));
        }
        for (int i = 0; i < 10; i++) { // init halves drawables lists
            upperDrawablesResList.add(context.getResources().getIdentifier("digit_" + i + "_upper", "drawable", context.getPackageName()));
            lowerDrawablesResList.add(context.getResources().getIdentifier("digit_" + i + "_lower", "drawable", context.getPackageName()));
        }
    }

    public List<ImageView> getUpperViewsList() {
        return upperViewsList;
    }

    public List<ImageView> getLowerViewsList() {
        return lowerViewsList;
    }

    public List<Integer> getUpperDrawablesResList() {
        return upperDrawablesResList;
    }

    public List<Integer> getLowerDrawablesResList() {
        return lowerDrawablesResList;
    }
}
