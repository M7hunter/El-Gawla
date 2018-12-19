package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Round;

@Database(entities = {Round.class, Country.class, ProductSubImage.class}, version = 1, exportSchema = false)
public abstract class GawlaDataBse extends RoomDatabase {

    private static final String DB_NAME = "Gawla_Database.db";
    private static GawlaDataBse INSTANCE;

    // singleton initiation
    public static GawlaDataBse getGawlaDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, GawlaDataBse.class, DB_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }

    // DAOs
    public abstract RoundDao RoundDao();
    public abstract CountryDao CountryDao();
    public abstract ProductImageDao productImageDao();
}
