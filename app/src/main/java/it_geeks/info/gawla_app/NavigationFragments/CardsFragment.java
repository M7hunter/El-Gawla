package it_geeks.info.gawla_app.NavigationFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Models.Card;
import it_geeks.info.gawla_app.Models.Data;
import it_geeks.info.gawla_app.Models.Request;
import it_geeks.info.gawla_app.Models.RequestMainBody;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.RESTful.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardsFragment extends Fragment {

    Card greenCard, redCard, goldenCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        initViews(view);

//        getData(view);

        return view;
    }

    private void getData(View view) {

        RequestMainBody requestMainBody = new RequestMainBody(
                new Data("getCardByUserID"),
                new Request(
                        SharedPrefManager.getInstance(getContext()).getUser().getUser_id(),
                        Common.Instance(getContext()).removeQuotes(SharedPrefManager.getInstance(getContext()).getUser().getApi_token())));

        Call<JsonObject> call = RetrofitClient.getInstance().getAPI().request(requestMainBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject object = response.body().getAsJsonObject();
                    boolean status = object.get("status").getAsBoolean();

                    if (status) {
                        // notify user
                        Toast.makeText(getContext(), object.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                        // save user data locally
                        handleServerResponse(object);

                    } else {
                        // notify user
                        Toast.makeText(getContext(), handleServerErrors(object), Toast.LENGTH_SHORT).show();
                    }

                } catch (NullPointerException e) {
                    // notify user
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleServerResponse(JsonObject object) {
        JsonArray dataArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < dataArray.size(); i++) {
            JsonObject cardObj = dataArray.get(i).getAsJsonObject();
            String card_name = cardObj.get("card_name").getAsString();
            String card_category = cardObj.get("card_category").getAsString();
            String type = cardObj.get("type").getAsString();
            String color_code = cardObj.get("color_code").getAsString();
            String cost = cardObj.get("cost").getAsString();

            switch (type) {
                case "green":
                    greenCard = new Card(card_name, card_category, type, color_code, cost);
                    break;
                case "red":
                    redCard = new Card(card_name, card_category, type, color_code, cost);
                    break;
                case "golden":
                    goldenCard = new Card(card_name, card_category, type, color_code, cost);
                    break;
            }
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

    private void initViews(View view) {
        TextView tvGreenCardStatus, tvRedCardStatus, tvGoldenCardStatus, tvGreenCardCount, tvRedCardCount, tvGoldenCardCount;

        tvGreenCardStatus = view.findViewById(R.id.green_card_status);
        tvGreenCardCount = view.findViewById(R.id.green_card_count);
        tvRedCardStatus = view.findViewById(R.id.red_card_status);
        tvRedCardCount = view.findViewById(R.id.red_card_count);
        tvGoldenCardStatus = view.findViewById(R.id.golden_card_status);
        tvGoldenCardCount = view.findViewById(R.id.golden_card_count);

//        tvGreenCardStatus.setText();
//        tvGreenCardCount.setText();
//        tvRedCardStatus.setText();
//        tvRedCardCount.setText();
//        tvGoldenCardStatus.setText();
//        tvGoldenCardCount.setText();
    }
}
