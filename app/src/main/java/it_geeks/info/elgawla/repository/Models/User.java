package it_geeks.info.elgawla.repository.Models;

public class User {

  private int user_id;

  private String api_token;

  private String name;

  private int country_id;

  private String image;

  private String email;

  private String membership;

  private String gender;

  private String phone;

  private String password;

  private boolean active;

  public User(String name, String email, String phone, String password) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.membership = "basic";
    this.password = password;
  }

  public User(int user_id, String api_token, String name, int country_id, String image, String email, String gender, String phone) {
    this.user_id = user_id;
    this.api_token = api_token;
    this.name = name;
    this.country_id = country_id;
    this.image = image;
    this.email = email;
    this.gender = gender;
    this.phone = phone;
  }

  public User(int user_id, String api_token, String name, int country_id, String image, String email, String membership, String gender, String phone) {
    this.user_id = user_id;
    this.api_token = api_token;
    this.name = name;
    this.country_id = country_id;
    this.image = image;
    this.email = email;
    this.membership = membership;
    this.gender = gender;
    this.phone = phone;
  }

  public User(int user_id, String api_token, String name, int country_id, String image, String email, boolean active, String gender, String phone) {
    this.user_id = user_id;
    this.api_token = api_token;
    this.name = name;
    this.country_id = country_id;
    this.image = image;
    this.email = email;
    this.active = active;
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

  public int getCountry_id() {
    return country_id;
  }

  public void setCountry_id(int country_id) {
    this.country_id = country_id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
