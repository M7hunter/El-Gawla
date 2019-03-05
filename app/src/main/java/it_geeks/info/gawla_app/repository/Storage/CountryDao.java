package it_geeks.info.gawla_app.repository.Storage;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import it_geeks.info.gawla_app.repository.Models.Country;

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

    @Query("SELECT country_title FROM Country")
    List<String> getCountriesNames();

    @Query("SELECT * FROM Country where country_title = :countryName")
    Country getCountryByName(String countryName);

    @Query("SELECT country_title FROM Country where country_id = :countryId")
    String getCountryNameByID(int countryId);
}
