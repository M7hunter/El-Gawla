package it_geeks.info.gawla_app.repository.Models;

import it_geeks.info.gawla_app.repository.RESTful.Request;

public class RequestMainBody {

    private Data Data;

    private Request Request;

    public RequestMainBody(Data Data, Request request) {
        this.Data = Data;
        this.Request = request;
    }

}
