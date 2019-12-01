package it_geeks.info.elgawla.repository.RESTful;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.concurrent.TimeUnit;

import it_geeks.info.elgawla.BuildConfig;
import it_geeks.info.elgawla.R;
import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;
import it_geeks.info.elgawla.repository.Models.Data;
import it_geeks.info.elgawla.repository.Models.RequestMainBody;
import it_geeks.info.elgawla.util.Common;
import it_geeks.info.elgawla.util.DialogBuilder;
import it_geeks.info.elgawla.util.Interfaces.ClickInterface;
import it_geeks.info.elgawla.views.account.MembershipActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static it_geeks.info.elgawla.repository.RESTful.ParseResponses.parseServerErrors;

public class RetrofitClient {

    private static final String TAG = "retrofit_connection";

    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private DialogBuilder dialogBuilder;

    private Call<JsonObject> call;

    private RetrofitClient(Context context) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(18, TimeUnit.SECONDS)
                .writeTimeout(18, TimeUnit.SECONDS)
                .readTimeout(18, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.SERVER_URL + SharedPrefManager.getInstance(context).getSavedLang() + "/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context) {
        // on creation || on lang changed
        if (mInstance == null || SharedPrefManager.getInstance(context).isLangChanged())
        {
            mInstance = new RetrofitClient(context);
        }
        return mInstance;
    }

    public void fetchDataFromServer(Context context, String action, RequestModel req, HandleResponses HandleResponses) {
        call = getInstance(context).getAPI().request(new RequestMainBody(new Data(action), req));
        call.enqueue(createWebserviceCallback(HandleResponses, context));
    }

    public void fetchDataPerPageFromServer(Context context, Data data, RequestModel req, HandleResponses HandleResponses) {
        call = getInstance(context).getAPI().request(new RequestMainBody(data, req));
        call.enqueue(createWebserviceCallback(HandleResponses, context));
    }

    private APIs getAPI() {
        return retrofit.create(APIs.class);
    }

    private Callback<JsonObject> createWebserviceCallback(final HandleResponses handleResponses, final Context context) {
        return new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                /*
                 * --> review codes
                 *   case unKnown 1:
                 *   case somethingWrong 100:
                 *   case emailNotExist 104:
                 *   case accountAlreadyVerified 106:
                 *   case invalidApiToken 111:
                 *   case tokenNotFound 115:
                 *   case accountNotConfirmed 116:
                 *   case success 200:
                 *   case authFailed 203:
                 *   case invalidAccessToken 401:
                 *   case validationErrors 402:
                 *   case notAuthorized 402:
                 *   case notFound 404:
                 *   case wrongPhoneVerifyNum 405:
                 *   case wrongForgetPassVerifyNum 406:
                 *   case waitBeforeResend 410:
                 *   case doNotHavePermission 412:
                 *   case internalServerError 500: */
                try
                {
                    Log.d(TAG, "response_code: " + response.code());
                    switch (response.code())
                    {
                        case 200:
                            try
                            {
                                JsonObject mainObj = response.body().getAsJsonObject();
                                if (mainObj.get("status").getAsBoolean())
                                {
                                    // dynamic with each call
                                    handleResponses.handleTrueResponse(mainObj);
                                }
                            }
                            catch (NullPointerException | UnsupportedOperationException e)
                            { // errors of response body 'maybe response body has been changed'
                                e.printStackTrace();
                                Crashlytics.logException(e);
                            }

                            break;
                        case 203:
                            displayServerError(null != response.errorBody() ? response.errorBody().string() : context.getString(R.string.error_occurred), context);
                            Common.Instance().signOut(context);

                            break;
                        case 412:
                            if (dialogBuilder == null)
                            {
                                initRenewMembershipAlert(context);
                            }
                            dialogBuilder.displayAlertDialog();

                            break;
                        default: // code != (200 || 203 || 412)
                            displayServerError(null != response.errorBody() ? response.errorBody().string() : context.getString(R.string.error_occurred), context);

                            break;
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

                // dynamic with each call
                handleResponses.handleAfterResponse();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { // connection errors
                if (t.getMessage() != null && !t.getMessage().isEmpty())
                    Log.d(TAG, "onFailure: " + t.getCause());

                if (!Common.Instance().isConnected(context))
                {
                    handleResponses.handleConnectionErrors(context.getString(R.string.check_connection));
                    return;
                }

                handleResponses.handleConnectionErrors(t.getLocalizedMessage());
            }
        };
    }

    private void displayServerError(String errorBody, Context context) {
        try
        {
            JsonObject errorObj = new JsonParser().parse(errorBody).getAsJsonObject();
            String serverError = parseServerErrors(errorObj);
            if (serverError.isEmpty())
            {
                serverError = context.getString(R.string.error_occurred);
            }

            // notify user
            Toast.makeText(context, serverError, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResponse!successful: " + serverError);
        }
        catch (JsonSyntaxException e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "JsonSyntaxException: " + e.getMessage());
        }
    }

    private void initRenewMembershipAlert(final Context context) {
        dialogBuilder = new DialogBuilder();
        dialogBuilder.createAlertDialog(context,
                new ClickInterface.AlertButtonsClickListener() {
                    @Override
                    public void onPositiveClick() {
                        context.startActivity(new Intent(context, MembershipActivity.class));
                    }

                    @Override
                    public void onNegativeCLick() {

                    }
                });

        dialogBuilder.setAlertText(context.getString(R.string.must_renew_membership));
    }

    public void cancelCall() {
        if (call != null && !call.isCanceled())
        {
            call.cancel();
        }
    }
}