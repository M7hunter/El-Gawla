package it_geeks.info.gawla_app.Repositry.RESTful;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIs {

    // request salons || cards || login || register
    @POST("master")
    Call<JsonObject> request(@Body RequestMainBody requestMainBody);

    // Upload Image
    @POST("master")
    Call<JsonObject> UploadImage(@Body RequestMainBody requestMainBody);

    // Upload Image
    @FormUrlEncoded
    @POST("master")
    Call<JsonObject> SocialLoginAndRegister(@Body RequestMainBody requestMainBody);
}
