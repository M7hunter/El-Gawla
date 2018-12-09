package it_geeks.info.gawla_app.ViewModels.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Views.LoginActivities.SignInActivity;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.SplashActivities.IntroActivity;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater LayoutInFlateR;
    public int[] slide_Images ;
    String[] slide_headings , slide_descs;
    TextView txt_Splat_Skip;
    IntroActivity onboardActivity = new IntroActivity();

    public SliderAdapter(Context context) {
        this.context = context;
          slide_Images = new int[]{
                  R.drawable.onbord1,
                  R.drawable.onbord2,
                  R.drawable.onbord3
          };

         slide_headings = new String[]{
                 context.getResources().getString(R.string.slide_heading1),
                 context.getResources().getString(R.string.slide_heading2),
                 context.getResources().getString(R.string.slide_heading3)
         };

         slide_descs = new String[]{
                 context.getResources().getString(R.string.slide_descs1),
                 context.getResources().getString(R.string.slide_descs2),
                 context.getResources().getString(R.string.slide_descs3)
         };
    }

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        LayoutInFlateR = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = LayoutInFlateR.inflate(R.layout.slide_layout,container,false);

        txt_Splat_Skip = v.findViewById(R.id.txt_splash_skip);
        ImageView slideImageView = v.findViewById(R.id.slide_Image);
        TextView slideHeading = v.findViewById(R.id.slide_Heading);
        TextView slideDesc = v.findViewById(R.id.slide_Desc);

        slideImageView.setImageResource(slide_Images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDesc.setText(slide_descs[position]);
        txt_Splat_Skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            context.startActivity(new Intent(context,SignInActivity.class));
            onboardActivity.finish();
            new SharedPrefManager(context).Intro_Save("1");
            }
        });

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
