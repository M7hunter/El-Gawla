package it_geeks.info.gawla_app.repository.Models;

public class BuyingProcess {

    private String process;

    private String card;

    private String date;

    private String cost;

    public BuyingProcess(String process, String card, String date, String cost) {
        this.process = process;
        this.card = card;
        this.date = date;
        this.cost = cost;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
