package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "VisualizzaOrdineServlet", value = "/VisualizzaOrdineServlet")
public class VisualizzaOrdineServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String address = "";
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        if(u!=null) {
            //controllo sull'id dell'ordine nella request
            //controllo se presente
            if(request.getParameter("id") == null){
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }
            else{
                //controllo se è un intero
                int ordineid;
                try {
                    ordineid = Integer.parseInt(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    ordineid = -1;
                }
                OrdineDAO odao = new OrdineDAO();
                BigliettoDAO bdao = new BigliettoDAO();
                Ordine o = new Ordine();
                //controllo se l'ordine con quell'id è presente
                if (ordineid != -1) {
                    o = odao.doRetrievebyIdConfirmed(ordineid);
                }
                if (ordineid != -1 && o != null) {
                    /*recupero tutti i biglietti dell'ordine per mostrarli nella jsp*/
                    ArrayList<Biglietto> biglietti = bdao.doRetrieveBigliettiByOrdine(o);
                    session.setAttribute("bigliettiAcquistati", biglietti); // va bene anche se passa null perche poi il controllo lo fa la stampaBiglietti
                    address = "/WEB-INF/results/stampaBiglietti.jsp";
                } else {
                    address = "/index.html";
                    request.setAttribute("errore", "errore");
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

