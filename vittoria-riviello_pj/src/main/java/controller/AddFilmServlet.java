package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@WebServlet(name = "AddFilmServlet", value = "/AddFilmServlet")
@MultipartConfig
public class AddFilmServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = "";

        HttpSession session = request.getSession();
        Utente admin = (Utente) session.getAttribute("adminLoggato");
        if(admin == null) {
            request.setAttribute("errore", "permessi");
            address = "/index.html";
        }

        //se sei loggato come admin:
        else {

            if(request.getParameter("nome") == null
                    || request.getParameter("durata") == null
                    || request.getParameter("prezzo") == null
                    || request.getParameter("datainizio") == null
                    || request.getParameter("descrizione") == null
                    || request.getParameter("genere") == null
                    || request.getParameter("regista") == null
                    || request.getParameter("attori") == null)
            {
                request.setAttribute("errorNewFilm", "errorNewFilm");
                address = "/WEB-INF/results/newFilm.jsp";
            }

            //se tutti i parametri sono diversi da null:
            else {

                //estrazione nome
                String nome = request.getParameter("nome");

                boolean nomeerror = true;
                if (nome.trim().matches("^\\s+$") || nome.length()>255)
                    nomeerror = false;

                //verifico prima di tutto se il film già esiste tramite il nome
                FilmDAO filmDAO = new FilmDAO();
                Film f = filmDAO.doRetrieveByNome(nome);

                //se il film già esiste
                if (f != null) {
                    request.setAttribute("filmEsistente", "filmEsistente");
                    address = "/WEB-INF/results/newFilm.jsp";
                }

                //se il film non esiste
                else {

                    //controllo input number (durata film)
                    int durata = 1;
                    boolean numberCheck = true;
                    try {
                        durata = Integer.parseInt(request.getParameter("durata"));
                    } catch (NumberFormatException e) {
                        numberCheck = false;
                    }

                    if (durata < 1)
                        numberCheck = false;

                    //controllo input prezzo
                    float prezzo = 1;
                    boolean prezzoCheck = true;
                    try {
                        prezzo = Float.parseFloat(request.getParameter("prezzo"));
                    } catch (NumberFormatException e) {
                        prezzoCheck = false;
                    }

                    if (prezzo < 1)
                        prezzoCheck = false;

                    //controllo data
                    LocalDate data = null;
                    boolean dataCheck = true;
                    try {
                        data = LocalDate.parse(request.getParameter("datainizio"));
                    } catch (DateTimeParseException e) {
                        dataCheck = false;
                    }

                    boolean erroreDescrizione = true;
                    String descrizione = request.getParameter("descrizione");
                    if(descrizione.length() > 255){
                        erroreDescrizione = false;
                    }

                    //controllo che il genere è presente
                    String genere = request.getParameter("genere");
                    boolean genereCheck = false;

                    ServletContext servletContext = getServletContext();
                    ArrayList<Genere> generi = (ArrayList<Genere>) servletContext.getAttribute("generi");

                    for(Genere g : generi)
                        if(g.getNome().equalsIgnoreCase(genere)) {
                            genereCheck = true;
                            break;
                        }

                    //controllo il formato di regista e attori
                    String regista = request.getParameter("regista");
                    String attNomiCognomi = request.getParameter("attori");

                    String regRegista = "^[A-Za-z]+\\s[A-Za-z]+$";
                    String regAttori = "^[A-Za-z]+\\s[A-Za-z]+(,[A-Za-z]+\\s[A-Za-z]+)*$";

                    Pattern patternRegista = Pattern.compile(regRegista);
                    Matcher matcherRegista = patternRegista.matcher(regista);

                    Pattern patternAttori = Pattern.compile(regAttori);
                    Matcher matcherAttori = patternAttori.matcher(attNomiCognomi);

                    //se un controllo è fallito -> errore
                    if (!matcherAttori.matches() || !matcherRegista.matches() || !numberCheck || !dataCheck || !prezzoCheck || !genereCheck || !erroreDescrizione || !nomeerror) {
                        request.setAttribute("errorNewFilm", "errorNewFilm");
                        address = "/WEB-INF/results/newFilm.jsp";
                    }
                    //se i controlli fatti fin ora sono andati a buon fine
                    else {

                        //gestione immagine copertina
                        Part immagine = request.getPart("copertina");
                        InputStream fileInputStream = immagine.getInputStream();
                        byte[] imageBytes = IOUtils.toByteArray(fileInputStream);

                        //gestione regista
                        String[] splitRegista = regista.split(" ");
                        //controllo che il regista esiste
                        RegistaDAO registaDAO = new RegistaDAO();
                        Regista registaOb = registaDAO.doRetrieveByNomeCognome(splitRegista[0], splitRegista[1]);

                        //se non esiste lo creo
                        if (registaOb == null) {
                            registaOb = new Regista();
                            registaOb.setNome(splitRegista[0]);
                            registaOb.setCognome(splitRegista[1]);
                            registaDAO.doSave(registaOb);
                        }

                        //setto i parametri dell'oggetto film
                        f = new Film();
                        f.setCopertina(imageBytes);
                        f.setNome(nome);
                        f.setDurata(durata);
                        f.setDataUscita(data);
                        f.setDescrizione(descrizione);
                        f.setGenere(genere);
                        f.setRegista(registaOb);
                        f.setPrezzo(prezzo);
                        filmDAO.doSave(f);


                        /*gestione attori:
                        attNomiCognomi = "nome1 cognome1,nome2 cognome2,nome3 cognome3"*/

                        String[] nomicognomi = attNomiCognomi.split(",");
                        //nomicognomi = {"nome1 cognome1" , "nome2 cognome2" , "nome3 cognome3"}

                        AttoreDAO attoreDAO = new AttoreDAO();
                        ArrayList<Attore> attori = new ArrayList<>();

                        //verifico se gli attori esistono già
                        for (String s : nomicognomi) {
                            Attore attoreOb = null;

                            String[] attore = s.split(" ");

                            attoreOb = attoreDAO.doRetrieveByNomeCognome(attore[0], attore[1]);

                            //se non esiste lo creo
                            if (attoreOb == null) {
                                attoreOb = new Attore();
                                attoreOb.setNome(attore[0]);
                                attoreOb.setCognome(attore[1]);
                                attoreDAO.doSave(attoreOb);
                            }

                            //lo aggiungo al cast del film nel db
                            attoreDAO.addCast(f.getId(), attoreOb.getId());

                            //lo aggiungo all'array di attori dell'oggetto film
                            attori.add(attoreOb);
                        }

                        f.setAttori(attori);


                        //aggiunta proiezioni per tutta la settimana
                        SalaDAO salaDAO = new SalaDAO();
                        String dataString2 = String.valueOf(data);

                        //ottengo tutte le sale che in quella settimana non hanno proiezioni di altri film
                        ArrayList<Integer> sale = salaDAO.doRetrieveByDate(dataString2 + " 16:00:00");

                        //dato che il controllo già l'ho effettuato, se non ci sono sale disponibili ci potrebbe essere un errore nella data
                        if (sale.size() == 0) {
                            request.setAttribute("erroreData", "erroreData");
                            address = "/WEB-INF/results/newFilm.jsp";
                        }
                        //se ci sono sale disponibili, prendo la prima disponibile
                        else {

                            Sala s = salaDAO.doRetrieveByNum(sale.get(0));

                            ProiezioneDAO proiezioneDAO = new ProiezioneDAO();

                            ArrayList<Proiezione> proiezioni = new ArrayList<>();

                            //aggiungo le proiezioni per tutta la settimana partendo dal lunedi fino alla domenica
                            for (int i = 0; i < 7; i++) {

                                LocalDate newDate = data.plusDays(i);

                                String dataString = String.valueOf(newDate);

                                Proiezione p1 = new Proiezione();
                                p1.setFilm(f.getId());
                                p1.setDataOra(dataString + " 16:00:00");
                                p1.setNumSala(s.getNumSala());
                                p1.setPostiDisponibili(s.getNumPosti());
                                proiezioneDAO.doSave(p1);

                                Proiezione p2 = new Proiezione();
                                p2.setFilm(f.getId());
                                p2.setDataOra(dataString + " 19:30:00");
                                p2.setNumSala(s.getNumSala());
                                p2.setPostiDisponibili(s.getNumPosti());
                                proiezioneDAO.doSave(p2);

                                Proiezione p3 = new Proiezione();
                                p3.setFilm(f.getId());
                                p3.setDataOra(dataString + " 23:00:00");
                                p3.setNumSala(s.getNumSala());
                                p3.setPostiDisponibili(s.getNumPosti());
                                proiezioneDAO.doSave(p3);

                                proiezioni.add(p1);
                                proiezioni.add(p2);
                                proiezioni.add(p3);
                            }

                            request.setAttribute("filmSettato", f);
                            address = "/index.html";
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
        doGet(request, response);
    }
}