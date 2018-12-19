package it_geeks.info.gawla_app.Repositry.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ProductSubImage {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private int Id;

    @ColumnInfo
    private int product_id;

    @ColumnInfo
    private String imageUrl;

    public ProductSubImage() {
    }

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
