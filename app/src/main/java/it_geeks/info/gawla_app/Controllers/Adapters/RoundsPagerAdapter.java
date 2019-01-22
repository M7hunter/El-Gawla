package it_geeks.info.gawla_app.Controllers.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.views.SalonActivity;
import it_geeks.info.gawla_app.Repositry.Models.Round;
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
        final Round round = roundsList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_round, container, false);

        // define views
        TextView tvProductName, tvProductCategory, tvStartTime, btnJoinRound;
        ImageView imgProductImage;

        // init views
        imgProductImage = view.findViewById(R.id.my_round_product_image);
        tvProductName = view.findViewById(R.id.my_round_product_name);
        tvProductCategory = view.findViewById(R.id.my_round_product_category);
        tvStartTime = view.findViewById(R.id.my_round_start_time);
        btnJoinRound = view.findViewById(R.id.my_round_btn_enter);
        RecyclerView cardsRecycler = view.findViewById(R.id.my_round_cards_recycler); // nested recycler
        cardsRecycler.setHasFixedSize(true);
        cardsRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        // set data
        Picasso.with(context).load(round.getProduct_image()).placeholder(R.drawable.placeholder).into(imgProductImage);
        tvProductName.setText(Common.Instance(context).removeEmptyLines(round.getProduct_name()));
        tvProductCategory.setText(Common.Instance(context).removeEmptyLines(round.getCategory_name()));
        tvStartTime.setText(Common.Instance(context).removeEmptyLines(round.getRound_start_time()));
        cardsRecycler.setAdapter(new SalonCardsAdapter(context, GawlaDataBse.getGawlaDatabase(context).cardDao().getCardsById(round.getSalon_id())));
        Common.Instance(context).changeDrawableViewColor(tvProductCategory, round.getCategory_color());

        // open round page
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SalonActivity.class);
                // send round's data to round page
                i.putExtra("product_id", round.getProduct_id());
                i.putExtra("salon_id", round.getSalon_id());
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("category_name", round.getCategory_name());
                i.putExtra("category_color", round.getCategory_color());
                i.putExtra("country_name", round.getCountry_name());
                i.putExtra("product_commercial_price", round.getProduct_commercial_price());
                i.putExtra("product_product_description", round.getProduct_product_description());
                i.putExtra("product_image", round.getProduct_image());
                i.putExtra("round_start_time", round.getRound_start_time());
                i.putExtra("round_end_time", round.getRound_end_time());
                i.putExtra("first_join_time", round.getFirst_join_time());
                i.putExtra("second_join_time", round.getSecond_join_time());
                i.putExtra("round_date", round.getRound_date());
                i.putExtra("round_time", round.getRound_time());
                i.putExtra("rest_time", round.getRest_time());

                context.startActivity(i);
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
