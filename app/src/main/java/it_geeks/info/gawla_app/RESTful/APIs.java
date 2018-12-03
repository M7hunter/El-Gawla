package it_geeks.info.gawla_app.RESTful;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Models.RequestMainBody;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;

public interface APIs {

    // register user
    @POST("master")
    Call<JsonObject> registerUser(@Body RequestMainBody requestMainBody);

    // login user
    @POST("master")
    Call<JsonObject> loginUser(@Body RequestMainBody requestMainBody);

    // get all salons
    @POST("master")
    Call<JsonObject> getSalons(@Body RequestMainBody requestMainBody);

    // get cards
    @POST("master")
    Call<JsonObject> getUserCards(@Body RequestMainBody requestMainBody);

}
