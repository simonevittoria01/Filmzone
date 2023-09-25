package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.SalaDAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "CheckProiezioniServlet", value = "/CheckProiezioniServlet")
public class CheckProiezioniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /*Controllo della data inizio proiezioni con regex
        Questo errore potrebbe verificarsi solo in caso di chiamate anomale della servlet tramite url senza passare..
        ..attraverso il form con ajax
        per questo non restituisce una risposta con ajax ma effettua un forward alla jsp*/
        boolean erroreData = false;
        boolean erroreAdmin = false;
        String dateString = request.getParameter("data");

        HttpSession session = request.getSession();
        if(session.getAttribute("adminLoggato") == null){
            erroreAdmin = true;
        }
        //se sei loggato come admin
        else {
            if (dateString == null) {
                erroreData = true;
            }
            //se la data non è null:
            else {

                //verifico che si tratti di una data
                String regData = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";

                Pattern patternData = Pattern.compile(regData);
                Matcher matcherData = patternData.matcher(dateString);

                if (!matcherData.matches()) {
                    erroreData = true;
                }
                //se la data rispetta il formato:
                else {

                    //verifico se la richiesta è stata mandata tramite ajax
                    String requestedWithHeader = request.getHeader("X-Requested-With");

                    //nel caso è stata mandata attraverso ajax
                    if (requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest")) {

                        //aggiungo l'ora alla data perché nelle proiezioni c'è la data completa con anche l'ora
                        dateString += " 16:00:00";

                        //ottengo tutte le sale che in quella settimana non hanno proiezioni di altri film
                        SalaDAO salaDAO = new SalaDAO();
                        ArrayList<Integer> sale = salaDAO.doRetrieveByDate(dateString);

                        // Creazione del formatter per il parsing della stringa
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                        //fin quando tutte le sale sono occupate, verifico per le settimane successive
                        while (sale.size() == 0) {
                            // Parsing della stringa in un oggetto LocalDateTime
                            LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);

                            // Aggiunta di 7 giorni all'oggetto LocalDateTime
                            LocalDateTime newDateTime = dateTime.plusDays(7);

                            // Formattazione della nuova data in una stringa
                            dateString = newDateTime.format(formatter);

                            //richiamo la funzione
                            sale = salaDAO.doRetrieveByDate(dateString);
                        }
                    }
                    //se la richiesta non è stata mandata tramite ajax
                    else {
                        erroreData = true;
                    }
                }
            }
        }

        //dopo tutti i controlli
        if(erroreData && !erroreAdmin){
            request.setAttribute("erroreData", "erroreData");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/newFilm.jsp");
            dispatcher.forward(request, response);
        }
        else if(!erroreData && erroreAdmin){
            request.setAttribute("errore", "permessi");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.html");
            dispatcher.forward(request, response);
        }
        //se è andato tutto a buon fine, rispondo con ajax
        else {
            response.getWriter().write(dateString.substring(0, 10));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
