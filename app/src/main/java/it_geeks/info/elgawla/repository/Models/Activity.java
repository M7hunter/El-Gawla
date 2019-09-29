package it_geeks.info.elgawla.repository.Models;

public class Activity {

    private String Body;

    private String Time;

    public Activity(String body, String time) {
        Body = body;
        Time = time;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
