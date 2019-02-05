package it_geeks.info.gawla_app.Repositry.Models;

public class Request {

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

    private String provider_id ;

    private String provider_name ;

    private String provider_email ;

    private String provider_image ;

    private String message;

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

    public Request(int user_id, String api_token, int salon_id, String offer_time, int offer) { // add offer
        this.user_id = user_id;
        this.api_token = api_token;
        this.salon_id = salon_id;
        this.offer_time = offer_time;
        this.offer = offer;
    }

    public Request(String name, String email, int country_id, String pass) { // register Request
        this.name = name;
        this.email = email;
        this.membership = "basic";
        this.country_id = country_id;
        this.password = pass;
    }

    public Request(int  userId, String api_token, String oldPass, String newPass) { // new pass Request
        user_id = userId;
        this.api_token = api_token;
        String old_password = oldPass;
        String password = newPass;
    }

    public Request(String provider, String provider_id, String provider_name, String provider_email, String provider_image,int country_id) { // Login and Register SocialMedia Request
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
    }

    public Request(int user_id, String api_token, String image) { // User Profile Image Upload
        this.user_id = user_id;
        this.api_token = api_token;
        this.image = image;
    }

    public Request(int user_id, String api_token, String firstName,String lastName,String phone,String gender, int country_id) { // User Profile update
        this.user_id = user_id;
        this.api_token = api_token;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender;
        this.country_id = country_id;
    }

    public Request(int user_id, String api_token, int salon_id) { // get round Realtime
        this.user_id = user_id;
        this.api_token = api_token;
        this.salon_id = salon_id;
    }

    public Request( int user_id , String api_token , String name , String email , String message) { // contact Us
        this.user_id = user_id;
        this.api_token = api_token;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public Request(String email, int user_id, String api_token) { // Update Email
        this.email = email;
        this.user_id = user_id;
        this.api_token = api_token;
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