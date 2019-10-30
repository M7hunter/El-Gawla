package it_geeks.info.elgawla.repository.Models;

import java.io.Serializable;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Round implements Serializable {

    @PrimaryKey
    @ColumnInfo
    private int product_id;

    @ColumnInfo
    private int salon_id;

    @ColumnInfo
    private int round_id;

    @ColumnInfo
    private String product_name;

    @ColumnInfo
    private String category_name;

    @ColumnInfo
    private String category_color;

    @ColumnInfo
    private String country_name;

    @ColumnInfo
    private String product_commercial_price;

    @ColumnInfo
    private String product_product_description;

    @ColumnInfo
    private String product_image;

    @Ignore
    private List<ProductSubImage> product_images;

    @Ignore
    private List<Card> salon_cards;

    @ColumnInfo
    private String round_date;

    @ColumnInfo
    private boolean status;

    @ColumnInfo
    private String message;

    // constructors
    public Round() {
    }

    public Round(int product_id, int salon_id, int round_id, String product_name, String category_name, String category_color, String country_name, String product_commercial_price, String product_product_description, String product_image, List<ProductSubImage> subImages, List<Card> salonCards, String round_date, boolean status, String message) {
        this.product_id = product_id;
        this.salon_id = salon_id;
        this.round_id = round_id;
        this.product_name = product_name;
        this.category_name = category_name;
        this.category_color = category_color;
        this.country_name = country_name;
        this.product_commercial_price = product_commercial_price;
        this.product_product_description = product_product_description;
        this.product_image = product_image;
        this.product_images = subImages;
        this.salon_cards = salonCards;
        this.round_date = round_date;
        this.status = status;
        this.message = message;
    }

    // getters & setters
    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public int getRound_id() {
        return round_id;
    }

    public void setRound_id(int round_id) {
        this.round_id = round_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_color() {
        return category_color;
    }

    public void setCategory_color(String category_color) {
        this.category_color = category_color;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getProduct_commercial_price() {
        return product_commercial_price;
    }

    public void setProduct_commercial_price(String product_commercial_price) {
        this.product_commercial_price = product_commercial_price;
    }

    public String getProduct_product_description() {
        return product_product_description;
    }

    public void setProduct_product_description(String product_product_description) {
        this.product_product_description = product_product_description;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public List<ProductSubImage> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(List<ProductSubImage> product_images) {
        this.product_images = product_images;
    }

    public List<Card> getSalon_cards() {
        return salon_cards;
    }

    public void setSalon_cards(List<Card> salon_cards) {
        this.salon_cards = salon_cards;
    }

    public String getRound_date() {
        return round_date;
    }

    public void setRound_date(String round_date) {
        this.round_date = round_date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}