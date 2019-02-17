package it_geeks.info.gawla_app.Repositry.Storage;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import it_geeks.info.gawla_app.Repositry.Models.Notifications;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(List<Notifications> notifications);

    @Update
    void removeNotification(List<Notifications> notifications);

    @Query("DELETE FROM Notifications")
    void removeNotifications();

    @Query("Select * From Notifications")
    LiveData<List<Notifications>> selectAllNotification();

    @Query("UPDATE Notifications SET status = :status")
    void updateStatusNotification(boolean status);

    @Query("SELECT * FROM Notifications where status = :status")
    LiveData<List<Notifications>> getStatusNotification(boolean status);


}

