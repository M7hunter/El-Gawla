package it_geeks.info.gawla_app.Repositry.Models;

public class TopTen {

    private int id;

    private String name;

    private String offer;

    public TopTen(int id, String name, String offer) {
        this.id = id;
        this.name = name;
        this.offer = offer;
    }

    public int getOrder() {
        return id;
    }

    public void setOrder(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}
