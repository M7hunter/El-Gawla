package it_geeks.info.gawla_app.Controllers.ViewModels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.RequestsActions;
import it_geeks.info.gawla_app.Repositry.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;

public class LoginViewModel {
    private Context context;

    public LoginViewModel(Context context) {
        this.context = context;
    }

    public void login(String email, String pass) {
        RetrofitClient.getInstance(context).executeConnectionToServer(context, RequestsActions.login.toString(), new Request(email, pass), new HandleResponses() {
            @Override
            public void handleTrueResponse(JsonObject mainObject) {
                cacheUserData(mainObject, context.getResources().getString(R.string.app_name)); // with normal provider

                ((LoginActivity) context).startActivity(new Intent(context, MainActivity.class));
                ((LoginActivity) context).finish();

                //hide progress
                ((LoginActivity) context).closeLoadingScreen();
            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {

            }

            @Override
            public void handleEmptyResponse() {
                ((LoginActivity) context).closeLoadingScreen();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                ((LoginActivity) context).closeLoadingScreen();
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(context).saveUser(user);
        SharedPrefManager.getInstance(context).saveProvider(provider); // Provider

        // save || update country
        SharedPrefManager.getInstance(context)
                .setCountry(GawlaDataBse.getGawlaDatabase(context).countryDao().getCountryByID(user.getCountry_id()));
    }

    // social login
    private void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(context).getCountry().getCountry_id();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                RequestsActions.loginOrRegisterWithSocial.toString(), new Request(provider, id, name, email, image, countryId), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);
                        ((LoginActivity) context).startActivity(new Intent(context, MainActivity.class));
                        ((LoginActivity) context).finish();
                        ((LoginActivity) context).closeLoadingScreen();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((LoginActivity) context).closeLoadingScreen();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((LoginActivity) context).closeLoadingScreen();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // google login
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask, String providerGoogle) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;

            socialLogin(id, name, email, image, provider);
            ((LoginActivity) context).setLoadingScreen();

            Log.w("Mo7", id + name + email + image);
        } catch (ApiException e) {
            Log.w("Mo7", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // fb login
    public void getData(final JSONObject object, String providerFacebook) {
        try {
            URL Profile_Picture = new URL("https://graph.facebook.com/v3.0/" + object.getString("id") + "/picture?type=normal");
            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();
            String provider = providerFacebook;
            ((LoginActivity) context).setLoadingScreen();
            socialLogin(id, name, email, image, provider);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
