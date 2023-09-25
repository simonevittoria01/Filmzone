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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebServlet(name = "AddTicketServlet", value = "/AddTicketServlet")
public class AddTicketServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        boolean errorerichiesta = false;
        if(u!=null) {
            AggiungereDAO adao = new AggiungereDAO();
            BigliettoDAO bdao = new BigliettoDAO();
            if(request.getParameter("nuovo") != null &&
                    request.getParameter("sala") != null &&
                    request.getParameter("film") != null &&
                    request.getParameter("ordine") != null &&
                    request.getParameter("data") != null &&
                    request.getParameter("ora") != null)
            {
                int nuovo, sala, film, ordine;
                try{
                  nuovo = Integer.parseInt(request.getParameter("nuovo")); //nuovo e la nuova quantita
                }catch(NumberFormatException e){
                    nuovo = -1;
                }

                try{
                    sala = Integer.parseInt(request.getParameter("sala"));
                }catch (NumberFormatException e){
                    sala = -1;
                }

                if(sala!=-1)
                {
                    SalaDAO salaDAO = new SalaDAO();
                    Sala s = salaDAO.doRetrieveByNum(sala);
                    if (s == null)
                        sala = -1;
                }

                try{
                    film = Integer.parseInt(request.getParameter("film"));
                }catch (NumberFormatException e){
                    film = -1;
                }

                if(film!=-1)
                {
                    FilmDAO filmDAO = new FilmDAO();
                    Film f = filmDAO.doRetrieveById(film);
                    if (f == null)
                        film = -1;
                }

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
                String regOra = "^[0-9]{2}:[0-9]{2}:[0-9]{2}$";

                Pattern patternOra = Pattern.compile(regOra);
                Matcher matcherOra = patternOra.matcher(ora);

                if (!matcherOra.matches()) {
                    erroreorario = true;
                }

                try {
                    ordine = Integer.parseInt(request.getParameter("ordine"));
                }catch (NumberFormatException e){
                    ordine = -1;
                }



                if (ordine !=-1 && nuovo!=-1 && sala != -1 && film != -1 && !erroreorario && !erroreData)
                {
                    /*controllo se l'orario e giusto*/
                    String data_ora = data + " " + ora;
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dataInserita = LocalDateTime.parse(data_ora, formatter);
                    LocalDateTime currentDateTime = LocalDateTime.now();

                    if (currentDateTime.isAfter(dataInserita)) {
                        errorerichiesta = true;
                    }else{
                        OrdineDAO odao = new OrdineDAO();
                        Ordine o = odao.doRetrievebyId(ordine);
                        if(o!=null) {
                            ProiezioneDAO proiezioneDAO = new ProiezioneDAO();
                            Proiezione proiezione = proiezioneDAO.doRetrieveProiezione(film, data_ora, sala);
                            if(proiezione!=null){
                                int vecchio = bdao.doRetrivebyProiezioneUtente(proiezione, o); //recupero la vecchia quantita di biglietti
                                    /*capisco se e una remove o aggiunta, se e uguale l'input non e proprio inviato*/
                                if (vecchio < nuovo) {
                                    adao.Aggiunta(o, proiezione, nuovo - vecchio);
                                } else {
                                    if (vecchio > nuovo) {
                                        adao.removeAggiunta(proiezione, o, vecchio - nuovo);
                                    }
                                }
                            }else
                                errorerichiesta = true;
                        }
                        else
                            errorerichiesta = true;
                    }
                }else
                    errorerichiesta = true;
            }else
                errorerichiesta = true;
        }else
            errorerichiesta = true;

        //verifico se la richiesta è stata mandata tramite ajax
        String requestedWithHeader = request.getHeader("X-Requested-With");

        //nel caso non è stata mandata attraverso ajax
        if ((requestedWithHeader == null || !requestedWithHeader.equals("XMLHttpRequest")) && !errorerichiesta)
            request.setAttribute("addTicketServlet", "addTicketServlet");

        else if(errorerichiesta)
            request.setAttribute("errore", "errore");

        RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
        dispatcher.forward(request, response);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
