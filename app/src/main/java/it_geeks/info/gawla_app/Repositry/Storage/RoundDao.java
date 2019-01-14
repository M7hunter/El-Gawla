package it_geeks.info.gawla_app.Repositry.Storage;

import java.util.List;

import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.gawla_app.Repositry.Models.Round;

@Dao
public interface RoundDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoundList(List<Round> roundList);

    @Delete
    void removeRound(Round round);

    @Delete
    void removeRounds(List<Round> rounds);

    @Query("SELECT * FROM Round")
    DataSource.Factory<Integer, Round> getRoundsPaged();

    @Query("SELECT * FROM Round")
    List<Round> getRounds(); // TODO: change returned value to 'LiveData<List<Round>>'

    @Query("SELECT * FROM Round WHERE round_date = :date")
    List<Round> getRoundsByDate(String date);

    @Query("SELECT DISTINCT round_date FROM Round")
    List<String> getRoundsDates();

    @Query("SELECT COUNT(round_date) FROM Round WHERE round_date = :date")
    String getDatesCount(String date);

}
