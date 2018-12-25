package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

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
}
