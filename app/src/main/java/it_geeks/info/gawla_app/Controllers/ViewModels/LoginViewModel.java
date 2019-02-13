package it_geeks.info.gawla_app.Controllers.ViewModels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;
import it_geeks.info.gawla_app.R;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.User;
import it_geeks.info.gawla_app.Repositry.RESTful.HandleResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.ParseResponses;
import it_geeks.info.gawla_app.Repositry.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.Repositry.RequestsActions;
import it_geeks.info.gawla_app.Repositry.Services.fcm.UpdateFirebaseToken;
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
                new UpdateFirebaseToken(context);

                //hide progress
                ((LoginActivity) context).closeLoadingScreen();
            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {
                ((LoginActivity) context).closeLoadingScreen();
                FirebaseAuth.getInstance().signOut();
            }

            @Override
            public void handleEmptyResponse() {
                ((LoginActivity) context).closeLoadingScreen();
                FirebaseAuth.getInstance().signOut();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                ((LoginActivity) context).closeLoadingScreen();
                FirebaseAuth.getInstance().signOut();
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
    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(context).getCountry().getCountry_id();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                RequestsActions.loginOrRegisterWithSocial.toString(), new Request(provider, id, name, email, image, countryId), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);
                        ((LoginActivity) context).startActivity(new Intent(context, MainActivity.class));
                        ((LoginActivity) context).finish();
                        ((LoginActivity) context).closeLoadingScreen();
                        new UpdateFirebaseToken(context);
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                        ((LoginActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((LoginActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((LoginActivity) context).closeLoadingScreen();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
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
            String image;
            if(account.getPhotoUrl() == null){
                 image = "https://i.stack.imgur.com/l60Hf.png";
            } else {
                 image = account.getPhotoUrl().toString();
            }
            String provider = providerGoogle;

            socialLogin(id, name, email, image, provider);
            ((LoginActivity) context).setLoadingScreen();

            Log.w("Mo7", id + name + email + image);
        } catch (ApiException e) {
            Log.w("Mo7", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}
