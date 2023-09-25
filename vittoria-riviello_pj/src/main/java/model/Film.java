package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Film {

    private ArrayList<Attore> attori;
    private int id;
    private String nome;
    private int durata;
    private LocalDate dataUscita;
    private String descrizione;
    private byte[] copertina;
    private String genere;
    private Regista regista;

    private float prezzo;

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    @Override
    public String toString() {
        return "Film{" +
                "attori=" + attori +
                ", id=" + id +
                ", nome='" + nome + '\'' +
                ", durata=" + durata +
                ", dataUscita=" + dataUscita +
                ", descrizione='" + descrizione + '\'' +
                ", genere='" + genere + '\'' +
                ", regista=" + regista +
                ", prezzo=" + prezzo +
                '}';
    }

    public ArrayList<Attore> getAttori() {
        return attori;
    }

    public void setAttori(ArrayList<Attore> attori) {
        this.attori = attori;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Film)) return false;
        Film film = (Film) o;
        return getId() == film.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDurata() {
        return durata;
    }

    public void setDurata(int durata) {
        this.durata = durata;
    }

    public LocalDate getDataUscita() {
        return dataUscita;
    }

    public void setDataUscita(LocalDate dataUscita) {
        this.dataUscita = dataUscita;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public byte[] getCopertina() {
        return copertina;
    }

    public void setCopertina(byte[] copertina) {
        this.copertina = copertina;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public Regista getRegista() {
        return regista;
    }

    public void setRegista(Regista regista) {
        this.regista = regista;
    }
}
