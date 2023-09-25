package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    public void doSave(Sala s) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO sala (num_sala, num_posti) VALUES(?,?);");
            ps.setInt(1, s.getNumSala());
            ps.setInt(2, s.getNumPosti());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Sala doRetrieveByNum(int num){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT num_sala, num_posti FROM sala WHERE num_sala=(?);");
            ps.setInt(1, num);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Sala s = new Sala();
                s.setNumSala(rs.getInt(1));
                s.setNumPosti(rs.getInt(2));
                return s;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<Integer> doRetrieveByDate(String dataInizio){

        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT num_sala FROM sala WHERE num_sala NOT IN "
                            + "(SELECT num_sala FROM proiezione WHERE data_ora = (?))");
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


}
