package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.RoundsPagerAdapter;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.R;

public class MyRoundsFragment extends Fragment {

    private ViewPager roundsViewPager;
    private List<Round> roundsList = new ArrayList<>();

    private ImageView arrowRight, arrowLeft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rounds, container, false);

        getData();

        initViews(view);

        initEmptyView(view);

        return view;
    }

    private void getData() {
        // fake data
        for (int i = 0; i < 5; i++) {
            Round round = new Round("samsung A" + i + " 201" + i
                    , "image" + i
                    , "Electronics"
                    , i + "0000 L.E"
                    , "this should be description"
                    , "14 hr 40 min"
                    , "8 hr 40 min"
                    , "2" + i + " member joined");

            roundsList.add(round);
        }
    }

    private void initViews(View view) {
        // pager
        roundsViewPager = view.findViewById(R.id.rounds_pager);
        roundsViewPager.setAdapter(new RoundsPagerAdapter(getActivity(), roundsList));

        // arrows
        arrowRight = view.findViewById(R.id.my_rounds_right_arrow);
        arrowLeft = view.findViewById(R.id.my_rounds_left_arrow);

        // clicks
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundsViewPager.setCurrentItem(roundsViewPager.getCurrentItem() - 1, true);
            }
        });

        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                roundsViewPager.setCurrentItem(roundsViewPager.getCurrentItem() + 1, true);
            }
        });

        // at the beginning
        arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);

        // to set arrows ui
        roundsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i == roundsViewPager.getAdapter().getCount() - 1) {
                    arrowRight.setImageResource(R.drawable.ic_arrow_right_grey);
                } else if (i == 0) {
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);
                } else {
                    arrowRight.setImageResource(R.drawable.ic_arrow_right);
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left);
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initEmptyView(View view) {
        LinearLayout emptyViewLayout = view.findViewById(R.id.my_rounds_empty_view);

        if (roundsList.size() > 0) {
            emptyViewLayout.setVisibility(View.INVISIBLE);
            roundsViewPager.setVisibility(View.VISIBLE);
        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            roundsViewPager.setVisibility(View.INVISIBLE);
        }
    }
}
