package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenereDAO {
    /*da usare nell'inserimento di un nuovo film*/
    public List<Genere> doRetrieveAll() {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT nome FROM genere;");
            ResultSet rs = ps.executeQuery();
            List<Genere> generi= new ArrayList<>();
            while (rs.next()) {
                Genere g = new Genere();
                g.setNome(rs.getString(1));
                generi.add(g);
            }
            return generi;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
