package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Utente;
import model.UtenteDAO;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "CheckLoginServlet", value = "/CheckLoginServlet")
public class CheckLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String address = "";

        //verifico se l'admin o un utente sono già loggati
        if(session.getAttribute("adminLoggato")!=null || session.getAttribute("utenteLoggato")!=null) {
            request.setAttribute("errore", "errore");
            address = "/index.html";
        }
        else {
            //verifico l'email e la password
            if (request.getParameter("email") == null || request.getParameter("password") == null) {
                request.setAttribute("errorLogin", "formato");
                address = "/login.jsp";
            }

            else {
                String email = request.getParameter("email");
                String password = request.getParameter("password");

                //controllo input server side

        /*controllo email:
        1 parte fino al + [a-z0-9!#$%&'*+/=?^_{|}~\-]:
        Questa parte corrisponde a un singolo carattere che può essere una lettera minuscola (a-z), un numero (0-9) o uno dei seguenti simboli: ! # $ % & ' * + / = ? ^ _ { | } ~ -.
        + indica che l'elemento precedente puo ripetersi piu volte.
        2 parte fino alla @:
        (?:.[a-z0-9!#$%&'+/=?^_`{|}~-]+) stesso controllo della prima parte solo con l aggiunta de ?:. che significa che possono essere
        nel caso anche preceduti da dei punti i caratteri.
        L'intera sequenza può ripetersi zero o più volte, indicata da "*".
        3 parte fino al +
        (?:a-z0-9?.)+: Questa parte descrive il dominio dell'indirizzo email. Inizia con un carattere alfanumerico (a-z0-9),
        seguito da zero o più caratteri alfanumerici o trattini ("-") e termina con un carattere alfanumerico.
        Questa sequenza di caratteri può ripetersi una o più volte, separata da punti.
        a-z0-9?: Questa parte descrive l'ultima parte del dominio (ad esempio, ".com", ".org", ecc.
                 */
                String regEmail = "[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~\\-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

                /*espressione per la password:
                . : qualisasi carattere eccetto il nuova riga
                {6,} indica che il carattere precedente(il punto) puo apparire minimo 6 ma anche piu volte
                 */
                String regPass = "^.{6,}$";

                Pattern patternEmail = Pattern.compile(regEmail);
                Matcher matcherEmail = patternEmail.matcher(email);

                Pattern patternPass = Pattern.compile(regPass);
                Matcher matcherPass = patternPass.matcher(password);

                if (!matcherEmail.matches() || !matcherPass.matches()) {
                    request.setAttribute("errorLogin", "formato");
                    address = "/login.jsp";
                }

                else {
                    //verifico se le credenziali sono quelle dell'admin gia preimpostate anche nel db
                    boolean admin = false;

                    if (email.equalsIgnoreCase("filmzone@gmail.com") && password.equalsIgnoreCase("123456")) {
                        admin = true;
                    }

                    UtenteDAO utenteDAO = new UtenteDAO();
                    Utente u = utenteDAO.doRetrieveByLogin(email, password);

                    if (u != null && admin) {
                        session.setAttribute("adminLoggato", u);
                        address = "/WEB-INF/results/areaAmministratore.jsp";
                    }

                    if (u != null && !admin) {
                        session.setAttribute("utenteLoggato", u);
                        address = "/WEB-INF/results/areaUtente.jsp";
                    }

                    if (u == null) {
                        request.setAttribute("errorLogin", "errorLogin");
                        address = "/login.jsp";
                    }
                }
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}
