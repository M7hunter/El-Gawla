package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.RoundsPagerAdapter;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Models.Data;
import it_geeks.info.gawla_app.Models.Request;
import it_geeks.info.gawla_app.Models.RequestMainBody;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRoundsFragment extends Fragment {

    private ViewPager roundsViewPager;
    private List<Round> roundsList = new ArrayList<>();

    private ProgressBar myRoundProgress;

    private ImageView arrowRight, arrowLeft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rounds, container, false);

        getData(view);

        initPager(view);

        return view;
    }

    private void getData(final View view) {
        String apiToken =SharedPrefManager.getInstance(getContext()).getUser().getApi_token();
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();

        RequestMainBody requestMainBody = new RequestMainBody(new Data("getSalonByUserID"), new Request(userId, apiToken));
        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().getSalons(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject ObjData = response.body().getAsJsonObject();
                    boolean status = ObjData.get("status").getAsBoolean();

                    if (status) {
                        JsonArray roundsArray = ObjData.get("data").getAsJsonArray();
                        for (int i = 0; i < roundsArray.size(); i++) {
                            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
                            String product_name = roundObj.get("product_name").getAsString();
                            String category_name = roundObj.get("category_name").getAsString();
                            String product_commercial_price = roundObj.get("product_commercial_price").getAsString();
                            String product_product_description = roundObj.get("product_product_description").getAsString();
                            String product_image = roundObj.get("product_image").getAsString();
                            String round_start_time = roundObj.get("round_start_time").getAsString();
                            String round_end_time = roundObj.get("round_end_time").getAsString();

                            Round round = new Round(
                                    product_name
                                    , product_image
                                    , category_name
                                    , product_commercial_price
                                    , product_product_description
                                    , round_start_time
                                    , round_end_time
                                    , "2" + i + " member joined");

                            roundsList.add(round);
                        }

                        initPager(view);

                        handleEvents(roundsList.size());

                    } else {
                        Toast.makeText(getActivity(), handleServerErrors(ObjData), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                initEmptyView(view);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                initEmptyView(view);
            }
        });


    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    private void initPager(View view) {
        myRoundProgress = view.findViewById(R.id.my_rounds_progress);

        // pager
        roundsViewPager = view.findViewById(R.id.rounds_pager);
        roundsViewPager.setAdapter(new RoundsPagerAdapter(getActivity(), roundsList));

        // to remove pager progress
        if (roundsViewPager.getViewTreeObserver().isAlive()) {
            myRoundProgress.setVisibility(View.GONE);
        }

        // arrows
        arrowRight = view.findViewById(R.id.my_rounds_right_arrow);
        arrowLeft = view.findViewById(R.id.my_rounds_left_arrow);
    }

    private void handleEvents(int cardsCount) {
        // at the beginning
        arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);

        if (cardsCount > 1) {
            arrowRight.setImageResource(R.drawable.ic_arrow_right);
        }

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

        // set arrows ui
        roundsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == roundsViewPager.getAdapter().getCount() - 1) {
                    arrowRight.setImageResource(R.drawable.ic_arrow_right_grey);
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left);
                } else if (i == 0) {
                    arrowLeft.setImageResource(R.drawable.ic_arrow_left_grey);
                    arrowRight.setImageResource(R.drawable.ic_arrow_right);
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
            arrowLeft.setVisibility(View.VISIBLE);
            arrowRight.setVisibility(View.VISIBLE);
        } else {
            emptyViewLayout.setVisibility(View.VISIBLE);
            roundsViewPager.setVisibility(View.INVISIBLE);
            arrowLeft.setVisibility(View.INVISIBLE);
            arrowRight.setVisibility(View.INVISIBLE);
        }
    }
}
