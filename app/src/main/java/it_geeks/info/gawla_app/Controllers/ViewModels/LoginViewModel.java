package it_geeks.info.gawla_app.Controllers.ViewModels;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.general.Common;
import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.RequestsActions;
import it_geeks.info.gawla_app.repository.Storage.GawlaDataBse;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
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
                cacheUserData(mainObject, LoginActivity.providerNormalLogin); // with normal provider

                ((LoginActivity) context).startActivity(new Intent(context, MainActivity.class));
                ((LoginActivity) context).finish();
                Common.Instance(context).updateFirebaseToken();

                //hide progress
                ((LoginActivity) context).hideLoading();
            }

            @Override
            public void handleFalseResponse(JsonObject mainObject) {
                ((LoginActivity) context).hideLoading();
                FirebaseAuth.getInstance().signOut();
            }

            @Override
            public void handleEmptyResponse() {
                ((LoginActivity) context).hideLoading();
                FirebaseAuth.getInstance().signOut();
            }

            @Override
            public void handleConnectionErrors(String errorMessage) {
                ((LoginActivity) context).hideLoading();
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
                        ((LoginActivity) context).hideLoading();
//                        new UpdateFirebaseToken(context);

                        Common.Instance(context).updateFirebaseToken();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                        ((LoginActivity) context).hideLoading();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((LoginActivity) context).hideLoading();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((LoginActivity) context).hideLoading();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                });
    }

}
