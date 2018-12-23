package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;

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
