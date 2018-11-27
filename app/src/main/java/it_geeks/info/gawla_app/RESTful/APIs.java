package it_geeks.info.gawla_app.RESTful;


import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Models.User;
import it_geeks.info.gawla_app.Models.UserLogin;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;


public interface APIs {

    // register user
    @POST("master")
    Call<JsonObject> registerUser(@Body User user);

    // login user
    @POST("master")
    Call<JsonObject> loginUser(@Body UserLogin userLogin);

}
