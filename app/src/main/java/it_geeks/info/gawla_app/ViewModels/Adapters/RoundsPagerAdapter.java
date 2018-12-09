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

        tvProductName.setText(round.getProduct_name());
        tvProductCategory.setText(round.getProduct_category());
        tvProductPrice.setText(round.getProduct_price());
        tvProductDescription.setText(round.getProduct_description());
        tvStartTime.setText(round.getStart_time());
        tvEndTime.setText(round.getEnd_time());

        // events
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SalonActivity.class);
                i.putExtra("product_name", round.getProduct_name());
                i.putExtra("product_image", round.getProduct_image());
                i.putExtra("product_category", round.getProduct_category());
                i.putExtra("product_price", round.getProduct_price());
                i.putExtra("product_description", round.getProduct_description());
                i.putExtra("round_start_time", round.getStart_time());
                i.putExtra("round_end_time", round.getEnd_time());
                i.putExtra("joined_members_number", round.getJoined_members_number());

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
