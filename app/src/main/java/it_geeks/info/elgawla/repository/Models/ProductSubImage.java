package it_geeks.info.elgawla.repository.Models;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ProductSubImage implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private int Id;

    @ColumnInfo
    private int product_id;

    @ColumnInfo
    private String imageUrl;

    public ProductSubImage() {
    }

    @Ignore
    public ProductSubImage(int product_id, String imageUrl) {
        this.product_id = product_id;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}