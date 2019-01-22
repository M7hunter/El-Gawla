package it_geeks.info.gawla_app.Repositry.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Trans {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String key;

    @ColumnInfo
    private String trans;

    @ColumnInfo
    private String lang;

    public Trans() {
    }

    @Ignore
    public Trans(String key, String trans, String lang) {
        this.key = key;
        this.trans = trans;
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
