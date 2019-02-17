package it_geeks.info.gawla_app.Repositry.Storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import it_geeks.info.gawla_app.Repositry.Models.Card;
import it_geeks.info.gawla_app.Repositry.Models.Country;
import it_geeks.info.gawla_app.Repositry.Models.Notifications;
import it_geeks.info.gawla_app.Repositry.Models.ProductSubImage;
import it_geeks.info.gawla_app.Repositry.Models.Round;
import it_geeks.info.gawla_app.Repositry.Models.Trans;

@Database(entities = {Round.class, Country.class, Card.class, ProductSubImage.class, Trans.class , Notifications.class}, version = 1, exportSchema = false)
public abstract class GawlaDataBse extends RoomDatabase {

    private static GawlaDataBse INSTANCE;
    private static final String DB_NAME = "Gawla_Database.db";

    // migrations
//    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE Card "
//                    + " ADD COLUMN card_category TEXT");
//        }
//    };
//
//    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE 'Trans' "
//                    + "('id' INTEGER, 'key' TEXT not null , 'trans' TEXT, 'lang' TEXT, PRIMARY KEY ('id'))");
//        }
//    };

    // singleton initiation
    public static GawlaDataBse getGawlaDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, GawlaDataBse.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // resolve this before release -> use migration <-
//                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return INSTANCE;
    }

    // DAOs
    public abstract RoundDao roundDao();
    public abstract CountryDao countryDao();
    public abstract ProductImageDao productImageDao();
    public abstract CardDao cardDao();
    public abstract TransDao transDao();
    public abstract NotificationDao notificationDao();
}
