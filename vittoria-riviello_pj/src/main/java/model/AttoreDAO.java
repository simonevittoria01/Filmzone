package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttoreDAO {

        /*usare nella scheda specifica di un film*/
    public List<Attore> doRetrieveActorsByFilms(Film f) {
        try (Connection con = ConPool.getConnection()) {

            PreparedStatement ps =
                    con.prepareStatement("SELECT attore.id, attore.nome, attore.cognome FROM attore, recita WHERE attore.id = attore AND film = ?;");
            ps.setInt(1, f.getId());
            ResultSet rs = ps.executeQuery();
            List<Attore> attori = new ArrayList<>();
            while (rs.next()) {
                Attore a = new Attore();
                a.setId(rs.getInt(1));
                a.setNome(rs.getString(2));
                a.setCognome(rs.getString(3));
                attori.add(a);
            }
            return attori;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSave(Attore a) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO attore (nome, cognome) VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, a.getNome());
            ps.setString(2, a.getCognome());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            a.setId(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Attore doRetrieveByNomeCognome(String nome, String cognome) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id FROM attore WHERE nome=(?) AND cognome=(?);");
            ps.setString(1, nome);
            ps.setString(2, cognome);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Attore a = new Attore();
                a.setId(rs.getInt(1));
                a.setNome(nome);
                a.setCognome(cognome);
                return a;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCast(int film, int attore) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO recita (film, attore) VALUES(?,?);");
            ps.setInt(1, film);
            ps.setInt(2, attore);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



}
