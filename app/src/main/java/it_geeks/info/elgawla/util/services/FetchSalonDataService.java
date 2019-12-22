package it_geeks.info.elgawla.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.views.intro.SplashScreenActivity;
import it_geeks.info.elgawla.views.salon.ClosedSalonActivity;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseSalon;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class FetchSalonDataService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try
        {
            getSalonDataFromServer(intent.getExtras().getInt("id"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void getSalonDataFromServer(int id) {
        RetrofitClient.getInstance(this).fetchDataFromServer(this,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID
                        , SharedPrefManager.getInstance(this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , id
                        , null, null, null, null), new HandleResponses() {

                    @Override
                    public void onServerError() {
                            if (!SplashScreenActivity.splashInstance.isDestroyed())
                                SplashScreenActivity.splashInstance.finish();
                    }

                    @Override
                    public void onTrueResponse(JsonObject mainObject) {
                        Salon salon = parseSalon(mainObject);
                        EventsManager.sendNotificationInteractionEvent(FetchSalonDataService.this, salon.getCategory_name(), String.valueOf(salon.getSalon_id()), salon.getProduct_name());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                        Intent i;
                        if (salon.isClosed())
                        {
                            i = new Intent(FetchSalonDataService.this, ClosedSalonActivity.class);
                        }
                        else
                        {
                            i = new Intent(FetchSalonDataService.this, SalonActivity.class);
                        }
                        i.putExtra(SALON, salon);

                        stackBuilder.addNextIntentWithParentStack(i);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void afterResponse() {
                    }

                    @Override
                    public void onConnectionErrors(String errorMessage) {
                        Toast.makeText(FetchSalonDataService.this, errorMessage, Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                });
    }
}