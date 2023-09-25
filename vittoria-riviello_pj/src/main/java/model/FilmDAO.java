package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    public Film doRetrieveById(int id) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT film.id, film.nome, film.durata, film.data_uscita, film.descrizione, film.copertina, film.genere, film.prezzo, regista.id, regista.nome, regista.cognome FROM film, regista WHERE film.regista = regista.id AND film.id = (?);");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            AttoreDAO adao = new AttoreDAO();
            if (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setDurata(rs.getInt(3));
                f.setDataUscita(rs.getDate(4).toLocalDate());
                f.setDescrizione(rs.getString(5));
                f.setCopertina(rs.getBytes(6));
                f.setGenere(rs.getString(7));
                f.setPrezzo(rs.getFloat(8));
                Regista r = new Regista();
                r.setId(rs.getInt(9));
                r.setNome(rs.getString(10));
                r.setCognome(rs.getString(11));
                f.setRegista(r);
                f.setAttori((ArrayList<Attore>) adao.doRetrieveActorsByFilms(f));
                return f;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSave(Film f) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO film (nome, durata, data_uscita, descrizione, copertina, genere, regista, prezzo) VALUES(?,?,?,?,?,?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, f.getNome());
            ps.setInt(2, f.getDurata());
            ps.setDate(3, Date.valueOf(f.getDataUscita()));
            ps.setString(4, f.getDescrizione());
            ps.setBytes(5, f.getCopertina());
            ps.setString(6, f.getGenere());
            ps.setInt(7, f.getRegista().getId());
            ps.setFloat(8, f.getPrezzo());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            f.setId(id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Film> doRetrievebyKind(String genere){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT film.id, film.nome, film.copertina FROM film WHERE film.genere=(?);");
            ps.setString(1, genere);
            ResultSet rs = ps.executeQuery();
            List<Film> films = new ArrayList<>();
            while (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setCopertina(rs.getBytes(3));
                films.add(f);
            }
            return films;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Film doRetrievebyKindAndByName(String genere, String nomefilm){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT film.id, film.nome, film.copertina FROM film WHERE film.nome=(?) AND film.genere=(?);");
            ps.setString(1, nomefilm);
            ps.setString(2, genere);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setCopertina(rs.getBytes(3));
                return f;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*film che hanno almeno una proiezione programmata dall'orario di oggi al cinema*/

    public List<Film> filmProiezioni(){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT DISTINCT film.id, film.nome, film.durata, film.data_uscita, film.descrizione, film.copertina, film.genere, film.prezzo, regista.id, regista.nome, regista.cognome FROM film, regista, proiezione WHERE regista.id = film.regista AND film.id = proiezione.film AND proiezione.data_ora>= NOW();");
            ResultSet rs = ps.executeQuery();
            List<Film> films = new ArrayList<>();
            AttoreDAO adao = new AttoreDAO();
            while (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setDurata(rs.getInt(3));
                f.setDataUscita(rs.getDate(4).toLocalDate());
                f.setDescrizione(rs.getString(5));
                f.setCopertina(rs.getBytes(6));
                f.setGenere(rs.getString(7));
                f.setPrezzo(rs.getFloat(8));
                Regista r = new Regista();
                r.setId(rs.getInt(9));
                r.setNome(rs.getString(10));
                r.setCognome(rs.getString(11));
                f.setRegista(r);
                f.setAttori((ArrayList<Attore>) adao.doRetrieveActorsByFilms(f));
                films.add(f);
            }
            return films;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //per verificare se esiste già un film con lo stesso nome
    public Film doRetrieveByNome(String nome) {
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT film.id, film.nome, film.copertina FROM film WHERE film.nome = (?);");
            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setCopertina(rs.getBytes(3));
                return f;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doUpdatePrice(int id, float prezzo){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("UPDATE film SET prezzo = ? WHERE id = ?");
            ps.setFloat(1, prezzo);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Film filmProiezioneById(int filmId){
        try (Connection con = ConPool.getConnection()) {
            PreparedStatement ps =
                    con.prepareStatement("SELECT DISTINCT film.id, film.nome, film.durata, film.data_uscita, film.descrizione, film.copertina, film.genere, film.prezzo, regista.id, regista.nome, regista.cognome FROM film, regista, proiezione WHERE regista.id = film.regista AND film.id = proiezione.film AND proiezione.data_ora>= NOW() AND film.id=(?);");
            ps.setInt(1, filmId);
            ResultSet rs = ps.executeQuery();
            AttoreDAO adao = new AttoreDAO();
            if (rs.next()) {
                Film f = new Film();
                f.setId(rs.getInt(1));
                f.setNome(rs.getString(2));
                f.setDurata(rs.getInt(3));
                f.setDataUscita(rs.getDate(4).toLocalDate());
                f.setDescrizione(rs.getString(5));
                f.setCopertina(rs.getBytes(6));
                f.setGenere(rs.getString(7));
                f.setPrezzo(rs.getFloat(8));
                Regista r = new Regista();
                r.setId(rs.getInt(9));
                r.setNome(rs.getString(10));
                r.setCognome(rs.getString(11));
                f.setRegista(r);
                f.setAttori((ArrayList<Attore>) adao.doRetrieveActorsByFilms(f));
                return f;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
