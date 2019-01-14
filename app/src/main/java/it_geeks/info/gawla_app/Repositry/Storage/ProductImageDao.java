package it_geeks.info.gawla_app.Repositry.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
