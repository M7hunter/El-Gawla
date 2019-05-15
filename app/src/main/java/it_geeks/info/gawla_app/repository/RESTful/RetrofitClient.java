package it_geeks.info.gawla_app.repository.RESTful;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.Data;
import it_geeks.info.gawla_app.repository.Models.RequestMainBody;
import it_geeks.info.gawla_app.views.login.LoginActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getApplicationContext;
import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseServerErrors;

public class RetrofitClient {

    private static final String TAG = "retrofit_connection";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private Call<JsonObject> call;

    private RetrofitClient(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(18, TimeUnit.SECONDS)
                .readTimeout(18, TimeUnit.SECONDS)
                .writeTimeout(18, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(selectBaseUrl(context))
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        // on creation || on lang changed
        if (mInstance == null || SharedPrefManager.getInstance(context).isLangChanged()) {
            mInstance = new RetrofitClient(context);
        }
        return mInstance;
    }

    private String selectBaseUrl(Context context) {
        // it geeks server : https://dev.itgeeks.info/api/v1/en/
        // gawla server : http://elgawla.net/dev/public/api/v1/en/
        // gawla server ip : http://134.209.0.250/dev/public/api/v1/en/

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

    public void executeConnectionToServer(Context context, String action, Request req, HandleResponses HandleResponses) {
        call = getInstance(context).getAPI().request(new RequestMainBody(new Data(action), req));
        call.enqueue(createWebserviceCallback(HandleResponses, context));
    }

    public void getSalonsPerPageFromServer(Context context, Data data, Request req, HandleResponses HandleResponses) {
        call = getInstance(context).getAPI().request(new RequestMainBody(data, req));
        call.enqueue(createWebserviceCallback(HandleResponses, context));
    }

    private APIs getAPI() {
        return retrofit.create(APIs.class);
    }

    private Callback<JsonObject> createWebserviceCallback(final HandleResponses HandleResponses, final Context context) {
        return new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                // TODO: check codes
                /* case unKnown 1:
                   case somethingWrong 100:
                   case emailNotExist 104:
                   case accountAlreadyVerified 106:
                   case invalidApiToken 111:
                   case tokenNotFound 115:
                   case accountNotConfirmed 116:
                   case success 200:
                   case authFailed 203:
                   case invalidAccessToken 401:
                   case validationErrors 402:
                   case notAuthorized 402:
                   case notFound 404:
                   case wrongPhoneVerifyNum 405:
                   case wrongForgetPassVerifyNum 406:
                   case waitBeforeResend 410:
                   case doNotHavePermission 412:
                   case internalServerError 500: */
                try {
                    Log.d(TAG, "response_code: " + response.code());
                    switch (response.code()) {
                        case 200:
                            try {
                                JsonObject mainObj = response.body().getAsJsonObject();

                                // dynamic with each call
                                HandleResponses.handleTrueResponse(mainObj);

                            } catch (NullPointerException e) { // errors of response body 'maybe response body has been changed'
                                e.printStackTrace();
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                Crashlytics.logException(e);
                            } catch (UnsupportedOperationException e) {
                                e.printStackTrace();
                                Log.e(TAG, "onResponse: " + e.getMessage());
                                Crashlytics.logException(e);
                            }

                            break;
                        case 203:
                            context.startActivity(new Intent(context, LoginActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            SharedPrefManager.getInstance(context).clearUser();
                            SharedPrefManager.getInstance(context).clearProvider();
                            LoginManager.getInstance().logOut();

                            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();
                            if (mGoogleApiClient.isConnected()) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                mGoogleApiClient.disconnect();
                                mGoogleApiClient.connect();
                            }

                            break;
                        default: // code != 200
                            try {
                                JsonObject errorObj = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                String serverError = parseServerErrors(errorObj);
                                if (serverError.isEmpty()) {
                                    serverError = context.getString(R.string.error_occurred);
                                }

                                // notify user
                                Toast.makeText(context, serverError, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onResponse!successful: " + serverError);

                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                Crashlytics.logException(e);
                                Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "JsonSyntaxException: " + e.getMessage());
                            }
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

                // dynamic with each call
                HandleResponses.handleAfterResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // connection errors
                if (t.getMessage() != null && !t.getMessage().isEmpty())
                    Log.d(TAG, "onFailure: " + t.getMessage());
                // dynamic with each call
                HandleResponses.handleConnectionErrors(context.getString(R.string.no_connection));
            }
        };
    }

    public void cancelCall() {
        if (call != null) {
            if (!call.isCanceled()) {
                call.cancel();
            }
        }
    }
}