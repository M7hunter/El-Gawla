package it_geeks.info.gawla_app.Repositry.Models;

public class Card {

    private String card_name;

    private String card_category;

    private String type;

    private String color_code;

    private String cost;

    public Card(String card_name, String card_category, String type, String color_code, String cost) {
        this.card_name = card_name;
        this.card_category = card_category;
        this.type = type;
        this.color_code = color_code;
        this.cost = cost;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_category() {
        return card_category;
    }

    public void setCard_category(String card_category) {
        this.card_category = card_category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
