package it_geeks.info.gawla_app.Repositry.RESTful;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses.parseServerErrors;

public class RetrofitClient {

    // locale : http://192.168.1.2/elgawla/public/api/v1/en/
    // it geeks server : https://dev.itgeeks.info/api/v1/en/
    // gawla server : http://elgawla.net/dev/public/api/v1/en/
    // gawla server ip : http://134.209.0.250/dev/public/api/v1/en/

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private Context context;

    private RetrofitClient() {
        this.retrofit = new Retrofit.Builder()
                .client(new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).build())
                .baseUrl(selectBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        if (mInstance == null) { // on creation
            mInstance = new RetrofitClient();

        } else if (!mInstance.selectBaseUrl().equals(SharedPrefManager.getInstance(context).getSavedLang())) { // on lang changed
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    private String selectBaseUrl() {
        String BASE_URL;
        switch (SharedPrefManager.getInstance(context).getSavedLang()) {
            case "en":
                BASE_URL = "http://elgawla.net/dev/public/api/v1/en/";
                break;
            case "ar":
                BASE_URL = "http://elgawla.net/dev/public/api/v1/ar/";
                break;
            default:
                BASE_URL = "http://elgawla.net/dev/public/api/v1/en/";
                break;
        }

        return BASE_URL;
    }

    public void executeConnectionToServer(Context context, String action, Request request, HandleResponses HandleResponses) {
        getInstance(context).getAPI().request(new RequestMainBody(new Data(action), request)).enqueue(createWebserviceCallback(HandleResponses, context));
    }

    public void getSalonsPerPageFromServer(Context context, Data data, Request request, HandleResponses HandleResponses) {
        getInstance(context).getAPI().request(new RequestMainBody(data, request)).enqueue(createWebserviceCallback(HandleResponses, context));
    }


    private APIs getAPI() {
        return retrofit.create(APIs.class);
    }

    private Callback<JsonObject> createWebserviceCallback(final HandleResponses HandleResponses, final Context context) {
        return new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) { // code == 200
//                    try {
                    JsonObject mainObj = response.body().getAsJsonObject();

                    HandleResponses.handleTrueResponse(mainObj);

//                    } catch (NullPointerException e) { // errors of response body 'maybe response body has changed'
//                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
                } else { // code != 200
                    try {
                        JsonObject errorObj = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();

                        Toast.makeText(context, parseServerErrors(errorObj), Toast.LENGTH_SHORT).show();
                        HandleResponses.handleFalseResponse(errorObj);

                        // TODO: check codes instead of strings
                        if (parseServerErrors(errorObj).contains("not logged in") || parseServerErrors(errorObj).contains("api token")) {
                            context.startActivity(new Intent(context, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                            SharedPrefManager.getInstance(context).clearUser();
                        }

                    } catch (IOException e) { // errors of error body
                        e.printStackTrace();
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                HandleResponses.handleEmptyResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // connection errors
                HandleResponses.handleConnectionErrors(context.getString(R.string.no_connection));
            }
        };
    }
}
