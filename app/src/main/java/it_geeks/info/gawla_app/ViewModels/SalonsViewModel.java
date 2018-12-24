package it_geeks.info.gawla_app.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import it_geeks.info.gawla_app.General.Common;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Views.LoginActivities.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalonsViewModel extends AndroidViewModel {

    private GawlaDataBse gawlaDataBse;

    private LiveData<PagedList<Round>> roundsList;

    public SalonsViewModel(@NonNull Application application) {
        super(application);

        gawlaDataBse = GawlaDataBse.getGawlaDatabase(getApplication());
    }

    private void getRoundsFromServer() {
        int userId = SharedPrefManager.getInstance(getApplication()).getUser().getUser_id();
        String apiToken = Common.Instance(getApplication()).removeQuotes(SharedPrefManager.getInstance(getApplication()).getUser().getApi_token());
//        long lastRequest = SharedPrefManager.getInstance(getApplication()).getLastRequest();

        RetrofitClient.getInstance(getApplication()).getAPI()
                .request(new RequestMainBody(
                        new Data("getAllSalons", 0, 1),
                        new Request(userId, apiToken)))
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                        try {
                            JsonObject mainObj = response.body().getAsJsonObject();
                            boolean status = mainObj.get("status").getAsBoolean();

                            if (status) { // no errors

//                                SharedPrefManager.getInstance(getApplication()).setLastRequest(Common.Instance(getApplication()).getCurrentTimeInMillis()); // update last request
                                gawlaDataBse.roundDao().removeRounds(gawlaDataBse.roundDao().getRounds()); // remove old list
                                gawlaDataBse.roundDao().insertRoundList(handleServerResponse(mainObj)); // add new list

                            } else { // errors from server
                                if (handleServerErrors(mainObj).equals("you are not logged in.")) {
                                    getApplication().startActivity(new Intent(getApplication(), LoginActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                                    SharedPrefManager.getInstance(getApplication()).clearUser();
                                }

                                Toast.makeText(getApplication(), handleServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                            }
                        } catch (NullPointerException e) { // errors of response body
                            Toast.makeText(getApplication(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) { // errors of connection
                        Toast.makeText(getApplication(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<Round> handleServerResponse(JsonObject object) {
        List<Round> rounds = new ArrayList<>();
        JsonArray roundsArray = object.get("data").getAsJsonArray();

        for (int i = 0; i < roundsArray.size(); i++) {
            JsonObject roundObj = roundsArray.get(i).getAsJsonObject();
            int product_id = roundObj.get("product_id").getAsInt();
            int salon_id = roundObj.get("salon_id").getAsInt();
            String product_name = roundObj.get("product_name").getAsString();
            String category_name = roundObj.get("category_name").getAsString();
            String category_color = roundObj.get("category_color").getAsString();
            String country_name = roundObj.get("country_name").getAsString();
            String product_commercial_price = roundObj.get("product_commercial_price").getAsString();
            String product_product_description = roundObj.get("product_description").getAsString();
            String product_image = roundObj.get("product_image").getAsString();
            String round_start_time = roundObj.get("round_start_time").getAsString();
            String round_end_time = roundObj.get("round_end_time").getAsString();
            String first_join_time = roundObj.get("first_join_time").getAsString();
            String second_join_time = roundObj.get("second_join_time").getAsString();
            String round_date = roundObj.get("round_date").getAsString();
            String round_time = roundObj.get("round_time").getAsString();
            String rest_time = roundObj.get("rest_time").getAsString();

            // save product images in locale storage
            gawlaDataBse.productImageDao().removeSubImages(gawlaDataBse.productImageDao().getSubImagesById(i));
            gawlaDataBse.productImageDao().insertSubImages(handleImages(roundObj, i));

            // save product cards in locale storage
            gawlaDataBse.cardDao().removeCards(gawlaDataBse.cardDao().getCardsById(salon_id));
            gawlaDataBse.cardDao().insertCards(handleCards(roundObj, salon_id));

            rounds.add(
                    new Round(product_id,
                            salon_id,
                            product_name,
                            category_name,
                            category_color,
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

    private List<ProductSubImage> handleImages(JsonObject roundObj, int product_id) {
        JsonArray product_images = roundObj.get("product_images").getAsJsonArray();

        List<ProductSubImage> subImagesList = new ArrayList<>();

        for (int i = 0; i < product_images.size(); i++) {
            ProductSubImage subImage = new ProductSubImage(product_id, product_images.get(i).getAsString());
            subImagesList.add(subImage);
        }

        return subImagesList;
    }

    private List<Card> handleCards(JsonObject roundObj, int salon_id) {
        JsonArray salon_cards = roundObj.get("salon_cards").getAsJsonArray();

        List<Card> salon_cardsList = new ArrayList<>();

        for (int j = 0; j < salon_cards.size(); j++) {
            JsonObject cardObj = salon_cards.get(j).getAsJsonObject();
//            int card_id = cardObj.get("id").getAsInt();
            String card_name = cardObj.get("name").getAsString();
            String card_details = cardObj.get("details").getAsString();
            String card_type = cardObj.get("type").getAsString();
            String card_color = cardObj.get("color").getAsString();
            String card_cost = cardObj.get("cost").getAsString();

            salon_cardsList.add(new Card(salon_id, card_name, card_details, card_type, card_color, card_cost));
        }

        return salon_cardsList;
    }

    private String handleServerErrors(JsonObject object) {
        String error = "no errors";
        JsonArray errors = object.get("errors").getAsJsonArray();
        for (int i = 0; i < errors.size(); i++) {
            error = errors.get(i).getAsString();
        }
        return error;
    }

    public void init() {
        getRoundsFromServer(); // refresh list
        DataSource.Factory<Integer, Round> factory = gawlaDataBse.roundDao().getRoundsPaged();

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setPrefetchDistance(2)
                .setPageSize(2)
                .build();

        roundsList = new LivePagedListBuilder<>(factory, config).build();
    }

    public LiveData<PagedList<Round>> getRoundsList() {
        if (gawlaDataBse.roundDao().getRounds().size() == 0) {
            getRoundsFromServer();
        }

        return roundsList;
    }
}
