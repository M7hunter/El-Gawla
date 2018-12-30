package it_geeks.info.gawla_app.Repositry.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import it_geeks.info.gawla_app.General.MediaInterfaces.ItemMedia;

@Entity
public class ProductSubVideo implements ItemMedia {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    private int Id;

    @ColumnInfo
    private int product_id;

    @ColumnInfo
    private String videoUrl;

    public ProductSubVideo() {
    }

    @Ignore
    public ProductSubVideo(int product_id, String videoUrl) {
        this.product_id = product_id;
        this.videoUrl = videoUrl;

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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int getItemType() {
        return ItemMedia.VideoType;
    }
}
