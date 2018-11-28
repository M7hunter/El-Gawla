package it_geeks.info.gawla_app.Models;

public class Request {

    private String email;

    private String password;

    private int user_id;

    private String name;

    private boolean active;

    private String api_token;

    private String image;

    private String membership;

    public Request(String email, String password) { // login Request
        this.email = email;
        this.password = password;
    }
    public Request(String name, String email, String pass) { // register Request
        this.name = name;
        this.email = email;
        this.membership = "basic";
        this.password = pass;
    }

    public Request(int user_id, String api_token) { // all salons Request
        this.user_id = user_id;
        this.api_token = api_token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembership() {
        return membership;
    }

    public void setMembership(String membership) {
        this.membership = membership;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}