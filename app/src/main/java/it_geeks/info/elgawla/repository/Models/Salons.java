package it_geeks.info.elgawla.repository.Models;

import java.util.List;

public class Salons {

    private List<Salon> salons;
    private List<SalonDate> salonDates;

    public Salons(List<Salon> salons, List<SalonDate> salonDates) {
        this.salons = salons;
        this.salonDates = salonDates;
    }

    public List<Salon> getSalons() {
        return salons;
    }

    public void setSalons(List<Salon> salons) {
        this.salons = salons;
    }

    public List<SalonDate> getSalonDates() {
        return salonDates;
    }

    public void setSalonDates(List<SalonDate> salonDates) {
        this.salonDates = salonDates;
    }
}
