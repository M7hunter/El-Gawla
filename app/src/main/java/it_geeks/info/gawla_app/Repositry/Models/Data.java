package it_geeks.info.gawla_app.Repositry.Models;

public class Data {

    private String action;

    private long last_request;

    public Data(String action) {
        this.action = action;
    }

    public Data(String action, long last_request) { // request salons
        this.action = action;
        this.last_request = last_request;
    }
}