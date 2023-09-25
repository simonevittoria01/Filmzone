package model;

public class Biglietto {

    private int id;
    private String fila;
    private int posto;
    private float prezzo;
    private int numSala;
    private String dataOra;
    private Film film;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }

    public int getPosto() {
        return posto;
    }

    public void setPosto(int posto) {
        this.posto = posto;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public int getNumSala() {
        return numSala;
    }

    public void setNumSala(int numSala) {
        this.numSala = numSala;
    }

    public String getDataOra() {
        return dataOra;
    }

    public void setDataOra(String dataOra) {
        this.dataOra = dataOra;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @Override
    public String toString() {
        return "Biglietto{" +
                "id=" + id +
                ", fila='" + fila + '\'' +
                ", posto=" + posto +
                ", prezzo=" + prezzo +
                ", numSala=" + numSala +
                ", dataOra='" + dataOra + '\'' +
                ", film=" + film +
                '}';
    }
}
