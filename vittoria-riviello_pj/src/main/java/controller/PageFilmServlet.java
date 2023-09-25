package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "PageFilmServlet", value = "/PageFilmServlet")
public class PageFilmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = "";

        int fid;
        try {
            fid = Integer.parseInt(request.getParameter("id"));
        }
        catch (NumberFormatException e){
            fid = -1;
        }

        if(fid == -1){
            address = "/index.html";
            request.setAttribute("errore", "errore");
        }
        //se il parametro è un intero, controllo se il film con quell'id esiste
        else {
            HttpSession session = request.getSession();
            FilmDAO fdao = new FilmDAO();
            Film film = fdao.doRetrieveById(fid);

            //se non esiste nel db -> errore
            if(film == null){
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }

            //se esiste lo inserisco nella sessione insieme ad altre informazioni e lo mostro
            else {
                session.setAttribute("film", film);

                address = "/WEB-INF/results/showFilm.jsp";

                ProiezioneDAO proiezioneDAO = new ProiezioneDAO();
                ArrayList<Proiezione> proiezioni = proiezioneDAO.dataProiezioneFilm(film.getId());
                session.setAttribute("proiezioni", proiezioni);

                RecensioneDAO recensioneDAO = new RecensioneDAO();
                ArrayList<Recensione> recensioni = recensioneDAO.doRetrieveRecensioniByFilm(film.getId());
                session.setAttribute("recensioni", recensioni);
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
