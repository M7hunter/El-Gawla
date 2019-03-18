package it_geeks.info.gawla_app.repository.Storage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import it_geeks.info.gawla_app.repository.Models.Card;
import it_geeks.info.gawla_app.repository.Models.Country;
import it_geeks.info.gawla_app.repository.Models.Notification;
import it_geeks.info.gawla_app.repository.Models.ProductSubImage;
import it_geeks.info.gawla_app.repository.Models.Round;
import it_geeks.info.gawla_app.repository.Models.Trans;

@Database(entities = {Round.class, Country.class, Card.class, ProductSubImage.class, Trans.class, Notification.class}, version = 1, exportSchema = false)
public abstract class GawlaDataBse extends RoomDatabase {

    private static GawlaDataBse INSTANCE;
    private static final String DB_NAME = "Gawla_Database.db";

    // migrations
//    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            database.execSQL("CREATE TABLE Round2" +
//                    " (product_id INTEGER not Null, salon_id INTEGER not Null, round_id INTEGER not Null, product_name TEXT, category_name TEXT, category_color TEXT, country_name TEXT, product_commercial_price TEXT, product_product_description TEXT, product_image TEXT, round_date TEXT, status INTEGER not Null, message TEXT," +
//                    " PRIMARY KEY(product_id))");
//
//            database.execSQL("INSERT INTO Round2 (product_id, salon_id, round_id, product_name, category_name, category_color, country_name, product_commercial_price, product_product_description, product_image, round_date, status, message)" +
//                    " SELECT product_id, salon_id, round_id, product_name, category_name, category_color, country_name, product_commercial_price, product_product_description, product_image, round_date, status, message" +
//                    " FROM Round");
//
//            database.execSQL("DROP TABLE Round");
//            database.execSQL("ALTER TABLE Round2 RENAME TO Round");
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

    // singleton initialization
    public static GawlaDataBse getGawlaDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, GawlaDataBse.class, DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration() // resolve this before release -> use migration <-
//                    .addMigrations(MIGRATION_2_3)
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
