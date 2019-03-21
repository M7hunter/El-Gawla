package it_geeks.info.gawla_app.repository.Models;

public class Request {

    private int round_id;

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

    public Request(int in1, String str, int in2, int in3) {
        this.user_id = in1;
        this.api_token = str;
        this.salon_id = in2;
        this.offer = in3;
        this.round_id = in3;
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
        this.email = api_token;
    }

    public Request(int user_id, String api_token, String string) { // User Profile Image Upload
        this.user_id = user_id;
        this.api_token = api_token;
        this.password = string;
        this.image = string;
        this.firebase_token = string;
        this.membership = string;
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

    public Request(int userId, String apiToken, int card_id, int salonId, int round_id) {
        this.user_id = userId;
        this.api_token = apiToken;
        this.card_id = card_id;
        this.salon_id = salonId;
        this.round_id = round_id;
    }

    public Request(int in1, int in2, String str1, String  str2) {
        this.user_id = in1;
        this.country_id = in2;
        this.api_token = str1;
        this.image = str2;
    }
}