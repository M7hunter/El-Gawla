package it_geeks.info.gawla_app.Repositry.Models;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Round {

    @PrimaryKey()
    @ColumnInfo
    private int product_id;

    @ColumnInfo
    private int salon_id;

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
    private List<String> product_images;

    @Ignore
    private List<Card> salon_cards;

    @ColumnInfo
    private String round_start_time;

    @ColumnInfo
    private String round_end_time;

    @ColumnInfo
    private String first_join_time;

    @ColumnInfo
    private String second_join_time;

    @ColumnInfo
    private String round_date;

    @ColumnInfo
    private String round_time;

    @ColumnInfo
    private String rest_time;

    // constructors
    public Round() {
    }

    @Ignore
    public Round(int product_id, int salon_id,String product_name, String category_name, String category_color, String country_name, String product_commercial_price, String product_product_description, String product_image, String round_start_time, String round_end_time, String first_join_time, String second_join_time, String round_date, String round_time, String rest_time) {
        this.product_id = product_id;
        this.salon_id = salon_id;
        this.product_name = product_name;
        this.category_name = category_name;
        this.category_color = category_color;
        this.country_name = country_name;
        this.product_commercial_price = product_commercial_price;
        this.product_product_description = product_product_description;
        this.product_image = product_image;
        this.round_start_time = round_start_time;
        this.round_end_time = round_end_time;
        this.first_join_time = first_join_time;
        this.second_join_time = second_join_time;
        this.round_date = round_date;
        this.round_time = round_time;
        this.rest_time = rest_time;
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

    public List<String> getProduct_images() {
        return product_images;
    }

    public void setProduct_images(List<String> product_images) {
        this.product_images = product_images;
    }

    public List<Card> getSalon_cards() {
        return salon_cards;
    }

    public void setSalon_cards(List<Card> salon_cards) {
        this.salon_cards = salon_cards;
    }

    public String getRound_start_time() {
        return round_start_time;
    }

    public void setRound_start_time(String round_start_time) {
        this.round_start_time = round_start_time;
    }

    public String getRound_end_time() {
        return round_end_time;
    }

    public void setRound_end_time(String round_end_time) {
        this.round_end_time = round_end_time;
    }

    public String getFirst_join_time() {
        return first_join_time;
    }

    public void setFirst_join_time(String first_join_time) {
        this.first_join_time = first_join_time;
    }

    public String getSecond_join_time() {
        return second_join_time;
    }

    public void setSecond_join_time(String second_join_time) {
        this.second_join_time = second_join_time;
    }

    public String getRound_date() {
        return round_date;
    }

    public void setRound_date(String round_date) {
        this.round_date = round_date;
    }

    public String getRound_time() {
        return round_time;
    }

    public void setRound_time(String round_time) {
        this.round_time = round_time;
    }

    public String getRest_time() {
        return rest_time;
    }

    public void setRest_time(String rest_time) {
        this.rest_time = rest_time;
    }

    // calculate differences and update
    public static DiffUtil.ItemCallback<Round> DIFF_CALLBACK = new DiffUtil.ItemCallback<Round>() {
        @Override
        public boolean areItemsTheSame(@NonNull Round oldItem, @NonNull Round newItem) {
            return oldItem.product_id == newItem.product_id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Round oldItem, @NonNull Round newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Round round = (Round) obj;
        return round.product_id == this.product_id;
    }
}
