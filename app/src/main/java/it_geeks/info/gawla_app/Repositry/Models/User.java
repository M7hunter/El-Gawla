package it_geeks.info.gawla_app.Repositry.Models;

public class User {

    private int user_id;

    private String name;

    private String email;

    private boolean active;

    private String api_token;

    private String image;

    private String membership;

    private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.membership = "basic";
        this.password = password;
    }

    public User(int user_id, String name, String email, String api_token, String image) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.api_token = api_token;
        this.image = image;
    }

    public User(int user_id, String name, String email , boolean active, String api_token, String image) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.api_token = api_token;
        this.image = image;
        this.membership = "basic";
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public boolean isActive() {
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
