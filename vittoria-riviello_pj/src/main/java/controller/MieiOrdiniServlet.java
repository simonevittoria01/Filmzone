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


@WebServlet(name = "MieiOrdiniServlet", value = "/MieiOrdiniServlet")
public class MieiOrdiniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        String address = "";
        if(u!=null) {
            //recupero tutti gli ordini confermati dall'utente
            OrdineDAO odao = new OrdineDAO();
            ArrayList<Ordine> ordiniConfermati = odao.doRetrieveOrdiniConfermatiByUtente(u);
            if(ordiniConfermati != null){
                session.setAttribute("ordiniConfermati", ordiniConfermati);
                address = "/WEB-INF/results/stampaOrdini.jsp";
            }else {
                address = "/index.html";
                request.setAttribute("errore", "errore");
            }
        }else{
            address = "/login.jsp";
            request.setAttribute("noLogin", "noLogin");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
