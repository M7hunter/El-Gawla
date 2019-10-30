package it_geeks.info.elgawla.repository.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.elgawla.repository.Models.Trans;

@Dao
public interface TransDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTranses(List<Trans> transList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrans(Trans trans);

    @Delete
    void removeTrans(Trans lang);

    @Delete
    void removeTranses(List<Trans> transList);

    @Query("SELECT * FROM Trans")
    List<Trans> getTranses();

    @Query("SELECT trans FROM Trans WHERE `key` = :key AND lang = :lang")
    String getTransByKeyAndLang(String key, String lang);

    @Query("SELECT * FROM Trans WHERE `key` = :key")
    List<Trans> getTransesByKey(String key);
}
