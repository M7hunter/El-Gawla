package it_geeks.info.gawla_app.repository.Storage;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import androidx.room.Update;
import it_geeks.info.gawla_app.repository.Models.Notification;

@Dao
public interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotification(List<Notification> notifications);

    @Delete()
    void removeNotifications(List<Notification> notifications);

    @Query("Select * From Notification")
    List<Notification> getAllNotification();

    @Query("UPDATE Notification SET status = :status")
    void updateStatusNotification(boolean status);

    @Query("SELECT * FROM Notification where status = :status")
    LiveData<List<Notification>> getStatusNotification(boolean status);
}

