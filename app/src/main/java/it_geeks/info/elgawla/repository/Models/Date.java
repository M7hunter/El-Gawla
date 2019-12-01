package it_geeks.info.elgawla.repository.Models;

public class Date {

    private String day_no, month, day, date;
    private int salon_count;
    private boolean is_today;

    public Date(String day_no, String month, String day, String date, int salon_count, boolean is_today) {
        this.day_no = day_no;
        this.month = month;
        this.day = day;
        this.date = date;
        this.salon_count = salon_count;
        this.is_today = is_today;
    }

    public String getDay_no() {
        return day_no;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getDate() {
        return date;
    }

    public int getSalon_count() {
        return salon_count;
    }

    public boolean is_today() {
        return is_today;
    }
}
