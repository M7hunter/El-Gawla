package it_geeks.info.elgawla.repository.Storage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it_geeks.info.elgawla.repository.Models.Category;

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<Category> CategoriesList);

    @Query("SELECT * FROM Category")
    LiveData<List<Category>> getCategories();
}