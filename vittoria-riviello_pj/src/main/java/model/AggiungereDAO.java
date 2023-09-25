package model;

import java.sql.*;
import java.util.ArrayList;

public class AggiungereDAO {

    public void firstAggiunta(Ordine o, Proiezione p){
        BigliettoDAO bdao = new BigliettoDAO();
        Biglietto last = bdao.doRetriveLastTicket(p);
        char let = 'A';
        int posto = 1;
        /*non e la prima aggiunta*/
        if(last != null){
            let = last.getFila().charAt(0);
            posto = last.getPosto();
            if(posto == 10){
                let++;
                posto = 1;
            }
            else {
                posto++;
            }
        }
        String fila = String.valueOf(let);
        Biglietto b = new Biglietto();
        b.setFila(fila);
        b.setPosto(posto);
        FilmDAO fdao = new FilmDAO();
        Film f = fdao.doRetrieveById(p.getFilm());
        b.setFilm(f);
        b.setDataOra(p.getDataOra());
        b.setNumSala(p.getNumSala());
        b.setPrezzo(f.getPrezzo());
        bdao.doSave(b);
        aggiungiAggiunta(b,o);
    }

    public void Aggiunta(Ordine o, Proiezione p, int quantita){
        BigliettoDAO bdao = new BigliettoDAO();
        Biglietto last = bdao.doRetriveLastTicket(p);

        /*si presuppone che le file siano tutte da 10 posti*/
        char let = last.getFila().charAt(0);
        int lastPosto = last.getPosto();
        for(int i = 0; i<quantita;i++)
        {
            Biglietto b = new Biglietto();
            if(lastPosto == 10) {
                b.setPosto(1);
                lastPosto = 1;
                let++;
                String fila = String.valueOf(let);
                b.setFila(fila);
            }else {
                lastPosto=lastPosto+1;
                b.setPosto(lastPosto);
                String fila = String.valueOf(let);
                b.setFila(fila);
            }
            FilmDAO fdao = new FilmDAO();
            Film f = fdao.doRetrieveById(p.getFilm());
            b.setFilm(f);
            b.setDataOra(p.getDataOra());
            b.setNumSala(p.getNumSala());
            b.setPrezzo(f.getPrezzo());
            bdao.doSave(b);
            aggiungiAggiunta(b,o);
        }
    }

        /*passo ordine perche ordine e collegato ad un solo utente
        * elimina tutti i biglietti che sono stati tolti dal carrello senza ancora averli acquistati*/
    public void rimuoviAllAggiunta(Proiezione p, Ordine o){
        try (Connection con = ConPool.getConnection()) {
            Statement st = con.createStatement();
            String query = "DELETE biglietto FROM biglietto, aggiungere, ordine, proiezione WHERE biglietto.id = aggiungere.biglietto AND aggiungere.ordine = ordine.id AND biglietto.film = proiezione.film AND biglietto.data_ora = proiezione.data_ora AND biglietto.num_sala = proiezione.num_sala AND ordine.id = "+o.getId()+" AND proiezione.data_ora = '"+p.getDataOra()+"' AND proiezione.num_sala = "+p.getNumSala()+" AND proiezione.film = "+p.getFilm()+" AND ordine.data_acquisto IS NULL;";
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void aggiungiAggiunta(Biglietto b, Ordine o){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO aggiungere (biglietto, ordine) VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,b.getId());
            ps.setInt(2, o.getId());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

/*Rimuove in base alla select*/
    public void removeAggiunta(Proiezione p, Ordine o, int quantita){
        try (Connection con = ConPool.getConnection()) {
            Statement st = con.createStatement();
            String query = "DROP VIEW IF EXISTS vista_el;";
            st.executeUpdate(query);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

/*creo la vista per eliminare una data quantita di biglietti, in particolare gli ultimi aggiunti*/
        try (Connection con = ConPool.getConnection()) {
            Statement st = con.createStatement();
            String query = "CREATE VIEW vista_el AS SELECT biglietto.id FROM biglietto, aggiungere, ordine, proiezione WHERE biglietto.id = aggiungere.biglietto AND aggiungere.ordine = ordine.id AND biglietto.film = proiezione.film AND biglietto.data_ora = proiezione.data_ora AND biglietto.num_sala = proiezione.num_sala AND ordine.id = "+o.getId()+" AND proiezione.data_ora = '"+p.getDataOra()+"' AND proiezione.num_sala = "+p.getNumSala()+" AND proiezione.film = "+p.getFilm()+" AND ordine.data_acquisto IS NULL ORDER BY biglietto.id DESC LIMIT "+quantita+";";
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
/*elimino definitivamente i biglietti*/
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps2 = con.prepareStatement("DELETE FROM biglietto WHERE id IN (SELECT id FROM vista_el);");
            ps2.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
