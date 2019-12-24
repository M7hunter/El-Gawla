package it_geeks.info.elgawla.repository.RESTful;

import com.google.gson.JsonObject;

public abstract class HandleResponses {

    public void onTrueResponse(JsonObject mainObject){}

    public void onFalseResponse() {}

    public void afterResponse(){}

    public void onServerError() {}

    public void onFailure(String failureMessage){}

    public void onConnectionError(String errorMessage){}
}


