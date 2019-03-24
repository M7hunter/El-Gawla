package it_geeks.info.gawla_app.repository.RESTful;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.repository.Models.Data;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.RequestMainBody;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.facebook.FacebookSdk.getApplicationContext;
import static it_geeks.info.gawla_app.repository.RESTful.ParseResponses.parseServerErrors;

public class RetrofitClient {

    // it geeks server : https://dev.itgeeks.info/api/v1/en/
    // gawla server : http://elgawla.net/dev/public/api/v1/en/
    // gawla server ip : http://134.209.0.250/dev/public/api/v1/en/

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private Call<JsonObject> call;
    private Context context;
    private int reconnect = 0;

    private RetrofitClient() {
        this.retrofit = new Retrofit.Builder()
                .client(new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).build())
                .baseUrl(selectBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        // on creation || on lang changed
        if (mInstance == null || SharedPrefManager.getInstance(context).isLangChanged()) {
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
                // TODO: check codes instead of strings
//                        case success 200:
//                        case validationErrors 402:
//                        case somethingWrong 100:
//                        case invalidApiToken 111:
//                        case invalidAccessToken 401:
//                        case notAuthorized 402:
//                        case notFound 404:
//                        case authFailed 203:
//                        case emailNotExist 104:
//                        case accountAlreadyVerified 106:
//                        case tokenNotFound 115:
//                        case accountNotConfirmed 116:
//                        case wrongPhoneVerifyNum 405:
//                        case wrongForgetPassVerifyNum 406:
//                        case waitBeforeResend 410:
//                        case doNotHavePermission 412:
//                        case internalServerError 500:
//                        case unKnown 1:
                try {
                    Log.d("response_code:", response.code() + "");
                    switch (response.code()) {
                        case 200:
                            try {
                                JsonObject mainObj = response.body().getAsJsonObject();

                                // dynamic with each call
                                HandleResponses.handleTrueResponse(mainObj);

                            } catch (NullPointerException e) { // errors of response body 'maybe response body has been changed'
                                Log.e("onResponse: ", e.getMessage());
                                Crashlytics.logException(e);
                            } catch (UnsupportedOperationException e) {
                                Log.e("onResponse: ", e.getMessage());
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
                    }

                    if (!response.isSuccessful()) { // code != 200
                        JsonObject errorObj = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String serverError = parseServerErrors(errorObj);

                        // notify user
                        Toast.makeText(context, serverError, Toast.LENGTH_SHORT).show();
                        Log.d("!successful: ", serverError);

                        // dynamic with each call
                        HandleResponses.handleFalseResponse(errorObj);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                } catch (JsonSyntaxException e) {
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
                HandleResponses.handleEmptyResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // connection errors
                if (t.getMessage() != null && !t.getMessage().isEmpty())
                    Log.d("onFailure: ", t.getMessage());
                // dynamic with each call
                HandleResponses.handleConnectionErrors(context.getString(R.string.no_connection));

                // try one more time
                if (t.getMessage().contains("timeout") && reconnect < 1) {
                    reconnect++;
                    call.enqueue(this);
                }
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