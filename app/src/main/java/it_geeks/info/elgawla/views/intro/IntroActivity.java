package it_geeks.info.elgawla.views.intro;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import it_geeks.info.elgawla.Adapters.SliderAdapter;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.views.BaseActivity;
import it_geeks.info.elgawla.views.signing.EnterPhoneActivity;
import it_geeks.info.elgawla.views.signing.SignInActivity;
import it_geeks.info.elgawla.R;

import static it_geeks.info.elgawla.util.Constants.PREVIOUS_PAGE_KEY;

public class IntroActivity extends BaseActivity {

    private ViewPager2 mViewPager;
    private LinearLayout mDotLayout;
    private Button btnSkip, btnNext;
    private ImageButton ibPrevious;
    private TextView tvSignIn, tvSignUp, dot1, dot2, dot3, dots[];
    private View introSignBar;

    private int mCurrentPage;
    public static boolean settingPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPrefManager.getInstance(this).setLang(SharedPrefManager.getInstance(this).getSavedLang());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initViews();

        initPager();

        handleEvents();
    }

    private void initViews() {
        btnSkip = findViewById(R.id.btn_intro_skip);
        ibPrevious = findViewById(R.id.ib_intro_previous);
        btnNext = findViewById(R.id.btn_intro_next);
        tvSignIn = findViewById(R.id.tv_intro_sign_in);
        tvSignUp = findViewById(R.id.tv_intro_sign_up);
        introSignBar = findViewById(R.id.intro_sign_bar);

        if (settingPage) introSignBar.setVisibility(View.GONE);
    }

    private void initPager() {
        mViewPager = findViewById(R.id.intro_view_pager);
        mViewPager.setAdapter(new SliderAdapter(IntroActivity.this));
        mDotLayout = findViewById(R.id.dots);

        initDots();
        updateDots(0);
    }

    private void handleEvents() {
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, SignInActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(IntroActivity.this, EnterPhoneActivity.class);
                i.putExtra(PREVIOUS_PAGE_KEY, IntroActivity.this.getClass().getSimpleName());
                startActivity(i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                updateDots(position);
                mCurrentPage = position;
                if (position == 0)
                {
                    ibPrevious.setVisibility(View.INVISIBLE);
                    btnNext.setText(R.string.next);
                }
                else if (position == 1)
                {
                    ibPrevious.setVisibility(View.VISIBLE);
                    btnNext.setText(R.string.next);
                }
                else if (position == 2)
                {
                    ibPrevious.setVisibility(View.VISIBLE);
                    btnNext.setText(R.string.finish);
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextPage();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentPage >= 2) openNextPage();

                mViewPager.setCurrentItem(mCurrentPage + 1);
            }
        });

        ibPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCurrentPage - 1);
            }
        });
    }

    public void openNextPage() {
        if (settingPage)
        {
            settingPage = false;
            finish();
        }
        else
        {
            startActivity(new Intent(IntroActivity.this, SignInActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void initDots() {
        dot1 = new TextView(this);
        dot2 = new TextView(this);
        dot3 = new TextView(this);
        dots = new TextView[]{dot1, dot2, dot3};

        for (TextView dot : dots)
        {
            dot.setText(Html.fromHtml("&#8226"));
            dot.setTextSize(20);
            mDotLayout.addView(dot);
        }
    }


    public void updateDots(int position) {
        for (TextView dot : dots)
        {
            dot.setTextColor(getResources().getColor(R.color.dots));
        }
        dots[position].setTextColor(getResources().getColor(R.color.babyBlue));
    }
}
