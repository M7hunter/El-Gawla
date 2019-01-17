package it_geeks.info.gawla_app.Models;

import it_geeks.info.gawla_app.Repositry.Models.User;

public class Data {

    private String action;

    private User user;

    public Data(String action, User user) {
        this.action = action;
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
