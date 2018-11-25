package it_geeks.info.gawla_app.RESTful;


import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface APIs {

    // register user
    @POST("/register.json")
    Call<ResponseBody> registerUser(@Body User user);

    // login user
    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> loginUser(@Field("email") String email , @Field("password") String password);

}
