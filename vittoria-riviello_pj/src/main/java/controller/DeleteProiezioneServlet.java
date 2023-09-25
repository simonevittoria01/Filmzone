package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "DeleteProiezioneServlet", value = "/DeleteProiezioneServlet")
public class DeleteProiezioneServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean errorerichiesta = false;

        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("adminLoggato");

        if(u == null) {
            errorerichiesta = true;
        }
        else {

            if (request.getParameter("sala") == null ||
                    request.getParameter("film") == null ||
                    request.getParameter("data") == null ||
                    request.getParameter("ora") == null) {
                errorerichiesta = true;
            }
            else {

                /*controllo della sala*/
                int sala;
                try {
                    sala = Integer.parseInt(request.getParameter("sala"));
                } catch (NumberFormatException e) {
                    sala = -1;
                }
                /*controllo dell effettiva esistenza della sala ricevuta, e se diversa da -1 continua senno e inutile*/
                if(sala!=-1)
                {
                    SalaDAO salaDAO = new SalaDAO();
                    Sala s = salaDAO.doRetrieveByNum(sala);
                    if (s == null)
                        sala = -1;
                }

                /*stessa cosa della sala ma col film*/
                int film;
                try {
                    film = Integer.parseInt(request.getParameter("film"));
                } catch (NumberFormatException e) {
                    film = -1;
                }

                /*se e gia -1 e inutile cercarlo*/
                if(film!=-1)
                {
                    FilmDAO filmDAO = new FilmDAO();
                    Film f = filmDAO.filmProiezioneById(film);
                    if (f == null)
                        film = -1;
                }
                /*controllo data e ora*/

                boolean erroreData = false;
                String data = request.getParameter("data");
                String regData = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";

                Pattern patternData = Pattern.compile(regData);
                Matcher matcherData = patternData.matcher(data);

                if (!matcherData.matches()) {
                    erroreData = true;
                }

                boolean erroreorario = false;
                String ora = request.getParameter("ora");
                String regOra = "^[0-9]{2}:[0-9]{2}$";

                Pattern patternOra = Pattern.compile(regOra);
                Matcher matcherOra = patternOra.matcher(ora);

                if (!matcherOra.matches()) {
                    erroreorario = true;
                }

                if (sala != -1 && film != -1 && !erroreorario && !erroreData) {

                    String data_ora = data + " " + ora;

                    //se i controlli vanno tutti a buon fine, recupero la proiezione con i dati ricevuti tramite ajax
                    ProiezioneDAO pdao = new ProiezioneDAO();
                    Proiezione p = pdao.doRetrieveProiezione(film, data_ora, sala);

                    if(p == null) {
                        errorerichiesta = true;
                    }
                    else {
                        pdao.deleteProiezione(p);
                    }
                }
                else {
                    errorerichiesta = true;
                }
            }
        }

        //verifico se la richiesta è stata mandata tramite ajax
        String requestedWithHeader = request.getHeader("X-Requested-With");

        //nel caso non è stata mandata attraverso ajax
        if ((requestedWithHeader == null || !requestedWithHeader.equals("XMLHttpRequest")) && !errorerichiesta)
            request.setAttribute("aggiuntaCarrello", "aggiuntaCarrello");

        if(errorerichiesta)
            request.setAttribute("errore", "errore");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

}
