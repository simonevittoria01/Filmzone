package model;

import java.sql.*;
import java.util.ArrayList;

public class RecensioneDAO {


    public void doSave(Recensione r) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO recensione (utente, film, voto, commento) VALUES(?,?,?,?);");
            ps.setInt(1, r.getUtente());
            ps.setInt(2, r.getFilm());
            ps.setFloat(3, r.getVoto());
            ps.setString(4, r.getCommento());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public ArrayList<Recensione> doRetrieveRecensioniByFilm(int filmId){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT utente, film, voto, commento FROM recensione WHERE film=(?);");
            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            ArrayList<Recensione> recensioni = new ArrayList<>();
            while (rs.next()) {
                Recensione r = new Recensione();
                r.setUtente(rs.getInt(1));
                r.setFilm(rs.getInt(2));
                r.setVoto(rs.getFloat(3));
                r.setCommento(rs.getString(4));

                recensioni.add(r);
            }
            return recensioni;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public boolean checkRecensioneEsistente(int idUtente, int idFilm){

        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT utente FROM recensione WHERE utente=(?) AND film=(?);");
            ps.setInt(1, idUtente);
            ps.setInt(2, idFilm);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return true;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
