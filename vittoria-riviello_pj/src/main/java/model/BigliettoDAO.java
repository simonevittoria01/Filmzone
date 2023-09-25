package model;

import java.sql.*;
import java.util.ArrayList;

public class BigliettoDAO {

    public void doDelete(Biglietto b){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM biglietto WHERE id=(?);");
            ps.setInt(1, b.getId());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("DELETE error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSave(Biglietto b) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO biglietto (fila, posto, prezzo, num_sala, data_ora, film) VALUES(?,?,?,?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, b.getFila());
            ps.setInt(2, b.getPosto());
            ps.setFloat(3, b.getPrezzo());
            ps.setInt(4, b.getNumSala());
            ps.setString(5, b.getDataOra());
            ps.setInt(6, b.getFilm().getId());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            b.setId(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
/*controlla se ci sono biglietti nel db di una proiezione con fila dopo la J che e l'ultima, ciò significherebbe eccessi*/
    public int filaEccesso(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT Count(*) FROM biglietto WHERE data_ora = ? AND film = ? AND num_sala = ? AND fila >= 'K';");
            ps.setString(1, p.getDataOra());
            ps.setInt(2, p.getFilm());
            ps.setInt(3, p.getNumSala());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Biglietto> doRetrieveBigliettiByProiezione(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, fila, posto, prezzo, num_sala, data_ora, film FROM biglietto WHERE film=(?) AND data_ora=(?) AND num_sala=(?)");
            ps.setInt(1, p.getFilm());
            ps.setString(2, p.getDataOra());
            ps.setInt(3, p.getNumSala());
            ResultSet rs = ps.executeQuery();
            ArrayList<Biglietto> biglietti = new ArrayList<>();

            while (rs.next()) {
                Biglietto b = new Biglietto();
                b.setId(rs.getInt(1));
                b.setFila(rs.getString(2));
                b.setPosto(rs.getInt(3));
                b.setPrezzo(rs.getFloat(4));
                b.setNumSala(rs.getInt(5));
                b.setDataOra(rs.getString(6));
                FilmDAO fdao = new FilmDAO();
                Film f = fdao.doRetrieveById(rs.getInt(7));
                b.setFilm(f);

                biglietti.add(b);
            }
            return biglietti;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Biglietto> doRetriveBigliettiEccesso(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            //filaEccesso(p) restituisce il numero di biglietti in eccesso della proiezione, ovvero quelli con fila > j
            int righe = filaEccesso(p);
            ArrayList<Biglietto> biglietti = new ArrayList<>();  //conterrà tutti i biglietti in eccesso non acquistati definitivamente di una proiezione
            if(righe!=0){
                PreparedStatement ps = con.prepareStatement("SELECT b.id, b.fila, b.posto, b.prezzo, b.num_sala, b.data_ora, b.film FROM biglietto b, aggiungere a, ordine o WHERE b.id = a.biglietto AND a.ordine = o.id AND o.data_acquisto IS NULL AND b.data_ora = ? AND b.film = ? AND b.num_sala = ?;");
                ps.setString(1, p.getDataOra());
                ps.setInt(2, p.getFilm());
                ps.setInt(3, p.getNumSala());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    Biglietto b = new Biglietto();
                    b.setId(rs.getInt(1));
                    b.setFila(rs.getString(2));
                    b.setPosto(rs.getInt(3));
                    b.setPrezzo(rs.getFloat(4));
                    b.setNumSala(rs.getInt(5));
                    b.setDataOra(rs.getString(6));
                    FilmDAO fdao = new FilmDAO();
                    Film f = fdao.doRetrieveById(rs.getInt(7));
                    b.setFilm(f);
                    biglietti.add(b);
                }
            }
            return biglietti;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Biglietto> doRetrieveBigliettiByOrdine(Ordine o){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, fila, posto, prezzo, num_sala, data_ora, film FROM biglietto JOIN aggiungere ON (id = biglietto) WHERE ordine=(?) ORDER BY film, data_ora, fila, posto;");
            ps.setInt(1, o.getId());

            ResultSet rs = ps.executeQuery();
            ArrayList<Biglietto> biglietti = new ArrayList<>();

            while (rs.next()) {
                Biglietto b = new Biglietto();
                b.setId(rs.getInt(1));
                b.setFila(rs.getString(2));
                b.setPosto(rs.getInt(3));
                b.setPrezzo(rs.getFloat(4));
                b.setNumSala(rs.getInt(5));
                b.setDataOra(rs.getString(6));
                FilmDAO fdao = new FilmDAO();
                Film f = fdao.doRetrieveById(rs.getInt(7));
                b.setFilm(f);
                biglietti.add(b);
            }
            return biglietti;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Biglietto doRetriveLastTicket(Proiezione p){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("DROP VIEW IF EXISTS mia_view;");
            ps.executeUpdate();
            ps =
                    con.prepareStatement("CREATE VIEW mia_view AS SELECT id, fila, posto, prezzo, num_sala, data_ora, film FROM biglietto WHERE num_sala = ? AND data_ora = ? AND film = ?;");
            ps.setInt(1, p.getNumSala());
            ps.setString(2, p.getDataOra());
            ps.setInt(3, p.getFilm());
            ps.executeUpdate();

            ps = con.prepareStatement("SELECT * FROM mia_view WHERE id = (SELECT max(id) from mia_view);");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Biglietto b = new Biglietto();
                b.setId(rs.getInt(1));
                b.setFila(rs.getString(2));
                b.setPosto(rs.getInt(3));
                b.setPrezzo(rs.getFloat(4));
                b.setNumSala(rs.getInt(5));
                b.setDataOra(rs.getString(6));
                FilmDAO fdao = new FilmDAO();
                Film f = fdao.doRetrieveById(rs.getInt(7));
                b.setFilm(f);
                return b;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public int doRetrivebyProiezioneUtente(Proiezione p, Ordine o){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT Count(biglietto.id) FROM biglietto, proiezione, aggiungere, ordine WHERE biglietto.data_ora = proiezione.data_ora AND biglietto.num_sala = proiezione.num_sala AND biglietto.film = proiezione.film AND biglietto.id = aggiungere.biglietto AND aggiungere.ordine = ordine.id AND ordine.utente = ? AND proiezione.data_ora = ? AND proiezione.num_sala = ? AND proiezione.film = ? AND ordine.id = ?;");
            ps.setInt(1, o.getUtente());
            ps.setString(2, p.getDataOra());
            ps.setInt(3, p.getNumSala());
            ps.setInt(4, p.getFilm());
            ps.setInt(5, o.getId());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePriceTicket(float prezzo, int id){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("UPDATE biglietto SET prezzo = ? WHERE film = ? AND id IN (SELECT biglietto FROM aggiungere WHERE ordine IN (SELECT id FROM ordine WHERE data_acquisto IS NULL));");
            ps.setFloat(1, prezzo);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        /*la funzione recupera un biglietto di un altro ordine non ancora confermato ma
        che abbia: la stessa proiezione del biglietto passato come parametro e un posto valido
        Viene poi chiamata la changeConfirm che effettua il cambio biglietto*/
    public void changeTicket(Biglietto b , int ordine){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT biglietto.id, ordine.id FROM biglietto, aggiungere, ordine WHERE biglietto.id = aggiungere.biglietto AND aggiungere.ordine = ordine.id AND biglietto.film = ? AND biglietto.data_ora = ? AND biglietto.num_sala = ? AND biglietto.fila <= 'J' AND ordine.id != ? AND ordine.data_acquisto IS NULL LIMIT 1;");
            ps.setInt(1, b.getFilm().getId());
            ps.setString(2, b.getDataOra());
            ps.setInt(3, b.getNumSala());
            ps.setInt(4, ordine);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {

                int biglietto = rs.getInt(1);
                int idOrd = rs.getInt(2);
                System.out.println("Trovato biglietto:"+biglietto+ " " +idOrd);
                changeConfirm(ordine, idOrd, b.getId(), biglietto);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
        /*funzione che si lega alla preccedente*/
    public void changeConfirm(int ordineUtente, int Ordinechange, int bigliettoUtente, int bigliettoChange){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("UPDATE aggiungere SET biglietto = ? WHERE ordine = ? AND biglietto = ?;");
            ps.setInt(1, bigliettoChange);
            ps.setInt(2, ordineUtente);
            ps.setInt(3, bigliettoUtente);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //cambio dell altro biglietto
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps1 =
                    con.prepareStatement("UPDATE aggiungere SET biglietto = ? WHERE ordine = ? AND biglietto = ?;");
            ps1.setInt(1, bigliettoUtente);
            ps1.setInt(2, Ordinechange);
            ps1.setInt(3, bigliettoChange);
            ps1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
