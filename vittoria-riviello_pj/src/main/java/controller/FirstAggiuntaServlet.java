package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "FirstAggiuntaServlet", value = "/FirstAggiuntaServlet")
public class FirstAggiuntaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean errorerichiesta = false;

        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("utenteLoggato");

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
                    Film f = filmDAO.doRetrieveById(film);
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

                    if (p == null) {
                        errorerichiesta = true;
                    } else {

                        //se la proiezione esiste e se...
                        //...la richiesta non è stata inviata tramite ajax, controllo che la data inserita segui quella di oggi
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime dataInserita = LocalDateTime.parse(data_ora, formatter);
                        LocalDateTime currentDateTime = LocalDateTime.now();

                        if (currentDateTime.isAfter(dataInserita)) {
                            errorerichiesta = true;
                        } else {
                            //recupero il carrello legato all'utente
                            OrdineDAO odao = new OrdineDAO();
                            Ordine o = odao.doRetrieveCarrello(u);
                            /*se non esiste, lo creo*/
                            if (o == null) {
                                o = new Ordine();
                                o.setUtente(u.getId());
                                odao.doSave(o);
                            }
                            //recupero, se esistono, il numero di biglietti nel carrello legati alla proiezione
                            BigliettoDAO bdao = new BigliettoDAO();
                            int bigliettiAttuali = bdao.doRetrivebyProiezioneUtente(p, o);
                            /*creo e aggiungo un biglietto al carrello solo se ci sono posti disponibili e i biglietti sono meno di 5*/
                            if (bigliettiAttuali < 5 && p.getPostiDisponibili() > bigliettiAttuali) {
                                AggiungereDAO adao = new AggiungereDAO();
                                adao.firstAggiunta(o, p);
                            }
                        }
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
