package it_geeks.info.elgawla.repository.RESTful;

import com.google.gson.JsonObject;

public interface HandleResponses {

    void handleTrueResponse(JsonObject mainObject);

    void handleAfterResponse();

    void handleConnectionErrors(String errorMessage);
}
