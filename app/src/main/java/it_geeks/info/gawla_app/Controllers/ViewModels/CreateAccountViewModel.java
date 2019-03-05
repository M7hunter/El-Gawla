package it_geeks.info.gawla_app.Controllers.ViewModels;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.repository.Models.Request;
import it_geeks.info.gawla_app.repository.Models.User;
import it_geeks.info.gawla_app.repository.RESTful.HandleResponses;
import it_geeks.info.gawla_app.repository.RESTful.ParseResponses;
import it_geeks.info.gawla_app.repository.RESTful.RetrofitClient;
import it_geeks.info.gawla_app.repository.RequestsActions;
import it_geeks.info.gawla_app.repository.services.fcm.UpdateFirebaseToken;
import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;
import it_geeks.info.gawla_app.views.MainActivity;
import it_geeks.info.gawla_app.views.loginActivities.CreateAccountActivity;
import it_geeks.info.gawla_app.views.loginActivities.LoginActivity;
import it_geeks.info.gawla_app.views.loginActivities.SubscribePlanActivity;

public class CreateAccountViewModel {
    private Context context;

    public void cacheUserData(JsonObject mainObject, String provider) {
        User user = ParseResponses.parseUser(mainObject);
        SharedPrefManager.getInstance(context).saveUser(user);
        SharedPrefManager.getInstance(context).saveProvider(provider); // Provider
    }

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
                        cacheUserData(mainObject, LoginActivity.providerNormalLogin);

                        new UpdateFirebaseToken(context);

                        // goto next page
                        context.startActivity(new Intent(context, SubscribePlanActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                        FirebaseAuth.getInstance().signOut();
                        ((CreateAccountActivity) context).closeLoadingScreen();
                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((CreateAccountActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        ((CreateAccountActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();

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

    public void socialLogin(String id, final String name, final String email, final String image, final String provider) {
        int countryId = SharedPrefManager.getInstance(context).getCountry().getCountry_id();
        RetrofitClient.getInstance(context).executeConnectionToServer(context,
                RequestsActions.loginOrRegisterWithSocial.toString(), new Request(provider, id, name, email, image, countryId), new HandleResponses() {
                    @Override
                    public void handleTrueResponse(JsonObject mainObject) {
                        cacheUserData(mainObject, provider);
                        new UpdateFirebaseToken(context);
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((CreateAccountActivity) context).finish();
                    }

                    @Override
                    public void handleFalseResponse(JsonObject mainObject) {
                        FirebaseAuth.getInstance().signOut();
                        ((CreateAccountActivity) context).closeLoadingScreen();
                    }

                    @Override
                    public void handleEmptyResponse() {
                        ((CreateAccountActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();
                    }

                    @Override
                    public void handleConnectionErrors(String errorMessage) {
                        Log.e("Mo7", errorMessage);
                        ((CreateAccountActivity) context).closeLoadingScreen();
                        FirebaseAuth.getInstance().signOut();
                    }
                });
    }


}
