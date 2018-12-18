package it_geeks.info.gawla_app.Repositry.Models;

import java.util.List;

public class Salons {

    private String header;

    private List<Round> rounds;

    public Salons(String header, List<Round> rounds) {
        this.header = header;
        this.rounds = rounds;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }
}
