package it_geeks.info.elgawla.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.google.gson.JsonObject;

import androidx.annotation.Nullable;
import androidx.core.app.TaskStackBuilder;
import it_geeks.info.elgawla.repository.Models.Salon;
import it_geeks.info.elgawla.repository.RESTful.HandleResponses;
import it_geeks.info.elgawla.repository.RESTful.RequestModel;
import it_geeks.info.elgawla.repository.RESTful.RetrofitClient;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.util.EventsManager;
import it_geeks.info.elgawla.views.salon.SalonActivity;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseRoundByID;
import static it_geeks.info.elgawla.util.Constants.REQ_GET_SALON_BY_ID;
import static it_geeks.info.elgawla.util.Constants.SALON;

public class GetSalonDataService extends Service {

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
        RetrofitClient.getInstance(this).executeConnectionToServer(this,
                REQ_GET_SALON_BY_ID, new RequestModel<>(REQ_GET_SALON_BY_ID
                        , SharedPrefManager.getInstance(this).getUser().getUser_id()
                        , SharedPrefManager.getInstance(this).getUser().getApi_token()
                        , id
                        , null, null, null, null), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        Salon salon = parseRoundByID(mainObject);
                        EventsManager.sendNotificationInteractionEvent(GetSalonDataService.this, salon.getCategory_name(), String.valueOf(salon.getSalon_id()), salon.getProduct_name());
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

                        Intent i = new Intent(GetSalonDataService.this, SalonActivity.class);
                        i.putExtra(SALON, salon);

                        stackBuilder.addNextIntentWithParentStack(i);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void handleAfterResponse() {
                        stopSelf();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Toast.makeText(GetSalonDataService.this, errorMessage, Toast.LENGTH_LONG).show();
                        stopSelf();
                    }
                });
    }
}