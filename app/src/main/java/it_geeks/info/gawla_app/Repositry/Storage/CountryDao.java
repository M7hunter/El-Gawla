package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import it_geeks.info.gawla_app.Repositry.Models.Country;

@Dao
public interface CountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCountryList(List<Country> CountryList);

    @Delete
    void removeCountry(Country country);

    @Delete
    void removeCountries(List<Country> Countries);

    @Query("SELECT * FROM Country")
    List<Country> getCountries();

    @Query("SELECT * FROM Country where country_id = :countryID")
    Country getCountryByID(int countryID);
}
