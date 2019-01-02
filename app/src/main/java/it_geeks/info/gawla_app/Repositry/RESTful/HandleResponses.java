package it_geeks.info.gawla_app.Repositry.RESTful;

import com.google.gson.JsonObject;

public interface HandleResponses {

    void handleResponseData(JsonObject mainObject);

    void handleEmptyResponse();
}
