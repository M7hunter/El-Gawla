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
import it_geeks.info.gawla_app.Repositry.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.loginActivities.CreateAccountActivity;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.views.loginActivities.SubscribePlanActivity;

import static it_geeks.info.gawla_app.views.loginActivities.LoginActivity.providerFacebook;
import static it_geeks.info.gawla_app.views.loginActivities.LoginActivity.providerGoogle;

public class CreateAccountViewModel {
    private Context context;

    public CreateAccountViewModel(Context context) {
        this.context = context;
    }

    public void connectToServer(final User user, final int countryId) {
        ((CreateAccountActivity) context).setLoadingScreen();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                RequestsActions.register.toString(), new Request(user.getName(), user.getEmail(), countryId, user.getPassword()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        ((CreateAccountActivity) context).closeLoadingScreen();

                        // notify user
                        Toast.makeText(context, mainObject.get("message").getAsString(), Toast.LENGTH_SHORT).show();

                        // save user data locally
                        cacheUserData(mainObject, context.getResources().getString(R.string.app_name));

                        // goto next page
                        context.startActivity(new Intent(context, SubscribePlanActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((CreateAccountActivity) context).closeLoadingScreen();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((CreateAccountActivity) context).closeLoadingScreen();

                        // notify user
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();

                        // try one more time
                        if (errorMessage.contains("timeout") && ((CreateAccountActivity) context).reconnect < 1) {
                            ((CreateAccountActivity) context).reconnect++;
                            connectToServer(user, countryId);
                        }
                    }
                });
    }

    // google sign up
    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String image = account.getPhotoUrl().toString();
            String provider = providerGoogle;

            Log.e("Mo7", id + " - " + name + " - " + email + " - " + provider + " - " + image);
            socialLogin(id, name, email, image, provider);

        } catch (ApiException e) {
            Log.w("", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(context).getCountry().getCountry_id();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                RequestsActions.loginOrRegisterWithSocial.toString(), new Request(provider, id, name, email, image, countryId, LoginActivity.FirebaseInstanceTokenID()), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);

                        context.startActivity(new Intent(context, MainActivity.class));
                        ((CreateAccountActivity) context).finish();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {

                    }

                    @Override
                    public void handleEmptyResponse() {

                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Log.e("Mo7", errorMessage);
                    }
                });
    }

    public void getData(JSONObject object) {
        try {
            URL Profile_Picture = new URL("https://graph.facebook.com/" + object.getString("id") + "/picture?type=normal");

            String id = object.optString("id");
            String name = object.optString("name");
            String email = object.optString("email");
            String image = Profile_Picture.toString();
            String provider = providerFacebook;
            new CreateAccountViewModel(context).socialLogin(id, name, email, image, provider);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(context).saveUser(user);
        SharedPrefManager.getInstance(context).saveProvider(provider); // Provider
    }
}
