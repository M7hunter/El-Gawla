package it_geeks.info.gawla_app.Repositry.Storage;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Round;

@Database(entities = {Round.class, Country.class, Card.class, ProductSubImage.class}, version = 3, exportSchema = false)
public abstract class GawlaDataBse extends RoomDatabase {

    private static GawlaDataBse INSTANCE;
    private static final String DB_NAME = "Gawla_Database.db";
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Card "
                    + " ADD COLUMN card_category TEXT");
        }
    };

    // singleton initiation
    public static GawlaDataBse getGawlaDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, GawlaDataBse.class, DB_NAME)
                    .allowMainThreadQueries()
//                    .fallbackToDestructiveMigration() // resolve this before release -> use migration <-
                    .addMigrations(MIGRATION_2_3)
                    .build();
        }
        return INSTANCE;
    }

    // DAOs
    public abstract RoundDao roundDao();
    public abstract CountryDao countryDao();
    public abstract ProductImageDao productImageDao();
    public abstract CardDao cardDao();
}
