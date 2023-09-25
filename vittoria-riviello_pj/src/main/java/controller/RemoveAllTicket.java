
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
        import java.util.ArrayList;
        import java.util.Map;
        import java.util.LinkedHashMap;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

@WebServlet(name = "RemoveAllTicket", value = "/RemoveAllTicket")
public class RemoveAllTicket extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AggiungereDAO adao = new AggiungereDAO();
        HttpSession session = request.getSession();
        boolean errorerichiesta = false;
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        if(u!=null){
            if(request.getParameter("sala") != null &&
                    request.getParameter("film") != null &&
                    request.getParameter("ordine") != null &&
                    request.getParameter("data") != null &&
                    request.getParameter("ora") != null) {
                int film, ordine, sala;
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

                String data_ora = data + " " + ora;
                try {
                    film = Integer.parseInt(request.getParameter("film"));
                } catch (NumberFormatException e) {
                    film = -1;
                }

                if (film != -1) {
                    FilmDAO filmDAO = new FilmDAO();
                    Film f = filmDAO.doRetrieveById(film);
                    if (f == null)
                        film = -1;
                }


                try {
                    sala = Integer.parseInt(request.getParameter("sala"));
                } catch (NumberFormatException e) {
                    sala = -1;
                }

                if (sala != -1) {
                    SalaDAO salaDAO = new SalaDAO();
                    Sala s = salaDAO.doRetrieveByNum(sala);
                    if (s == null)
                        sala = -1;
                }

                try {
                    ordine = Integer.parseInt(request.getParameter("ordine"));
                } catch (NumberFormatException e) {
                    ordine = -1;
                }

                    /*nel caso non vi siano errori*/
                if (ordine != -1 && sala != -1 && film != -1 && !erroreorario && !erroreData) {
                    OrdineDAO odao = new OrdineDAO();
                    Ordine o = odao.doRetrievebyId(ordine);
                    if (o != null) {
                        ProiezioneDAO proiezioneDAO = new ProiezioneDAO();
                        Proiezione proiezione = proiezioneDAO.doRetrieveProiezione(film, data_ora, sala);
                        /*prendo la proiezione e rimuovo le aggiunte dell'utente sia nel db che nella sessione*/
                        if (proiezione != null) {
                            adao.rimuoviAllAggiunta(proiezione, o); //rimuovo nel db
                            ArrayList<Proiezione> proiezioni = (ArrayList<Proiezione>) session.getAttribute("proiezioni");
                            if (proiezioni != null) {
                                proiezioni.remove(proiezione);
                                session.setAttribute("proiezioni", proiezioni);
                                LinkedHashMap<Film, LinkedHashMap<String, Integer>> biglietti = (LinkedHashMap<Film, LinkedHashMap<String, Integer>>) session.getAttribute("biglietti");
                                if (biglietti != null) {
                                    FilmDAO fdao = new FilmDAO();
                                    Film f = fdao.doRetrieveById(proiezione.getFilm());
                                    if (f != null) {

                                        // Verifica se il film è presente nella mappa esterna
                                        if (biglietti.containsKey(f)) {
                                            // Ottieni la mappa interna per il film specificato
                                            LinkedHashMap<String, Integer> conteggioPerOrario = biglietti.get(f);

                                            // Rimuovi l'orario dalla mappa interna
                                            conteggioPerOrario.remove(proiezione.getDataOra());

                                            // Verifica se non ci sono più orari associati al film
                                            if (conteggioPerOrario.isEmpty()) {
                                                // Rimuovi il film dalla mappa esterna
                                                biglietti.remove(f.getNome());
                                            }
                                        }
                                        session.setAttribute("biglietti", biglietti);
                                    } else
                                        errorerichiesta = true;
                                } else
                                    errorerichiesta = true;
                            } else
                                errorerichiesta = true;
                        } else
                            errorerichiesta = true;
                    } else
                        errorerichiesta = true;
                } else
                    errorerichiesta = true;
            }else
                errorerichiesta = true;
        }else
            errorerichiesta = true;
        //verifico se la richiesta è stata mandata tramite ajax
        String requestedWithHeader = request.getHeader("X-Requested-With");

        //nel caso non è stata mandata attraverso ajax
        if ((requestedWithHeader == null || !requestedWithHeader.equals("XMLHttpRequest")) && !errorerichiesta)
            request.setAttribute("removeAllTicket", "removeAllTicket");

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
