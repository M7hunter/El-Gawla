package it_geeks.info.gawla_app.RESTful;

import com.google.gson.JsonElement;

import it_geeks.info.gawla_app.Models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIs {

    // register user
    @POST("/register.json")
    Call<ResponseBody> newUser(@Body User user);

    // login user
    @GET("/login.json")
    Call<JsonElement> getUser(@Path("phone") String phone);

}
