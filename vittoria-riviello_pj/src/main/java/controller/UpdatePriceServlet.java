package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.*;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(name = "UpdatePriceServlet", value = "/UpdatePriceServlet")
public class UpdatePriceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();;
        Utente u = (Utente) session.getAttribute("adminLoggato");
        String address = "";
        if(u!=null){
            if(request.getParameter("id") == null
                    || request.getParameter("prezzo") == null)
            {
                request.setAttribute("errore", "errore");
                address = "/index.html";
            }
            else{
                int id;
                try{
                    id = Integer.parseInt(request.getParameter("id"));
                }catch(NumberFormatException e){
                    id = -1;
                }

                if(id!=-1)
                {
                    FilmDAO filmDAO = new FilmDAO();
                    Film f = filmDAO.doRetrieveById(id);
                    if (f == null)
                        id = -1;
                }

                float prezzo = 1;
                boolean prezzoCheck = true;
                try{
                    prezzo = Float.parseFloat(request.getParameter("prezzo"));
                }catch (NumberFormatException e){
                    prezzoCheck = false;
                }

                if(prezzo < 1){
                    prezzoCheck = false;
                }

                if(id == -1 || !prezzoCheck){
                    address = "/index.html";
                    request.setAttribute("errore", "errore");
                    /*update del prezzo del film*/
                }else {
                    FilmDAO fdao = new FilmDAO();
                    fdao.doUpdatePrice(id, prezzo);
                    BigliettoDAO bdao = new BigliettoDAO();
                    bdao.updatePriceTicket(prezzo, id);
                    address = "/WEB-INF/results/areaAmministratore.jsp";
                    request.setAttribute("prezzoOk", "prezzoOk");
                }
            }
        }else {
            address = "/index.html";
            request.setAttribute("errore", "permessi");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}