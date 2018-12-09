package it_geeks.info.gawla_app.Repositry.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

@Entity
public class Round {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo
    private String product_name;

    @ColumnInfo
    private String product_image;

    @ColumnInfo
    private String product_category;

    @ColumnInfo
    private String product_price;

    @ColumnInfo
    private String product_description;

    @ColumnInfo
    private String start_time;

    @ColumnInfo
    private String end_time;

    @ColumnInfo
    private String joined_members_number;

    // constructors
    public Round() {
    }

    public Round(String productName, String productImage, String productCategory, String productPrice, String productDescription, String startTime, String endTime, String joinedMembersNumber) {
        product_name = productName;
        product_image = productImage;
        product_category = productCategory;
        product_price = productPrice;
        product_description = productDescription;
        start_time = startTime;
        end_time = endTime;
        joined_members_number = joinedMembersNumber;
    }

    // getters & setters
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getJoined_members_number() {
        return joined_members_number;
    }

    public void setJoined_members_number(String joined_members_number) {
        this.joined_members_number = joined_members_number;
    }

    // calculate differences and update
    public static DiffUtil.ItemCallback<Round> DIFF_CALLBACK = new DiffUtil.ItemCallback<Round>() {
        @Override
        public boolean areItemsTheSame(@NonNull Round oldItem, @NonNull Round newItem) {
            return oldItem.Id == newItem.Id;
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
        return round.Id == this.Id;
    }
}
