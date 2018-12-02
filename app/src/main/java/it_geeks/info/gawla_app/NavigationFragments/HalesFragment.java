package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.Adapters.RecentHalesAdapter;
import it_geeks.info.gawla_app.Adapters.WinnersNewsAdapter;
import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Models.Data;
import it_geeks.info.gawla_app.Models.Request;
import it_geeks.info.gawla_app.Models.RequestMainBody;
import it_geeks.info.gawla_app.Models.Round;
import it_geeks.info.gawla_app.Models.WinnerNews;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HalesFragment extends Fragment {

    private RecyclerView recentHalesRecycler;
    private RecyclerView winnersNewsRecycler;
    private RecentHalesAdapter recentHalesAdapter;
    private WinnersNewsAdapter winnersNewsAdapter;

    private ProgressBar recentHalesProgress;
    private ProgressBar winnersNewsProgress;

    private List<Round> roundsList = new ArrayList<>();
    private List<WinnerNews> winnerNewsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hales, container, false);

        initViews(view);

        getData(view);

        initWinnersRecycler(view);

        return view;
    }

    private void initViews(View view) {
        recentHalesProgress = view.findViewById(R.id.recent_hales_progress);
        winnersNewsProgress = view.findViewById(R.id.winners_news_progress);
    }

    private void getData(final View view) {

        String apiToken = Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token());
        int userId = SharedPrefManager.getInstance(getContext()).getUser().getUser_id();

        RequestMainBody requestMainBody = new RequestMainBody(
                new Data("getAllSalons"), new Request(userId, apiToken));

        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().Salons(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

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
                                , "not yet");

                        roundsList.add(round);

                        initHalesRecycler(view);

                    } // end of get Salons loop
                } else {
                    Toast.makeText(getActivity(), handleServerErrors(ObjData), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        // fake Data
        for (int i = 0; i < 5; i++) {
            WinnerNews winnerNews = new WinnerNews("title " + i
                    , "body " + i
                    , "not yet");

            winnerNewsList.add(winnerNews);
        }
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    private void initHalesRecycler(View view) {
        recentHalesRecycler = view.findViewById(R.id.recent_hales_recycler);
        recentHalesRecycler.setHasFixedSize(true);
        recentHalesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 0, false));
        recentHalesAdapter = new RecentHalesAdapter(getActivity(), roundsList);
        recentHalesRecycler.setAdapter(recentHalesAdapter);

        // to remove progress bar
        recentHalesRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                recentHalesProgress.setVisibility(View.GONE);
                recentHalesRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initWinnersRecycler(View view) {
        winnersNewsRecycler = view.findViewById(R.id.winners_news_recycler);
        winnersNewsRecycler.setHasFixedSize(true);
        winnersNewsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), 1, false));
        winnersNewsAdapter = new WinnersNewsAdapter(getActivity(), winnerNewsList);
        winnersNewsRecycler.setAdapter(winnersNewsAdapter);

        // to remove progress bar
        winnersNewsRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                winnersNewsProgress.setVisibility(View.GONE);
                winnersNewsRecycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
