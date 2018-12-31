package it_geeks.info.gawla_app.Repositry.RESTful;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // locale : http://192.168.1.2/elgawla/public/api/v1/en/
    // online : https://dev.itgeeks.info/api/v1/en/

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private Context context;

    private RetrofitClient() {
        this.retrofit = new Retrofit.Builder()
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
                BASE_URL = "https://dev.itgeeks.info/api/v1/en/";
                break;
            case "ar":
                BASE_URL = "https://dev.itgeeks.info/api/v1/ar/";
                break;
            default:
                BASE_URL = "https://dev.itgeeks.info/api/v1/en/";
                break;
        }

        return BASE_URL;
    }

    public APIs getAPI() {
        return retrofit.create(APIs.class);
    }
}
