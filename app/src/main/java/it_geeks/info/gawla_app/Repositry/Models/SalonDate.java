package it_geeks.info.gawla_app.Repositry.Models;

public class SalonDate {

    private String dayOfMonth;

    private String month;

    private String dayOfWeek;

    private String salonsCount;

    public SalonDate(String dayOfMonth, String month, String dayOfWeek, String salonsCount) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
        this.salonsCount = salonsCount;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getSalonsCount() {
        return salonsCount;
    }

    public void setSalonsCount(String salonsCount) {
        this.salonsCount = salonsCount;
    }
}
