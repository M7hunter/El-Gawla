package it_geeks.info.gawla_app.repository.RESTful;

import com.google.gson.JsonObject;

public interface HandleResponses {

    void handleTrueResponse(JsonObject mainObject);

    void handleFalseResponse(JsonObject errorObject);

    void handleEmptyResponse();

    void handleConnectionErrors(String errorMessage);
}
