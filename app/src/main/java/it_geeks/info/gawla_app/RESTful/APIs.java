package it_geeks.info.gawla_app.RESTful;

import com.google.gson.JsonElement;

import it_geeks.info.gawla_app.Models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIs {

    // register user
    @POST("/register.json")
    Call<ResponseBody> registerUser(@Body User user);

    // login user
    @GET("/login.json")
    Call<JsonElement> loginUser(String  email, String pass);

}
