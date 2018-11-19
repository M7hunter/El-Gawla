package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import it_geeks.info.gawla_app.HaleActivity;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.R;

public class RoundsPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Round> roundsList;

    public RoundsPagerAdapter(Context context, List<Round> roundsList) {
        this.context = context;
        this.roundsList = roundsList;
    }

    @Override
    public int getCount() {
        return roundsList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return o == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_round, container, false);

        // init views
        Button btnEnter = view.findViewById(R.id.my_round_btn_enter);

        // events
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, HaleActivity.class));
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View) object);
    }
}
