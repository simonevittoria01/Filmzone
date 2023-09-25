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
import java.util.ArrayList;
import java.util.LinkedHashMap;

@WebServlet(name = "CartServlet", value = "/CartServlet")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OrdineDAO odao = new OrdineDAO();
        HttpSession session = request.getSession();
        String address ="";
        Utente u = (Utente) session.getAttribute("utenteLoggato");

        if (u != null) {
            Ordine o = odao.doRetrieveCarrello(u);  //riprendo, se esiste il carrello precedentemente utilizzato
            if (o == null) {
                o = new Ordine();
                o.setUtente(u.getId());
                odao.doSave(o);
            } else {
                BigliettoDAO bdao = new BigliettoDAO();
                ArrayList<Biglietto> biglietti = bdao.doRetrieveBigliettiByOrdine(o);   //riprendo tutti i biglietti che si trovano in quell'ordine

                    // Inizializza la mappa per tenere traccia del numero di biglietti per ogni film e orario
                    // A ciascun oggetto Film è associata una mappa che contiene gli orari del film e il numero di biglietti legati a ciascun orario
                    LinkedHashMap<Film, LinkedHashMap<String, Integer>> conteggioPerFilmEOrario = new LinkedHashMap<>();
                    ArrayList<Proiezione> proiezioni = new ArrayList<>();

                    for (Biglietto biglietto : biglietti) {
                        //per ogni biglietto recupero il film e l'orario
                        Film film = biglietto.getFilm();
                        String orario = biglietto.getDataOra();

                        //controllo l'orario del biglietto, se è vecchio viene eliminato
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime dataInserita = LocalDateTime.parse(orario, formatter);
                        LocalDateTime currentDateTime = LocalDateTime.now();
                            /*se il biglietto e vecchio viene eliminato*/
                        if (currentDateTime.isAfter(dataInserita)) {
                            bdao.doDelete(biglietto);
                        }else{

                            // Verifica se il film è già presente nella mappa dei film, contains key usa la equals
                            // Se esiste si prende la mappa associata ad esso
                            if (conteggioPerFilmEOrario.containsKey(film)) {
                                LinkedHashMap<String, Integer> conteggioPerOrario = conteggioPerFilmEOrario.get(film);

                                // Verifica se l'orario del biglietto è già presente nella mappa degli orari per il film
                                if (conteggioPerOrario.containsKey(orario)) {
                                    //Se l'orario è già presente, incrementa il conteggio dei biglietti per quell'orario
                                    //dunque recupero il numero di biglietti e lo incremento di 1
                                    int conteggioAttuale = conteggioPerOrario.get(orario);
                                    conteggioPerOrario.put(orario, conteggioAttuale + 1);
                                } else {
                                    //Se l'orario non è presente, aggiungo l'orario alla mappa degli orari per il film con un conteggio di 1
                                    //e aggiungo all'array delle proiezioni la proiezione con quell'orario
                                    conteggioPerOrario.put(orario, 1);
                                    ProiezioneDAO pdao = new ProiezioneDAO();
                                    proiezioni.add(pdao.doRetrievebyBiglietto(biglietto));
                                }
                            } else {
                                // Se il film non è presente, crea una nuova mappa degli orari e aggiungo l'orario del biglietto con un conteggio di 1
                                LinkedHashMap<String, Integer> conteggioPerOrario = new LinkedHashMap<>();
                                conteggioPerOrario.put(orario, 1);
                                conteggioPerFilmEOrario.put(film, conteggioPerOrario);
                                //aggiungo la proiezione all'array
                                ProiezioneDAO pdao = new ProiezioneDAO();
                                proiezioni.add(pdao.doRetrievebyBiglietto(biglietto));
                            }
                        }
                    }

                    session.setAttribute("biglietti", conteggioPerFilmEOrario);
                    session.setAttribute("proiezioni", proiezioni);
                    session.setAttribute("ordine", o);
            }
            address = "/WEB-INF/results/carrello.jsp";

        } else {
            address = "/index.html";
            request.setAttribute("errore", "errore");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
