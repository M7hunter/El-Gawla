package it_geeks.info.gawla_app.Repositry.Models;

public class Notification {
    String title , body , type , date ;
    int id ;

    public Notification(String title, String body, String type, String date, int id) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.date = date;
        this.id = id;
    }

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
