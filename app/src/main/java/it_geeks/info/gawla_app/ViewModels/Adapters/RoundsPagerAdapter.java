package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.Views.SalonActivity;
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
        TextView tvProductName, tvProductCategory, tvProductPrice, tvProductDescription, tvStartTime, tvEndTime, btnJoinRound;
        ImageView imgProductImage;

        // init views
        imgProductImage = view.findViewById(R.id.my_round_product_image);
        tvProductName = view.findViewById(R.id.my_round_product_name);
        tvProductCategory = view.findViewById(R.id.my_round_product_category);
        tvProductPrice = view.findViewById(R.id.my_round_product_price);
        tvProductDescription = view.findViewById(R.id.my_round_product_description);
        tvStartTime = view.findViewById(R.id.my_round_start_time);
        tvEndTime = view.findViewById(R.id.my_round_end_time);
        btnJoinRound = view.findViewById(R.id.my_round_btn_enter);

        // set data
        Picasso.with(context).load(round.getProduct_image()).placeholder(R.mipmap.ic_launcher_gawla).into(imgProductImage);

        tvProductName.setText(adjustStrings(round).getProduct_name());
        tvProductCategory.setText(adjustStrings(round).getCategory_name());
        tvProductPrice.setText(adjustStrings(round).getProduct_commercial_price());
        tvProductDescription.setText(adjustStrings(round).getProduct_product_description());
        tvStartTime.setText(adjustStrings(round).getRound_start_time());
        tvEndTime.setText(adjustStrings(round).getRound_end_time());

        // open round page
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SalonActivity.class);
                // send round's data to round page
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("category_name", round.getCategory_name());
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

    private Round adjustStrings(Round round) { // to remove unwanted empty spaces || lines
        String adjustedProductName = round.getProduct_name().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductImage = round.getProduct_image().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductCategory = round.getCategory_name().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductPrice = round.getProduct_commercial_price().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductDescription = round.getProduct_product_description().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductStart = round.getRound_start_time().replaceAll("(?m)^[ \t]*\r?\n", "");
        String adjustedProductEnd = round.getRound_end_time().replaceAll("(?m)^[ \t]*\r?\n", "");

        return new Round(adjustedProductName,
                adjustedProductCategory,
                round.getCountry_name(),
                adjustedProductPrice,
                adjustedProductDescription,
                adjustedProductImage,
                adjustedProductStart,
                adjustedProductEnd,
                round.getFirst_join_time(),
                round.getSecond_join_time(),
                round.getRound_date(),
                round.getRound_time(),
                round.getRest_time()
        );
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View) object);
    }

}
