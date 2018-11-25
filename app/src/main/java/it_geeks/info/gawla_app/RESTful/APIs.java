package it_geeks.info.gawla_app.RESTful;

import com.google.gson.JsonElement;

import it_geeks.info.gawla_app.Models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIs {

    //user
    @PUT("/User.json")
    Call<ResponseBody> newUser(@Path("phone") String phone,
                               @Body User user);

    @GET("/User/{phone}.json")
    Call<JsonElement> getUser(@Path("phone") String phone);

}
