package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Film;
import model.FilmDAO;
import model.Genere;
import model.GenereDAO;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "HomeServlet", value = "/index.html", loadOnStartup = 1)
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //la HomeServlet è la prima servlet ad essere eseguita
        //recupera dal db tutti i film con almeno una proiezione in corso
        FilmDAO filmDAO = new FilmDAO();
        ArrayList<Film> films = (ArrayList<Film>) filmDAO.filmProiezioni();

        //aggiunge i film alla sessione
        HttpSession session = request.getSession();
        session.setAttribute("films", films);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    @Override
    public void init() throws ServletException {
        super.init();

        //dato che i generi non cambiano, vengono recuperati dal db e memorizzati nel contesto dell'intera applicazione
        GenereDAO genereDAO = new GenereDAO();
        ArrayList<Genere> generi = (ArrayList<Genere>) genereDAO.doRetrieveAll();

        ServletContext servletContext = getServletContext();
        servletContext.setAttribute("generi", generi);
    }
}
