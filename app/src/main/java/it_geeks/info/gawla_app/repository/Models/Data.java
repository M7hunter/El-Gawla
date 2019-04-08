package it_geeks.info.gawla_app.repository.Models;

public class Data {

    private String action;

    private long last_request;

    private int page;

    public Data(String action) {
        this.action = action;
    }

    public Data(String action, int page) { // request salons paged
        this.action = action;
        this.page = page;
    }
}