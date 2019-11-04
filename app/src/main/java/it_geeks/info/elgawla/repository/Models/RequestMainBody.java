package it_geeks.info.elgawla.repository.Models;

import it_geeks.info.elgawla.repository.RESTful.RequestModel;

public class RequestMainBody {

    private Data Data;

    private RequestModel Request;

    public RequestMainBody(Data Data, RequestModel request) {
        this.Data = Data;
        this.Request = request;
    }

}
