package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Film;
import model.FilmDAO;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "SearchFilmServlet", value = "/SearchFilmServlet")
public class SearchFilmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String nomefilm = request.getParameter("nomeFilm");
        String genere = request.getParameter("genere");

        if(nomefilm != null || genere != null) {

            if(nomefilm == null)
                nomefilm = "";
            else if(genere == null)
                genere = "";

            FilmDAO filmDAO = new FilmDAO();

            //ricerca effettuata sia per genere che per nome
            if (!genere.equals("") && !nomefilm.equals("")) {
                Film f = filmDAO.doRetrievebyKindAndByName(genere, nomefilm);
                if (f != null) {
                    request.setAttribute("filmtrovato", f);
                } else {
                    request.setAttribute("errorericerca", "errorericerca");
                }
            }
            //ricerca effettuata solo per genere
            else if (!genere.equals("")) {
                ArrayList<Film> films = (ArrayList<Film>) filmDAO.doRetrievebyKind(genere);
                if (films.size() != 0) {
                    request.setAttribute("filmstrovati", films);
                } else {
                    request.setAttribute("errorericerca", "errorericerca");
                }
            }
            //ricerca effettuata solo per nome
            else if (!nomefilm.equals("")) {
                Film f = filmDAO.doRetrieveByNome(nomefilm);
                if (f != null) {
                    request.setAttribute("filmtrovato", f);
                } else {
                    request.setAttribute("errorericerca", "errorericerca");
                }
            }
        }
        //se sono entrambi null
        else {
            request.setAttribute("errorericerca", "errorericerca");
        }

        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/cercaFilm.jsp");
        requestDispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
