package it_geeks.info.gawla_app.Repositry.Models;

import java.util.PriorityQueue;

public class Request {

    private String email;

    private String password;

    private int user_id;

    private String name;

    private String api_token;

    private String image;

    private String membership;

    private String provider;

    private int provider_id ;

    private String provider_name ;

    private String provider_email ;

    private String provider_image ;

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

    public Request(String provider, int provider_id, String provider_name, String provider_email, String provider_image) { // Login and Register SocialMedia Requesr
        this.provider = provider;
        this.provider_id = provider_id;
        this.provider_name = provider_name;
        this.provider_email = provider_email;
        this.provider_image = provider_image;
    }

    public Request(int user_id, String api_token) { // salons & cards Request
        this.user_id = user_id;
        this.api_token = api_token;
    }

    public Request(int user_id, String api_token, String image) { // User Profile Image Upload
        this.user_id = user_id;
        this.api_token = api_token;
        this.image = image;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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