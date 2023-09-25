package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;


import java.io.IOException;
import java.util.ArrayList;


@WebServlet(name = "GestioneFilmServlet", value = "/GestioneFilmServlet")
public class GestioneFilmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("adminLoggato");
        String address = "";
        if(u!=null) {
            FilmDAO filmDAO = new FilmDAO();
            ArrayList<Film> films = (ArrayList<Film>) filmDAO.filmProiezioni();

            if(films != null){
                session.setAttribute("films", films);
                address = "/WEB-INF/results/stampaFilm.jsp";
            }else {
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }
        }else{
            address = "/index.html";
            request.setAttribute("errore", "errore");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
