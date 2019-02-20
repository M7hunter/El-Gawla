package it_geeks.info.gawla_app.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it_geeks.info.gawla_app.Controllers.Adapters.ActivityAdapter;
import it_geeks.info.gawla_app.Controllers.Adapters.TopTenAdapter;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Activity;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.TopTen;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class SalonActivitiesActivity extends AppCompatActivity {

    private RecyclerView activityRecycler;
    private List<Activity> activityList = new ArrayList<>();
    private List<TopTen> topTenList = new ArrayList<>();

    private CardView loadingCard;
    private TextView tvActivityHeader, tvTopTenHeader;

    private int salon_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon_activities);

        getData(savedInstanceState);

        initViews();

        getDataFromServer();

        handleEvents();
    }

    private void getData(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                salon_id = extras.getInt("salon_id");
            }
        } else {
            salon_id = savedInstanceState.getInt("salon_id");
        }
    }

    private void initViews() {
        loadingCard = findViewById(R.id.loading_card);
        activityRecycler = findViewById(R.id.salon_activity_recycler);
        tvActivityHeader = findViewById(R.id.tv_activity_header);
        tvTopTenHeader = findViewById(R.id.tv_top_ten_header);
    }

    private void getDataFromServer() {
        displayLoading();

        int user_id = SharedPrefManager.getInstance(this).getUser().getUser_id();
        String api_token = SharedPrefManager.getInstance(this).getUser().getApi_token();

        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getSalonActivity", new Request(user_id, api_token, salon_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                activityList.addAll(ParseResponses.parseSalonActivity(mainObject));

                initActivityRecycler();
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
            }
        });

        RetrofitClient.getInstance(this).executeConnectionToServer(this, "getTopTen", new Request(user_id, api_token, salon_id), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                topTenList.addAll(ParseResponses.parseTopTen(mainObject));
            }

            @Override
            public void handleFalseResponse(JsonObject errorObject) {

            }

            @Override
            public void handleEmptyResponse() {
                hideLoading();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                hideLoading();
            }
        });

    }

    private void displayLoading() {
        loadingCard.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideLoading() {
        loadingCard.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void handleEvents() {
        // back
        findViewById(R.id.salon_activity_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // goto notification page
        findViewById(R.id.Notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SalonActivitiesActivity.this, NotificationActivity.class));
            }
        });

        tvActivityHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initActivityRecycler();

                tvActivityHeader.setBackground(getResources().getDrawable(R.drawable.bg_rounded_corners_bordered_white_pale_grey));
                tvTopTenHeader.setBackground(getResources().getDrawable(R.drawable.bg_rounded_corners_pale_grey));
            }
        });

        tvTopTenHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTopTenRecycler();

                tvActivityHeader.setBackground(getResources().getDrawable(R.drawable.bg_rounded_corners_pale_grey));
                tvTopTenHeader.setBackground(getResources().getDrawable(R.drawable.bg_rounded_corners_bordered_white_pale_grey));
            }
        });
    }

    private void initActivityRecycler() {
        activityRecycler.setHasFixedSize(true);
        activityRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        activityRecycler.setAdapter(new ActivityAdapter(activityList));
    }

    private void initTopTenRecycler() {
        activityRecycler.setHasFixedSize(true);
        activityRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        activityRecycler.setAdapter(new TopTenAdapter(topTenList));
    }
}
