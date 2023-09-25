package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "ConfirmPaymentServlet", value = "/ConfirmPaymentServlet")
public class ConfirmPaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        LinkedHashMap<Film, LinkedHashMap<String, Integer>> conteggio = (LinkedHashMap<Film, LinkedHashMap<String, Integer>>) session.getAttribute("biglietti");

        String address = "";

        //controlli sui parametri
        if(u!=null && conteggio!=null && !conteggio.isEmpty()){

            if(request.getParameter("card-number") == null
                    || request.getParameter("cvv") == null
                    || request.getParameter("card-holder") == null
                    || request.getParameter("expiration-date") == null)
            {
                request.setAttribute("errore", "errore");
                address = "/index.html";
            }else {
                /*controllo se sono cambiati dei prezzi*/
                boolean prezzo = false;
                for (Film film : conteggio.keySet()) {
                    FilmDAO fdao = new FilmDAO();
                    Film f = fdao.doRetrieveById(film.getId());
                    if(f.getPrezzo()!= film.getPrezzo()){
                        prezzo = true;
                    }
                }
                //se nessun prezzo è cambiato
                if(!prezzo){
                    /*numero di carta*/
                    String cardNumber = request.getParameter("card-number");
                    String regCard = "^[0-9]{16}$";
                    Pattern patternCard = Pattern.compile(regCard);
                    Matcher matcherCard = patternCard.matcher(cardNumber);

                    /*titolare della carta*/
                    String cardHolder = request.getParameter("card-holder");
                    String regHolder = "^[A-Za-z]+\\s[A-Za-z]+$";
                    Pattern patternHolder = Pattern.compile(regHolder);
                    Matcher matcherHolder = patternHolder.matcher(cardHolder);

                    /*mese e anno scadenza*/
                    boolean carta = false;
                    String expiryDate = request.getParameter("expiration-date");
                    String regDate = "^(0[1-9]|1[0-2])/([0-9]{2})$";
                    Pattern patternDate = Pattern.compile(regDate);
                    Matcher matcherDate = patternDate.matcher(expiryDate);
                    if (matcherDate.matches()) {
                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
                        /*controllo se c e errore nel parsing*/
                        try {
                            LocalDate expiry = YearMonth.parse(expiryDate, formatter).atEndOfMonth();
                            if (!(expiry.isAfter(today) || expiry.isEqual(today))) {
                                carta = true;
                            }
                        } catch (DateTimeParseException e) {
                            carta = true;
                        }
                    } else {
                        carta = true;
                    }

                    /*cvv*/
                    String cvv = request.getParameter("cvv");
                    String regCvv = "^[0-9]{3}$";
                    Pattern patternCvv = Pattern.compile(regCvv);
                    Matcher matcherCvv = patternCvv.matcher(cvv);

                    //recupero l'ordine dalla sessione
                    Ordine o = (Ordine) session.getAttribute("ordine");
                    OrdineDAO odao = new OrdineDAO();
                    //verifico che i controlli precedenti siano andati a buon fine e l'ordine esiste
                    if (!carta && matcherCvv.matches() && matcherCard.matches() && matcherHolder.matches() && o != null) {
                        //controllo che l'ordine recuperato non è stato gia confermato
                        //la doRetrievebyId restituisce un ordine solo se la data di acquisto è null, quindi che non è stato confermato
                        o = odao.doRetrievebyId(o.getId());
                        if(o!=null) {
                            //recupero tutti i biglietti dell'ordine presenti nel db, ordinati per film e poi proiezione
                            BigliettoDAO bdao = new BigliettoDAO();
                            ArrayList<Biglietto> biglietti = bdao.doRetrieveBigliettiByOrdine(o);
                            /*controllo se esistono*/
                            if (biglietti != null && !biglietti.isEmpty()) {
                                /*recupero la proiezione del primo biglietto*/
                                ProiezioneDAO pdao = new ProiezioneDAO();
                                Proiezione prec = pdao.doRetrieveProiezione(biglietti.get(0).getFilm().getId(), biglietti.get(0).getDataOra(), biglietti.get(0).getNumSala());
                                if (prec != null) {
                                /*se la proiezione del biglietto esiste, controllo che l'orario e il giorno della proiezione
                                non siano antecedenti alla data di oggi, per non permettere l'acquisto di un
                                biglietto di una proiezione vecchia*/
                                    boolean erroreData = false;
                                    String data_ora = prec.getDataOra();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                    LocalDateTime dataInserita = LocalDateTime.parse(data_ora, formatter);
                                    LocalDateTime currentDateTime = LocalDateTime.now();
                                    //controllo quindi l'errore
                                    if (currentDateTime.isAfter(dataInserita)) {
                                        erroreData = true;
                                    }
                                    if(!erroreData) {
                                        /*il controllo sulla data viene fatto per ogni biglietto
                                        che ha una proiezione diversa dalla precedente*/
                                        for(Biglietto b: biglietti){
                                            Proiezione p = new Proiezione();
                                            p.setNumSala(b.getNumSala());
                                            p.setDataOra(b.getDataOra());
                                            p.setFilm(b.getFilm().getId());
                                            //se la proiezione è diversa controllo la data
                                            if (!p.equals(prec)) {
                                                 data_ora = prec.getDataOra();
                                                 formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                                 dataInserita = LocalDateTime.parse(data_ora, formatter);
                                                 currentDateTime = LocalDateTime.now();
                                                //controllo quindi l'errore
                                                if (currentDateTime.isAfter(dataInserita)) {
                                                    erroreData = true;
                                                    break;
                                                }else{
                                                    prec = pdao.doRetrieveProiezione(b.getFilm().getId(), b.getDataOra(), b.getNumSala());
                                                }
                                            }
                                        }

                                        //se non si è mai verificato un errore sulla data
                                        if(!erroreData){
                                            /*Per ogni biglietto dell'ordine, controllo che la fila non sia 'k' in poi (biglietto non valido)
                                            dato che le sale arrivano massimo al posto: 'fila J numero 10'*/
                                            boolean cambi = false;
                                            for(Biglietto b: biglietti){
                                                char carattere = b.getFila().charAt(0);
                                                if(carattere>='K'){
                                                    /* se si trova o supera la fila 'k', cerco nel db un biglietto valido
                                                    che non sia stato ancora acquistato, ma si trova in qualche carrello != da questo,
                                                    ed effettuo il cambio posto e fila*/
                                                    bdao.changeTicket(b, o.getId());  //serve per non dare biglietti oltre il limite
                                                    cambi = true;
                                                }
                                            }
                                            //recupero i biglietti dell utente nel db dopo i cambi
                                            if(cambi){
                                                biglietti = bdao.doRetrieveBigliettiByOrdine(o);
                                            }

                                                /*dopo tutti i controlli, ulteriore ciclo for sui biglietti dell'ordine
                                                se il biglietto ha la stessa proiezione del precedente aumento la quantità dei biglietti, essendo ordinati posso
                                                se la proiezione cambia, quindi sono finiti i biglietti della precedente proiezione nell'array,
                                                vado a fare l'update definitivo nell'ordine*/
                                                boolean acquistoEffettuato = false;
                                                int quantita = 0;
                                                //recupero la proiezione del primo biglietto
                                                prec = pdao.doRetrieveProiezione(biglietti.get(0).getFilm().getId(), biglietti.get(0).getDataOra(), biglietti.get(0).getNumSala());

                                                for (Biglietto b : biglietti) {
                                                    Proiezione p = new Proiezione();
                                                    p.setNumSala(b.getNumSala());
                                                    p.setDataOra(b.getDataOra());
                                                    p.setFilm(b.getFilm().getId());
                                                    /*se la proiezione dell'attuale biglietto è uguale alla proiezione prec,
                                                    aumento la quantità dei biglietti, altrimenti aggiorno il db con la conferma dei posti*/
                                                    if (p.equals(prec)) {
                                                        quantita++;
                                                    } else {
                                                        //un ultimo controllo prima di procedere con l'aggiornamento
                                                        prec = pdao.doRetrieveProiezione(prec.getFilm(), prec.getDataOra(), prec.getNumSala());
                                                        /*nel caso di cambiamento all'ultimo i biglietti non saranno acquistati, neanche i rimanenti*/
                                                        if (prec.getPostiDisponibili() >= quantita && prec.getPostiDisponibili() > 0) {
                                                            pdao.updatePosti(prec, quantita, o);
                                                            acquistoEffettuato = true;
                                                        }
                                                        //ora in prec ci va la proiezione dell'attuale biglietto
                                                        prec = pdao.doRetrieveProiezione(b.getFilm().getId(), b.getDataOra(), b.getNumSala());
                                                        quantita = 1;
                                                    }
                                                }
                                                //update dell ultima proiezione
                                                if (prec.getPostiDisponibili() >= quantita && prec.getPostiDisponibili() > 0) {
                                                    pdao.updatePosti(prec, quantita, o);
                                                    acquistoEffettuato = true;
                                                }
                                                /*se c'è stato almeno un acquisto certifico l'ordine*/
                                                if (acquistoEffettuato) {
                                                    //doUpdate(o), modifica la data di acquisto dell'ordine da NULL alla data attuale
                                                    //dunque da carrello diventa ordine
                                                    odao.doUpdate(o);
                                                    //aggiungo gia un nuovo cerrello per l'utente siccome il vecchio ordine è stato confermato
                                                    Ordine newOrdine = new Ordine();
                                                    newOrdine.setUtente(u.getId());
                                                    odao.doSave(newOrdine);
                                                    ArrayList<Biglietto> bigliettiConfirmed = bdao.doRetrieveBigliettiByOrdine(o);  //presa dei biglietti confermati
                                                    session.setAttribute("bigliettiAcquistati", bigliettiConfirmed); // va bene anche se passa null perche poi il controllo lo fa la stampaBiglietti
                                                    session.removeAttribute("biglietti");
                                                    session.removeAttribute("proiezioni");
                                                    address = "/WEB-INF/results/stampaBiglietti.jsp";
                                                } else {
                                                    address = "/index.html";
                                                    request.setAttribute("erroreAcquistoBigl", "erroreAcquistoBigl");
                                                }
                                        }else{
                                            address = "/index.html";
                                            request.setAttribute("errore", "errore");
                                        }
                                    }else{
                                        address = "/index.html";
                                        request.setAttribute("errore", "errore");
                                    }
                                } else {
                                    address = "/index.html";
                                    request.setAttribute("errore", "errore");
                                }
                            } else {
                                address = "/index.html";
                                request.setAttribute("errore", "errore");
                            }
                        }else {
                            address = "/index.html";
                            request.setAttribute("errore", "errore");
                        }
                    }else {
                        address = "/index.html";
                        request.setAttribute("errore", "errore");
                    }
                }else{
                    address = "/index.html";
                    request.setAttribute("cambioPrezzo", "cambioPrezzo");
                }
            }
        }else {
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
