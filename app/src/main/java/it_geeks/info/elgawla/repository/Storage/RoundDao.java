package it_geeks.info.elgawla.repository.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.elgawla.repository.Models.Salon;

@Dao
public interface RoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoundList(List<Salon> salonList);

    @Delete
    void removeRound(Salon salon);

    @Delete
    void removeRounds(List<Salon> salons);

    @Query("SELECT * FROM Salon")
    List<Salon> getRounds(); // TODO: change returned value to 'LiveData<List<Salon>>'

    @Query("SELECT * FROM Salon WHERE salon_date = :date")
    List<Salon> getRoundsByDate(String date);

    @Query("SELECT * FROM Salon WHERE salon_id = :salonID")
    Salon getRoundByID(int salonID);

    @Query("SELECT * FROM Salon WHERE category_name = :categoryName")
    List<Salon> getRoundsByCategory(String categoryName);

    @Query("SELECT DISTINCT salon_date FROM Salon")
    List<String> getRoundsDates();

    @Query("SELECT DISTINCT category_name FROM Salon")
    List<String> getRoundsCategories();

    @Query("SELECT COUNT(salon_date) FROM Salon WHERE salon_date = :date")
    String getDatesCount(String date);

}