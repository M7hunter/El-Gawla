package it_geeks.info.gawla_app.Repositry;

import android.arch.paging.PagedList;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.APIs;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;

public class RecentSalonsCallback extends PagedList.BoundaryCallback<Round> {

    private Context context;

    private APIs apIs;

    private GawlaDataBse gawlaDataBse;

    private SharedPrefManager sm;

    public RecentSalonsCallback(Context context, APIs apIs, GawlaDataBse gawlaDataBse, SharedPrefManager spm) {
        this.context = context;
        this.apIs = apIs;
        this.gawlaDataBse = gawlaDataBse;
        this.sm = spm;
    }

    @Override
    public void onZeroItemsLoaded() {
        RetrofitClient.getInstance(context).executeConnectionToServer("getAllSalons", new Request(sm.getUser().getUser_id(), sm.getUser().getApi_token()), new HandleResponses() {
            @Override
            public void handleResponseData(JsonObject mainObject) {
                insertItemsIntoDatabase(mainObject);
            }

            @Override
            public void handleEmptyResponse() {

            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemAtEndLoaded(@NonNull Round itemAtEnd) {
        onZeroItemsLoaded();
    }

    private void insertItemsIntoDatabase(JsonObject mainObj) {
        gawlaDataBse.roundDao().insertRoundList(ParseResponses.parseRounds(mainObj, gawlaDataBse));
    }
}