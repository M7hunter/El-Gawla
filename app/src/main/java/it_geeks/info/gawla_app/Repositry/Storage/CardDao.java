package it_geeks.info.gawla_app.Repositry.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.gawla_app.Repositry.Models.Card;

@Dao
public interface CardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCards(List<Card> cardsList);

    @Delete
    void removeCard(Card card);

    @Delete
    void removeCards(List<Card> cardsList);

    @Query("SELECT * FROM Card")
    List<Card> getCards();

    @Query("SELECT * FROM Card WHERE salon_id = :salonId")
    List<Card> getCardsById(int salonId);
}
