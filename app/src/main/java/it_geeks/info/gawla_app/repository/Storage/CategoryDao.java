package it_geeks.info.gawla_app.repository.Storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it_geeks.info.gawla_app.repository.Models.Category;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> CategoriesList);

    @Delete
    void removeCategory(Category category);

    @Delete
    void removeCategories(List<Category> CategoriesList);

    @Query("SELECT * FROM Category")
    List<Category> getCategories();
}