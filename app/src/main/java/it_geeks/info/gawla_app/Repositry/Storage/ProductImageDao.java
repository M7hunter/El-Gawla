package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;

@Dao
public interface ProductImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubImages(List<ProductSubImage> subImagesList);

    @Delete
    void removeSubImage(ProductSubImage subImage);

    @Delete
    void removeSubImages(List<ProductSubImage> subImageList);

    @Query("SELECT * FROM ProductSubImage")
    List<ProductSubImage> getSubImages();

    @Query("SELECT * FROM ProductSubImage WHERE product_id = :productId")
    List<ProductSubImage> getSubImagesById(int productId);
}
