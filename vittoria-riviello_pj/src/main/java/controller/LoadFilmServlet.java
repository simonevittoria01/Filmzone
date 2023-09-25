package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "LoadFilmServlet", value = "/LoadFilmServlet")
public class LoadFilmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = "";
        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("adminLoggato");
        if(u==null) {
            address = "/index.html";
            request.setAttribute("errore", "permessi");
        }
        else if(request.getParameter("id")!=null && request.getParameter("id2")==null) {
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
                FilmDAO fdao = new FilmDAO();
                Film film = fdao.filmProiezioneById(fid);

                //se non esiste nel db -> errore
                if(film == null){
                    address = "/index.html";
                    request.setAttribute("errore", "errore");
                }

                //se esiste lo inserisco nella sessione insieme ad altre informazioni e lo mostro
                else {
                    session.setAttribute("filmModify", film);
                    address = "/WEB-INF/results/modificaPrezzo.jsp";
                }
            }
        }
        else if(request.getParameter("id2")!=null && request.getParameter("id")==null) {
            int fid2;
            try {
                fid2 = Integer.parseInt(request.getParameter("id2"));
            }
            catch (NumberFormatException e){
                fid2 = -1;
            }

            if(fid2 == -1){
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }
            //se il parametro è un intero, controllo se il film con quell'id esiste
            else {
                FilmDAO fdao = new FilmDAO();
                Film film = fdao.filmProiezioneById(fid2);

                //se non esiste nel db -> errore
                if(film == null){
                    address = "/index.html";
                    request.setAttribute("errore", "errore");
                }
                //se esiste lo inserisco nella sessione insieme ad altre informazioni e lo mostro
                else {
                    session.setAttribute("filmDelete", film);
                    address = "/WEB-INF/results/deleteProiezione.jsp";

                    ProiezioneDAO proiezioneDAO = new ProiezioneDAO();
                    ArrayList<Proiezione> proiezioni = proiezioneDAO.dataProiezioneFilm(film.getId());
                    session.setAttribute("proiezioni", proiezioni);
                }
            }
        }
        else {
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
