package it_geeks.info.gawla_app.Repositry.Models;

public class Request {

    private  int card_id;

    private String email;

    private String password;

    private int user_id;

    private int country_id;

    private String name;

    private String api_token;

    private String image;

    private String firstName;

    private String lastName;

    private String phone;

    private String gender;

    private String membership;

    private String join_time;

    private String left_time;

    private int salon_id;

    private String offer_time;

    private int offer;

    private String provider;

    private String provider_id;

    private String provider_name;

    private String provider_email;

    private String provider_image;

    private String message;

    private String firebase_token;

    public Request(String email, String password) { // login Request
        this.email = email;
        this.password = password;

    }

    public Request(int user_id, String api_token, String join_time, String left_time, int salon_id) { // join salon
        this.user_id = user_id;
        this.api_token = api_token;
        this.join_time = join_time;
        this.left_time = left_time;
        this.salon_id = salon_id;
    }

    public Request(int user_id, String api_token, int salon_id, int offer) { // add offer
        this.user_id = user_id;
        this.api_token = api_token;
        this.salon_id = salon_id;
        this.offer = offer;
    }

    public Request(String name, String email, int country_id, String pass) { // register Request
        this.name = name;
        this.email = email;
        this.membership = "basic";
        this.country_id = country_id;
        this.password = pass;
    }

    public Request(String provider, String provider_id, String provider_name, String provider_email, String provider_image, int country_id) { // Login and Register SocialMedia Request
        this.provider = provider;
        this.provider_id = provider_id;
        this.provider_name = provider_name;
        this.provider_email = provider_email;
        this.provider_image = provider_image;
        this.country_id = country_id;
    }

    public Request(int user_id, String api_token) { // salons & cards Request
        this.user_id = user_id;
        this.api_token = api_token;
    }

    public Request(String api_token) { // all countries
        this.api_token = api_token;
        this.email = api_token; // for email in forget password
    }

    public Request(int user_id, String api_token, String image) { // User Profile Image Upload
        this.user_id = user_id;
        this.api_token = api_token;
        this.password = image;
        this.image = image;
    }

    public Request(int user_id, String api_token, String firstName, String lastName, String phone, String gender, int country_id) { // User Profile update
        this.user_id = user_id;
        this.api_token = api_token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.country_id = country_id;
    }

    public Request(int user_id, String api_token, int integer) { // get round RealTime
        this.user_id = user_id;
        this.api_token = api_token;
        this.salon_id = integer;
        this.card_id = integer;
    }

    public Request(int user_id, String api_token, String name, String email, String message) { // contact Us
        this.user_id = user_id;
        this.api_token = api_token;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public Request(String text, int user_id, String api_token) { // Update Email
        this.email = text;
        this.user_id = user_id;
        this.api_token = api_token;
    }


    public Request(int user_id, String api_token ,String firebase_token,int x) { // /Update Firebase Token
        this.user_id = user_id;
        this.api_token = api_token;
        this.firebase_token = firebase_token;
    }

}