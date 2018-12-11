package it_geeks.info.gawla_app.Repositry.Models;

public class Card {

    private int card_id;

    private String card_name;

    private String card_type;

    private String card_color;

    private String card_cost;

    private int count;

    public Card(int card_id, String card_name, String card_type, String card_color, String card_cost, int count) {
        this.card_id = card_id;
        this.card_name = card_name;
        this.card_type = card_type;
        this.card_color = card_color;
        this.card_cost = card_cost;
        this.count = count;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_color() {
        return card_color;
    }

    public void setCard_color(String card_color) {
        this.card_color = card_color;
    }

    public String getCard_cost() {
        return card_cost;
    }

    public void setCard_cost(String card_cost) {
        this.card_cost = card_cost;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
