package model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProiezioneDAO {
    public Proiezione doRetrieveProiezione(int idfilm, String data_ora, int num_sala) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT data_ora, posti_disponibili, film, num_sala FROM proiezione WHERE film=(?) AND data_ora = (?) AND num_sala = (?);");
            ps.setInt(1, idfilm);
            ps.setString(2, data_ora);
            ps.setInt(3, num_sala);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Proiezione p = new Proiezione();
                p.setDataOra(rs.getString(1));
                p.setPostiDisponibili(rs.getInt(2));
                p.setFilm(rs.getInt(3));
                p.setNumSala(rs.getInt(4));
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
/* usare nella stampa di tutti le programmazioni di tutti i film*/
    public List<Proiezione> doRetrieveAll() {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT data_ora, posti_disponibili, film, num_sala FROM proiezione WHERE posti_disponibili>0;");
            ResultSet rs = ps.executeQuery();
            List<Proiezione> programmate= new ArrayList<>();
            while (rs.next()) {
                Proiezione p = new Proiezione();
                p.setDataOra(rs.getString(1));
                p.setPostiDisponibili(rs.getInt(2));
                p.setFilm(rs.getInt(3));
                p.setNumSala(rs.getInt(4));
                programmate.add(p);
            }
            return programmate;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Integer> doRetrieveByDate(String dataInizio){

        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT num_sala FROM proiezione WHERE data_ora=(?);");
            ps.setString(1, dataInizio);
            ResultSet rs = ps.executeQuery();
            ArrayList<Integer> numSale = new ArrayList<>();
            while (rs.next()) {
                numSale.add(rs.getInt(1));
            }
            return numSale;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

/*usare nella scelta della programmazione*/
    public ArrayList<Proiezione> dataProiezioneFilm(int id){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT data_ora, num_sala, posti_disponibili FROM proiezione WHERE film=(?) AND posti_disponibili>0 AND data_ora>= NOW()");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ArrayList<Proiezione> proiezioni= new ArrayList<>();
            while (rs.next()) {
                Proiezione p = new Proiezione();
                p.setDataOra(rs.getString(1));
                p.setNumSala(rs.getInt(2));
                p.setPostiDisponibili(rs.getInt(3));
                p.setFilm(id);
                proiezioni.add(p);
            }
            return proiezioni;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



/*inserimento nuova proiezione di un film*/
    public void doSave(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO proiezione (data_ora, posti_disponibili, film, num_sala) VALUES(?, ?, ?, ?);");

            ps.setString(1, p.getDataOra());
            ps.setInt(2, p.getPostiDisponibili());
            ps.setInt(3, p.getFilm());
            ps.setInt(4, p.getNumSala());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*da usare alla conferma dell'acquisto del/i biglietto/i per diminuire i posti disponibili della proiezione
    * se i posti disponibili terminano, vengono eliminati dal db tutti i biglietti non acquistati definitivamente*/
    public void updatePosti(Proiezione p, int acquisti, Ordine o){
        try (Connection con = ConPool.getConnection()) {
            int quantita = p.getPostiDisponibili()-acquisti;
            Statement st = con.createStatement();
            String query = "UPDATE proiezione SET posti_disponibili="+quantita+" WHERE num_sala = "+p.getNumSala()+" AND data_ora = '"+p.getDataOra()+"' AND film = "+p.getFilm()+";";
            st.executeUpdate(query);
            /*se sono finiti i posti elimino tutti i biglietti in sospeso*/
            if(quantita == 0){
                BigliettoDAO bdao = new BigliettoDAO();
                //in questo array ci saranno tutti i biglietti non acquistati di una proiezione, ma presenti in carrelli non confermati
                ArrayList<Biglietto> biglietti = bdao.doRetriveBigliettiEccesso(p);
                for(Biglietto b: biglietti){
                    query = "UPDATE aggiungere SET ordine = "+o.getId()+" WHERE biglietto = "+b.getId()+";";
                    st.executeUpdate(query);
                }
                query = "DELETE FROM biglietto WHERE num_sala = "+p.getNumSala()+" AND data_ora = '"+p.getDataOra()+"' AND film = "+p.getFilm()+" AND fila >='K';";
                st.executeUpdate(query);
                //query per togliere tutti i rimanenti biglietti per posti finiti
                query = "DELETE biglietto FROM biglietto, aggiungere, ordine, proiezione WHERE biglietto.id = aggiungere.biglietto AND aggiungere.ordine = ordine.id AND biglietto.film = proiezione.film AND biglietto.data_ora = proiezione.data_ora AND biglietto.num_sala = proiezione.num_sala AND proiezione.data_ora = '"+p.getDataOra()+"' AND proiezione.num_sala = "+p.getNumSala()+" AND proiezione.film = "+p.getFilm()+" AND ordine.utente != "+o.getUtente()+" AND ordine.data_acquisto IS NULL;";
                st.executeUpdate(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Proiezione doRetrievebyBiglietto(Biglietto b) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT data_ora, posti_disponibili, film, num_sala FROM proiezione WHERE film=(?) AND data_ora = (?) AND num_sala = (?);");
            ps.setInt(1, b.getFilm().getId());
            ps.setString(2, b.getDataOra());
            ps.setInt(3, b.getNumSala());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Proiezione p = new Proiezione();
                p.setDataOra(rs.getString(1));
                p.setPostiDisponibili(rs.getInt(2));
                p.setFilm(rs.getInt(3));
                p.setNumSala(rs.getInt(4));
                return p;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteProiezione(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM proiezione WHERE film = (?) AND num_sala = (?) AND data_ora = (?);");
            ps.setInt(1, p.getFilm());
            ps.setInt(2, p.getNumSala());
            ps.setString(3, p.getDataOra());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("DELETE error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
