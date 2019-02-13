package it_geeks.info.gawla_app.Repositry.Services.fcm;


import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;

public class UpdateFirebaseToken {

    Context context;

    public UpdateFirebaseToken(Context context) {
        this.context = context;
        new uFirebaseToken().execute();
    }

        class uFirebaseToken extends AsyncTask<Void, Void, Void> {

            public uFirebaseToken() {
            }

            @Override
            protected Void doInBackground(Void... voids) {

                int user_id = SharedPrefManager.getInstance(context).getUser().getUser_id();
                String apiToken = SharedPrefManager.getInstance(context).getUser().getApi_token();

                if (!String.valueOf(user_id).isEmpty() && !apiToken.isEmpty()) {
                    RetrofitClient.getInstance(context).executeConnectionToServer(context, "setUserFirebaseToken", new Request(user_id, apiToken, FirebaseInstanceId.getInstance().getInstanceId().toString(), 0), new HandleResponses() {
                        @Override
                        public void handleTrueResponse(JsonObject mainObject) {
                        }

                        @Override
                        public void handleFalseResponse(JsonObject errorObject) {

                        }

                        @Override
                        public void handleEmptyResponse() {

                        }

                        @Override
                        public void handleConnectionErrors(String errorMessage) {
                        }
                    });
                }

                return null;
            }

        }

}
