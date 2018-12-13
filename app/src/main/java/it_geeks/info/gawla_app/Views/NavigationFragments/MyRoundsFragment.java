package it_geeks.info.gawla_app.Views.NavigationFragments;

import android.content.Intent;
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

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.ViewModels.Adapters.RoundsPagerAdapter;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import it_geeks.info.gawla_app.Views.MainActivity;
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
        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();

        RequestMainBody requestMainBody = new RequestMainBody(
                new Data("getSalonByUserID"),
                new Request(userId, apiToken));

        Call<JsonObject> call = RetrofitClient.getInstance(getContext()).getAPI().request(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject mainObj = response.body().getAsJsonObject();
                    boolean status = mainObj.get("status").getAsBoolean();

                    if (status) { // no errors

                        roundsList.addAll(handleServerResponse(mainObj));

                        initPager(view);

                        handleEvents(roundsList.size());

                    } else { // errors from server
                        if (handleServerErrors(mainObj).equals("you are not logged in.")) {
                            startActivity(new Intent(getContext(), LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            SharedPrefManager.getInstance(getActivity()).clearUser();
                        }

                        Toast.makeText(MainActivity.mainActivityInstance, handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) { // errors of response body
                    Toast.makeText(MainActivity.mainActivityInstance, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                initEmptyView(view);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // errors of connection
                Toast.makeText(MainActivity.mainActivityInstance, t.getMessage(), Toast.LENGTH_SHORT).show();
                initEmptyView(view);
            }
        });
    }

    private List<Round> handleServerResponse(JsonObject object) {
        List<Round> rounds = new ArrayList<>();
        JsonArray roundsArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            String product_name = roundObj.get("product_name").getAsString();
            String category_name = roundObj.get("category_name").getAsString();
            String country_name = roundObj.get("country_name").getAsString();
            String product_commercial_price = roundObj.get("product_commercial_price").getAsString();
            String product_product_description = roundObj.get("product_product_description").getAsString();
            String product_image = roundObj.get("product_image").getAsString();
            String round_start_time = roundObj.get("round_start_time").getAsString();
            String round_end_time = roundObj.get("round_end_time").getAsString();
            String first_join_time = roundObj.get("first_join_time").getAsString();
            String second_join_time = roundObj.get("second_join_time").getAsString();
            String round_date = roundObj.get("round_date").getAsString();
            String round_time = roundObj.get("round_time").getAsString();
            String rest_time = roundObj.get("rest_time").getAsString();

            rounds.add(
                    new Round(product_name,
                            category_name,
                            country_name,
                            product_commercial_price,
                            product_product_description,
                            product_image,
                            round_start_time,
                            round_end_time,
                            first_join_time,
                            second_join_time,
                            round_date,
                            round_time,
                            rest_time));
        }

        return rounds;
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

        // set arrows ui & remove pager progress
        roundsViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                myRoundProgress.setVisibility(View.GONE);
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
