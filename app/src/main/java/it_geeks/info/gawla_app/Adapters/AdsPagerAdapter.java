package it_geeks.info.gawla_app.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Models.Ad;

public class AdsPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Ad> adList;

    public AdsPagerAdapter(Context context, List<Ad> adList) {
        this.context = context;
        this.adList = adList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_ad, container, false);

        Ad ad = adList.get(position);

        ImageView ivAdImage = itemView.findViewById(R.id.iv_ad_image);
        TextView tvAdTitle = itemView.findViewById(R.id.tv_ad_title);
        TextView tvAdBody = itemView.findViewById(R.id.tv_ad_body);

            Picasso.with(context)
                    .load(ad.getImage())
                    .placeholder(R.drawable.flodillus)
                    .into(ivAdImage);

            tvAdTitle.setText(ad.getTitle());
            tvAdBody.setText(ad.getBody());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public int getCount() {
        return adList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((LinearLayout)object);
    }
}
