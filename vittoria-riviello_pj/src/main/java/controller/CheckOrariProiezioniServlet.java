package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Proiezione;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "CheckOrariProiezioniServlet", value = "/CheckOrariProiezioniServlet")
public class CheckOrariProiezioniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String data = request.getParameter("data");
        boolean erroreData = false;
        String risposta = "";

        if(data == null){
            erroreData = true;
        }

        //se la data non è null, verifico il suo formato
        else {

            String regData = "^[0-9]{4}-[0-9]{2}-[0-9]{2}$";

            Pattern patternData = Pattern.compile(regData);
            Matcher matcherData = patternData.matcher(data);

            if(!matcherData.matches()){
                erroreData = true;
            }

            //se il formato è corretto
            else {

                //verifico se la richiesta è stata mandata tramite ajax
                String requestedWithHeader = request.getHeader("X-Requested-With");

                //nel caso è stata mandata attraverso ajax
                if (requestedWithHeader != null && requestedWithHeader.equals("XMLHttpRequest")) {

                    //recupero dalla sessione le proiezioni, per restituire gli orari disponibili
                    HttpSession session = request.getSession();
                    ArrayList<Proiezione> proiezioni = (ArrayList<Proiezione>) session.getAttribute("proiezioni");

                    if (proiezioni == null) {
                        erroreData = true;
                    }
                    else {

                        ArrayList<String> orari = new ArrayList<>();

                        /*per ogni proiezione, verifico se la data corrisponde alla data nella request
                        se corrisponde, restituisco l'orario nell'array degli orari*/
                        for (Proiezione p : proiezioni)
                            if (p.getDataOra().substring(0, 10).equalsIgnoreCase(data))
                                orari.add(p.getDataOra().substring(11));

                        //compongo la risposta da mandare tramite ajax
                        for (int i = 0; i <= orari.size() - 1; i++)
                            if (i == orari.size() - 1)
                                risposta += orari.get(i);
                            else
                                risposta += orari.get(i) + " ";

                    }
                }
                //se la richiesta non è stata mandata tramite ajax
                else {
                    erroreData = true;
                }
            }
        }

        if(erroreData){
            request.setAttribute("errore", "errore");
            RequestDispatcher requestDispatcher = request.getRequestDispatcher("/index.html");
            requestDispatcher.forward(request,response);
        }
        else {
            response.getWriter().write(risposta);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
