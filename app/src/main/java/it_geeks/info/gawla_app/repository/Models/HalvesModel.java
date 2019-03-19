package it_geeks.info.gawla_app.repository.Models;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class HalvesModel {

    private Context context;
    private View parent;

    private List<ImageView> upperHalvesIdsList = new ArrayList<>();
    private List<ImageView> lowerHalvesIdsList = new ArrayList<>();
    private List<Integer> upperHalvesDrawablesList = new ArrayList<>();
    private List<Integer> lowerHalvesDrawablesList = new ArrayList<>();

    public HalvesModel(Context context, View parent) {
        this.context = context;
        this.parent = parent;
        initHalves();
    }

    private void initHalves() {
        for (int i = 1; i <= 12; i++) { // init halves ids lists
            upperHalvesIdsList.add((ImageView) parent.findViewById(context.getResources().getIdentifier("div_up" + i, "id", context.getPackageName())));
            lowerHalvesIdsList.add((ImageView) parent.findViewById(context.getResources().getIdentifier("div_down" + i, "id", context.getPackageName())));
        }
        for (int i = 0; i < 12; i++) { // init halves drawables lists
            upperHalvesDrawablesList.add(context.getResources().getIdentifier("digit_" + i + "_upper", "drawable", context.getPackageName()));
            lowerHalvesDrawablesList.add(context.getResources().getIdentifier("digit_" + i + "_lower", "drawable", context.getPackageName()));
        }
    }

    public List<ImageView> getUpperHalvesIdsList() {
        return upperHalvesIdsList;
    }

    public List<ImageView> getLowerHalvesIdsList() {
        return lowerHalvesIdsList;
    }

    public List<Integer> getUpperHalvesDrawablesList() {
        return upperHalvesDrawablesList;
    }

    public List<Integer> getLowerHalvesDrawablesList() {
        return lowerHalvesDrawablesList;
    }
}
