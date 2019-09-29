package it_geeks.info.elgawla.repository.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.elgawla.repository.Models.Round;

@Dao
public interface RoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoundList(List<Round> roundList);

    @Delete
    void removeRound(Round round);

    @Delete
    void removeRounds(List<Round> rounds);

    @Query("SELECT * FROM Round")
    List<Round> getRounds(); // TODO: change returned value to 'LiveData<List<Round>>'

    @Query("SELECT * FROM Round WHERE round_date = :date")
    List<Round> getRoundsByDate(String date);

    @Query("SELECT * FROM Round WHERE salon_id = :salonID")
    Round getRoundByID(int salonID);

    @Query("SELECT * FROM Round WHERE category_name = :categoryName")
    List<Round> getRoundsByCategory(String categoryName);

    @Query("SELECT DISTINCT round_date FROM Round")
    List<String> getRoundsDates();

    @Query("SELECT DISTINCT category_name FROM Round")
    List<String> getRoundsCategories();

    @Query("SELECT COUNT(round_date) FROM Round WHERE round_date = :date")
    String getDatesCount(String date);

}