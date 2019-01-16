package it_geeks.info.gawla_app.Repositry;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.General.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;

public class RecentSalonsCallback extends PagedList.BoundaryCallback<Round> {

    private Context context;

    private GawlaDataBse gawlaDataBse;

    private SharedPrefManager sm;

    public RecentSalonsCallback(Context context, GawlaDataBse gawlaDataBse, SharedPrefManager spm) {
        this.context = context;
        this.gawlaDataBse = gawlaDataBse;
        this.sm = spm;
    }

    @Override
    public void onZeroItemsLoaded() {

    }

    @Override
    public void onItemAtEndLoaded(@NonNull Round itemAtEnd) {
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                "getAllSalons", new Request(sm.getUser().getUser_id(), sm.getUser().getApi_token()), new HandleResponses() {
                    @Override
                    public void handleResponseData(JsonObject mainObject) {
                        insertItemsIntoDatabase(mainObject);
                        Toast.makeText(context, "connect", Toast.LENGTH_SHORT).show();
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

    private void insertItemsIntoDatabase(JsonObject mainObj) {
        gawlaDataBse.roundDao().removeRounds(gawlaDataBse.roundDao().getRounds());
        gawlaDataBse.roundDao().insertRoundList(ParseResponses.parseRounds(mainObj, gawlaDataBse));
    }
}