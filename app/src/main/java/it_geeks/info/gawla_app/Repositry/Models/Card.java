package it_geeks.info.gawla_app.Repositry.Models;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Card implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private int card_id;

    @ColumnInfo
    private int salon_id;

    @ColumnInfo
    private String card_name;

    @ColumnInfo
    private String card_details;

    @ColumnInfo
    private String card_category;

    @ColumnInfo
    private String card_type;

    @ColumnInfo
    private String card_color;

    @ColumnInfo
    private String card_cost;

    @ColumnInfo
    private int count;

    @Ignore
    private int position;

    public Card() {
    }

    @Ignore
    public Card(int card_id, int salon_id, String card_name, String card_details, String card_type, String card_color, String card_cost) {
        this.card_id = card_id;
        this.salon_id = salon_id;
        this.card_name = card_name;
        this.card_details = card_details;
        this.card_type = card_type;
        this.card_color = card_color;
        this.card_cost = card_cost;
    }

    @Ignore
    public Card(int card_id, String card_name, String card_details, String card_color, String card_cost) {
        this.card_id = card_id;
        this.card_name = card_name;
        this.card_details = card_details;
        this.card_color = card_color;
        this.card_cost = card_cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalon_id() {
        return salon_id;
    }

    public void setSalon_id(int salon_id) {
        this.salon_id = salon_id;
    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_details() {
        return card_details;
    }

    public void setCard_details(String card_details) {
        this.card_details = card_details;
    }

    public String getCard_category() {
        return card_category;
    }

    public void setCard_category(String card_category) {
        this.card_category = card_category;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public String getCard_color() {
        return card_color;
    }

    public void setCard_color(String card_color) {
        this.card_color = card_color;
    }

    public String getCard_cost() {
        return card_cost;
    }

    public void setCard_cost(String card_cost) {
        this.card_cost = card_cost;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}