package it_geeks.info.gawla_app.views.splashActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import it_geeks.info.gawla_app.Controllers.Adapters.SliderAdapter;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.R;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private LinearLayout mDotLayout;
    private TextView[] mDots;
    TextView txtback, txtnext;
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----------------full screen-----------------//
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //--------------------------------------------//
        setContentView(R.layout.activity_intro);

        txtback = findViewById(R.id.txt_back);
        txtnext = findViewById(R.id.txt_next);
        mViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dots);
        SliderAdapter sliderAdapter = new SliderAdapter(IntroActivity.this);
        mViewPager.setAdapter(sliderAdapter);

        addDots(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                addDots(i);
                mCurrentPage = i;
                if (i == 0) {
                    txtback.setVisibility(View.INVISIBLE);
                    txtnext.setText(R.string.next);
                } else if (i == 1) {
                    txtback.setVisibility(View.VISIBLE);
                    txtnext.setText(R.string.next);
                    txtback.setText(R.string.back);
                } else if (i == 2) {
                    txtback.setVisibility(View.VISIBLE);
                    txtnext.setText(R.string.finish);
                    txtback.setText(R.string.back);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        txtnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage >= 2) {
                    openNextPage();
                }
                mViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });
        txtback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });

    }

    public void openNextPage() {
        startActivity(new Intent(IntroActivity.this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void addDots(int position) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(25);
            mDots[i].setTextColor(getResources().getColor(R.color.dots));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.babyBlue));
        }
    }
}
