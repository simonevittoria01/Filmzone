package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "RedirectServlet", value = "/RedirectServlet")
public class RedirectServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = "";
        HttpSession session = request.getSession();
        String destinazione = request.getParameter("destinazione");

        if(destinazione != null){

            //se non l'utente e l'admin non sono loggati viene verificata per area utente
            if( destinazione.equalsIgnoreCase("areaUtente.jsp")
                    && (session.getAttribute("utenteLoggato") == null)
                    && (session.getAttribute("adminLoggato") == null) ){
                address = "/login.jsp";
                request.setAttribute("noLogin", "noLogin");
            }

            //se non l'utente e l'admin non sono loggati viene verificata per area utente, redirect
            else if( destinazione.equalsIgnoreCase("areaUtente.jsp")
                    && (session.getAttribute("utenteLoggato") == null)
                    && (session.getAttribute("adminLoggato") != null) ){
                address = "/WEB-INF/results/areaAmministratore.jsp";
            }

            //se l'utente e l'admin non sono loggati viene verificata
            else if(destinazione.equalsIgnoreCase("pagamento.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") == null)){
                address = "/login.jsp";
                request.setAttribute("noLogin", "noLogin");
            }

            else if(destinazione.equalsIgnoreCase("pagamento.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") != null)){
                address = "/WEB-INF/results/pagamento.jsp";
            }

            else if(destinazione.equalsIgnoreCase("pagamento.jsp")
                    && (session.getAttribute("adminLoggato") != null)
                    && (session.getAttribute("utenteLoggato") == null)){
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }

            else if(destinazione.equalsIgnoreCase("areaAmministratore.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") == null)){
                address = "/login.jsp";
                request.setAttribute("noLogin", "noLogin");
            }

            else if(destinazione.equalsIgnoreCase("areaAmministratore.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") != null)){
                address = "/WEB-INF/results/areaUtente.jsp";
            }

            else if(destinazione.equalsIgnoreCase("newFilm.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") == null)){
                address = "/login.jsp";
                request.setAttribute("noLogin", "noLogin");
            }

            else if(destinazione.equalsIgnoreCase("newFilm.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") != null)){
                address = "/WEB-INF/results/areaUtente.jsp";
            }

            else if(destinazione.equalsIgnoreCase("registrazione.jsp")
                    && (session.getAttribute("adminLoggato") == null)
                    && (session.getAttribute("utenteLoggato") != null)){
                address = "/WEB-INF/results/areaUtente.jsp";
            }

            else if(destinazione.equalsIgnoreCase("registrazione.jsp")
                    && (session.getAttribute("adminLoggato") != null)
                    && (session.getAttribute("utenteLoggato") == null)){
                address = "/WEB-INF/results/areaAmministratore.jsp";
            }

            else if(!destinazione.equalsIgnoreCase("areaUtente.jsp") && !destinazione.equalsIgnoreCase("areaAmministratore.jsp") && !destinazione.equalsIgnoreCase("registrazione.jsp") && !destinazione.equalsIgnoreCase("newFilm.jsp") && !destinazione.equalsIgnoreCase("pagamento.jsp")) {
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }
            else {
                address = "/WEB-INF/results/" + destinazione;
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
