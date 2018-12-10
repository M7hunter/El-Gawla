package it_geeks.info.gawla_app.Repositry.Models;

public class Country {

    private int country_id;

    private String country_title;

    private String count_code;

    private String country_timezone;

    private String tel;

    private String image;

    public Country(int country_id, String country_title, String count_code, String country_timezone, String tel, String image) {
        this.country_id = country_id;
        this.country_title = country_title;
        this.count_code = count_code;
        this.country_timezone = country_timezone;
        this.tel = tel;
        this.image = image;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getCountry_title() {
        return country_title;
    }

    public void setCountry_title(String country_title) {
        this.country_title = country_title;
    }

    public String getCount_code() {
        return count_code;
    }

    public void setCount_code(String count_code) {
        this.count_code = count_code;
    }

    public String getCountry_timezone() {
        return country_timezone;
    }

    public void setCountry_timezone(String country_timezone) {
        this.country_timezone = country_timezone;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
