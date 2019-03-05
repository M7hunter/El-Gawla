package it_geeks.info.gawla_app.repository.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Notifications {
    @PrimaryKey(autoGenerate = true)
    private int notificationID;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String body;
    @ColumnInfo
    private String type;
    @ColumnInfo
    private String date;
    @ColumnInfo
    private int id;
    @ColumnInfo
    private boolean status;

    public Notifications() {
    }

    @Ignore
    public Notifications(String title, String body, String type, String date, int id, boolean status) {
        this.title = title;
        this.body = body;
        this.type = type;
        this.date = date;
        this.id = id;
        this.status = status;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}