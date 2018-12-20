package it_geeks.info.gawla_app.Repositry.RESTful;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.Repositry.Models.Data;
import it_geeks.info.gawla_app.Repositry.Models.Request;
import it_geeks.info.gawla_app.Repositry.Models.RequestMainBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIs {

    // request salons || cards || login || register || upload Image || SocialMedia Login
    @POST("master")
    Call<JsonObject> request(@Body RequestMainBody requestMainBody);

    @FormUrlEncoded
    @POST("master")
    Call<JsonObject> uploadImageRequest(@Field("Data")Data data, @Field("Request") Request request);
}
