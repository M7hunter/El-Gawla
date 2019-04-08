package it_geeks.info.gawla_app.repository.RESTful;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.repository.Models.RequestMainBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIs {

  @Headers("Accept: application/json")
  @POST("master")
  Call<JsonObject> request(@Body RequestMainBody requestMainBody);
}