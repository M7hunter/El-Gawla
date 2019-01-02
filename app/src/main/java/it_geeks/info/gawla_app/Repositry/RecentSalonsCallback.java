package it_geeks.info.gawla_app.Repositry;

import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.APIs;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecentSalonsCallback extends PagedList.BoundaryCallback<Round> {

    private APIs apIs;

    private GawlaDataBse gawlaDataBse;

    private SharedPrefManager sm;

    private Context context;

    public RecentSalonsCallback(Context context, APIs apIs, GawlaDataBse gawlaDataBse, SharedPrefManager spm) {
        this.context = context;
        this.apIs = apIs;
        this.gawlaDataBse = gawlaDataBse;
        this.sm = spm;
    }

    @Override
    public void onZeroItemsLoaded() {
        apIs.request(new RequestMainBody(new Data("getAllSalons"), new Request(sm.getUser().getUser_id(), sm.getUser().getApi_token())))
                .enqueue(createWebserviceCallback());
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Round itemAtEnd) {
        apIs.request(new RequestMainBody(new Data("getAllSalons"), new Request(sm.getUser().getUser_id(), sm.getUser().getApi_token())))
                .enqueue(createWebserviceCallback());
    }

    private Callback<JsonObject> createWebserviceCallback() {
        return new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonObject mainObj = response.body().getAsJsonObject();
                        boolean status = mainObj.get("status").getAsBoolean();

                        if (status) { // no errors
                            insertItemsIntoDatabase(mainObj);
                        } else {
                            Toast.makeText(context, ParseResponses.parseServerErrors(mainObj), Toast.LENGTH_SHORT).show();
                        }

                    } catch (NullPointerException e) { // errors of response body
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
            }
        };
    }

    private void insertItemsIntoDatabase(JsonObject mainObj) {
        gawlaDataBse.roundDao().insertRoundList(ParseResponses.parseRounds(mainObj, gawlaDataBse));
    }
}