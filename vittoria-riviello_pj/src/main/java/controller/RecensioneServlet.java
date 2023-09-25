package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Film;
import model.Recensione;
import model.RecensioneDAO;
import model.Utente;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "RecensioneServlet", value = "/RecensioneServlet")
public class RecensioneServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        Utente admin = (Utente) session.getAttribute("adminLoggato");
        Film f = (Film) session.getAttribute("film");

        String address = "/WEB-INF/results/showFilm.jsp";

        //se sei loggato come admin o non sono presenti film nella sessione -> errore
        if(admin != null || f == null){
            request.setAttribute("errore", "errore");
            address = "/index.html";
        }

        else {

            //se non sei loggato come utente non puoi lasciare una recensione
            if (u == null) {
                address = "/login.jsp";
                request.setAttribute("noLogin", "noLogin");
            }

            //controllo parametri
            else {

                if (request.getParameter("idfilm") == null || request.getParameter("idutente") == null
                        || request.getParameter("voto") == null || request.getParameter("recensione") == null) {
                    request.setAttribute("errorRecensione", "errorRecensione");
                }

                else {
                    int filmid;
                    int utenteid;

                    //controllo il formato
                    try {
                        filmid = Integer.parseInt(request.getParameter("idfilm"));
                    } catch (NumberFormatException e) {
                        filmid = -1;
                    }

                    try {
                        utenteid = Integer.parseInt(request.getParameter("idutente"));
                    } catch (NumberFormatException e) {
                        utenteid = -1;
                    }

                    //controllo se il film e l'utente corrispondono a quelli della sessione

                    if (f.getId() != filmid)
                        filmid = -1;

                    if (u.getId() != utenteid)
                        utenteid = -1;

                    boolean erroreRecensione = false;
                    String recensione = request.getParameter("recensione");
                    if(recensione.length()>255){
                        erroreRecensione = true;
                    }

                    if (filmid == -1 || utenteid == -1 || erroreRecensione) {
                        request.setAttribute("errorRecensione", "errorRecensione");
                    }

                    else {

                        RecensioneDAO recensioneDAO = new RecensioneDAO();

                        //controllo se la recensione già esiste, un utente può lasciare al massimo una recensione per un film
                        if (recensioneDAO.checkRecensioneEsistente(utenteid, filmid))
                            request.setAttribute("RecensioneEsistente", "RecensioneEsistente");

                        else {
                            float voto = 0;
                            boolean votoCheck = true;
                            try {
                                voto = Float.parseFloat(request.getParameter("voto"));
                            } catch (NumberFormatException e) {
                                votoCheck = false;
                            }

                            if (voto <= 0 || voto > 5)
                                votoCheck = false;


                            if (!votoCheck) {
                                request.setAttribute("errorRecensione", "errorRecensione");
                            }

                            //aggiungo la recensione
                            else {



                                Recensione r = new Recensione();
                                r.setFilm(filmid);
                                r.setUtente(utenteid);
                                r.setVoto(voto);
                                r.setCommento(recensione);

                                recensioneDAO.doSave(r);

                                ArrayList<Recensione> recensioni = recensioneDAO.doRetrieveRecensioniByFilm(filmid);
                                session.setAttribute("recensioni", recensioni);

                                request.setAttribute("Recensione", "Recensione");
                            }
                        }
                    }
                }
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
