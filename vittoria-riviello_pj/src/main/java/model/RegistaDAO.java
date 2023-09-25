package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistaDAO {

    public Regista doRetrieveByNomeCognome(String nome, String cognome) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT id FROM regista WHERE nome=(?) AND cognome=(?);");
            ps.setString(1, nome);
            ps.setString(2, cognome);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Regista r = new Regista();
                r.setId(rs.getInt(1));
                r.setNome(nome);
                r.setCognome(cognome);
                return r;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void doSave(Regista r) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO regista (nome, cognome) VALUES(?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, r.getNome());
            ps.setString(2, r.getCognome());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            r.setId(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
