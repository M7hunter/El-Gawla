package it_geeks.info.gawla_app.Repositry.RESTful;

import android.content.Context;
import android.widget.Toast;

import it_geeks.info.gawla_app.General.SharedPrefManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // locale : http://192.168.1.2/elgawla/public/api/v1/en/
    // online : http://dev.itgeeks.info/api/v1/en/
    private static String BASE_URL = "http://dev.itgeeks.info/api/v1/en/";

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
        if (mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    private String selectBaseUrl() {
        switch (SharedPrefManager.getInstance(context).getSavedLang()) {
            case "en":
                BASE_URL = "http://dev.itgeeks.info/api/v1/en/";
                break;
            case "ar":
                BASE_URL = "http://dev.itgeeks.info/api/v1/ar/";
                break;
            default:
                BASE_URL = "http://dev.itgeeks.info/api/v1/en/";
                break;
        }

        return BASE_URL;
    }

    public APIs getAPI() {
        return retrofit.create(APIs.class);
    }
}
