package it_geeks.info.elgawla.repository.Models;

public class MyCardModel {

    private int card_id;

    private String card_color;

    private String card_category;

    private boolean card_status;

    private int salon_id;

    public MyCardModel(int card_id, String card_color, String card_category, boolean card_status, int salon_id) {
        this.card_id = card_id;
        this.card_color = card_color;
        this.card_category = card_category;
        this.card_status = card_status;
        this.salon_id = salon_id;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_color() {
        return card_color;
    }

    public void setCard_color(String card_color) {
        this.card_color = card_color;
    }

    public String getCard_category() {
        return card_category;
    }

    public void setCard_category(String card_category) {
        this.card_category = card_category;
    }

    public boolean getCard_status() {
        return card_status;
    }

    public void setCard_status(boolean card_status) {
        this.card_status = card_status;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }
}
