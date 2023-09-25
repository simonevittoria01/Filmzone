package model;

import java.util.ArrayList;

public class Sala {

    private ArrayList<Proiezione> proiezioni;
    private int numSala;
    private int numPosti;

    public ArrayList<Proiezione> getProiezioni() {
        return proiezioni;
    }

    public void setProiezioni(ArrayList<Proiezione> proiezioni) {
        this.proiezioni = proiezioni;
    }

    public int getNumSala() {
        return numSala;
    }

    public void setNumSala(int numSala) {
        this.numSala = numSala;
    }

    public int getNumPosti() {
        return numPosti;
    }

    public void setNumPosti(int numPosti) {
        this.numPosti = numPosti;
    }
}
