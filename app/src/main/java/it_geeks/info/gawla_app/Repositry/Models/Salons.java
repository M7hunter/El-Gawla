package it_geeks.info.gawla_app.Repositry.Models;

import java.util.List;

public class Salons {

    private List<Round> rounds;
    private List<SalonDate> salonDates;

    public Salons(List<Round> rounds, List<SalonDate> salonDates) {
        this.rounds = rounds;
        this.salonDates = salonDates;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public List<SalonDate> getSalonDates() {
        return salonDates;
    }

    public void setSalonDates(List<SalonDate> salonDates) {
        this.salonDates = salonDates;
    }
}
