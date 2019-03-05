package it_geeks.info.gawla_app.repository.RESTful;

import com.google.gson.JsonObject;

import it_geeks.info.gawla_app.repository.Models.RequestMainBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIs {

  // request salons || cards || login || register || upload Image || SocialMedia Login
  @POST("master")
  Call<JsonObject> request(@Body RequestMainBody requestMainBody);
}