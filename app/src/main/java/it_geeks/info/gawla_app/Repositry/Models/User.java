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

    private String firstName;

    private String lastName;

    private String gender;

    private String phone;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.membership = "basic";
        this.password = password;
    }

    public User(int user_id, String name, String email, String api_token, String image , String firstName,String lastName,String phone,String gender,String membership) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.api_token = api_token;
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phone = phone;
        this.membership = membership;
    }

    public User(int user_id, String name, String email, boolean active, String api_token, String image, String membership, String password, String firstName, String lastName, String country, String gender, String phone) {
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.api_token = api_token;
        this.image = image;
        this.membership = membership;
        this.password = password;
        this.firstName = firstName;
        LastName = lastName;
        this.country = country;
        this.gender = gender;
        this.phone = phone;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
