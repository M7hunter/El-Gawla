package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import it_geeks.info.gawla_app.General.PicassoClint;
import it_geeks.info.gawla_app.HaleActivity;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.R;

public class RoundsPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Round> roundsList;
    TextView tvProductName, tvProductCategory, tvProductPrice, tvProductDescription, tvStartTime, tvEndTime, tvMembersNumber, btnJoinRound;
    ImageView imgProductImage;

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
        Round round = roundsList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_round, container, false);

        // init views
        imgProductImage = view.findViewById(R.id.my_round_product_image);
        tvProductName = view.findViewById(R.id.my_round_product_name);
        tvProductCategory = view.findViewById(R.id.my_round_product_category);
        tvProductPrice = view.findViewById(R.id.my_round_product_price);
        tvProductDescription = view.findViewById(R.id.my_round_product_description);
        tvStartTime = view.findViewById(R.id.my_round_start_time);
        tvEndTime = view.findViewById(R.id.my_round_end_time);
        tvMembersNumber = view.findViewById(R.id.my_round_members_number);
        btnJoinRound = view.findViewById(R.id.my_round_btn_enter);

        // set data
        tvProductName.setText(round.getProduct_name());
        tvProductCategory.setText(round.getProduct_category());
        tvProductPrice.setText(round.getProduct_price());
        tvProductDescription.setText(round.getProduct_description());
        tvStartTime.setText(round.getStart_time());
        tvEndTime.setText(round.getEnd_time());
        tvMembersNumber.setText(round.getJoined_members_number());
        PicassoClint.downloadImage(context,round.getProduct_image(),imgProductImage);

        // events
        btnJoinRound.setOnClickListener(new View.OnClickListener() {
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
