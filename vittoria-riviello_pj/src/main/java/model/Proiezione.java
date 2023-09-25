package model;

import java.util.ArrayList;
import java.util.Objects;

public class Proiezione {

    private ArrayList<Biglietto> biglietti;

    private String dataOra;
    private int postiDisponibili;
    private int film;
    private int numSala;

    public ArrayList<Biglietto> getBiglietti() {
        return biglietti;
    }

    public void setBiglietti(ArrayList<Biglietto> biglietti) {
        this.biglietti = biglietti;
    }

    public String getDataOra() {
        return dataOra;
    }

    public void setDataOra(String dataOra) {
        this.dataOra = dataOra;
    }

    public int getPostiDisponibili() {
        return postiDisponibili;
    }

    public void setPostiDisponibili(int postiDisponibili) {
        this.postiDisponibili = postiDisponibili;
    }

    public int getFilm() {
        return film;
    }

    public void setFilm(int film) {
        this.film = film;
    }

    public int getNumSala() {
        return numSala;
    }

    public void setNumSala(int numSala) {
        this.numSala = numSala;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proiezione)) return false;
        Proiezione that = (Proiezione) o;
        return getFilm() == that.getFilm() && getNumSala() == that.getNumSala() && Objects.equals(getDataOra(), that.getDataOra());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataOra(), getFilm(), getNumSala());
    }

    @Override
    public String toString() {
        return "Proiezione{" +
                "dataOra='" + dataOra + '\'' +
                ", postiDisponibili=" + postiDisponibili +
                ", film=" + film +
                ", numSala=" + numSala +
                '}';
    }
}
