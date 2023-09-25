package model;

import java.sql.*;
import java.util.ArrayList;

public class OrdineDAO {

    //storico ordini
    public ArrayList<Ordine> doRetrieveOrdiniConfermatiByUtente(Utente u){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, data_acquisto, utente FROM ordine WHERE utente=(?) AND data_acquisto IS NOT NULL;");
            ps.setInt(1, u.getId());
            ResultSet rs = ps.executeQuery();
            ArrayList<Ordine> ordini = new ArrayList<>();
            while (rs.next()) {
                Ordine o = new Ordine();
                o.setId(rs.getInt(1));
                o.setDataAcquisto(rs.getString(2));
                o.setUtente(rs.getInt(3));

                ordini.add(o);
            }
            return ordini;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //visualizza carrello
    public Ordine doRetrieveCarrello(Utente u){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, data_acquisto, utente FROM ordine WHERE utente=(?) AND data_acquisto IS NULL;");
            ps.setInt(1, u.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ordine o = new Ordine();
                o.setId(rs.getInt(1));
                o.setDataAcquisto(rs.getString(2));
                o.setUtente(rs.getInt(3));
                return o;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
            /*usato per i non confermati*/
    public Ordine doRetrievebyId(int id){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, data_acquisto, utente FROM ordine WHERE id=(?) AND data_acquisto IS NULL;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ordine o = new Ordine();
                o.setId(rs.getInt(1));
                o.setDataAcquisto(rs.getString(2));
                o.setUtente(rs.getInt(3));
                return o;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Ordine doRetrievebyIdConfirmed(int id){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id, data_acquisto, utente FROM ordine WHERE id=(?) AND data_acquisto IS NOT NULL;");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ordine o = new Ordine();
                o.setId(rs.getInt(1));
                o.setDataAcquisto(rs.getString(2));
                o.setUtente(rs.getInt(3));
                return o;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //creazione carrello
    public void doSave(Ordine o) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO ordine (utente) VALUES(?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, o.getUtente());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            o.setId(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //acquisto confermato
    public void doUpdate(Ordine o) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE ordine SET data_acquisto = CURDATE() WHERE id = (?);");
            ps.setInt(1, o.getId());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("UPDATE error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doDelete(int id){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM ordine WHERE id=(?);");
            ps.setInt(1, id);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("DELETE error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
