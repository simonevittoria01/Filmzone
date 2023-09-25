package controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.Utente;
import model.UtenteDAO;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "RegServlet", value = "/RegServlet")
public class RegServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = "";

        HttpSession session = request.getSession();
        if(session.getAttribute("utenteLoggato")!=null){
            address = "/WEB-INF/results/areaUtente.jsp";
        }
        else if(session.getAttribute("adminLoggato")!=null){
            address = "/WEB-INF/results/areaAmministratore.jsp";
        }
        else {
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (nome == null || cognome == null || email == null || password == null) {
                request.setAttribute("errorReg", "errorReg");
                address = "/WEB-INF/results/registrazione.jsp";
            }
            else {

                //controllo input
                String regNomeCognome = "^[a-zA-Z]{3,}$";

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

                Pattern patternNomeCognome = Pattern.compile(regNomeCognome);
                Matcher matcherNome = patternNomeCognome.matcher(nome);
                Matcher matcherCognome = patternNomeCognome.matcher(cognome);

                if (!matcherEmail.matches() || !matcherPass.matches() || !matcherNome.matches() || !matcherCognome.matches()) {
                    request.setAttribute("errorReg", "errorReg");
                    address = "/WEB-INF/results/registrazione.jsp";
                }
                else {
                    //verifico se l'utente già esiste
                    UtenteDAO utenteDAO = new UtenteDAO();
                    Utente u = utenteDAO.doRetrieveByEmail(email);

                    if (u != null) {
                        request.setAttribute("utenteRegistrato", "utenteRegistrato");
                        address = "/WEB-INF/results/registrazione.jsp";
                    }
                    else {
                        //creo il nuovo utente
                        u = new Utente();
                        u.setNome(nome);
                        u.setCognome(cognome);
                        u.setEmail(email);
                        u.setPassword(password);

                        utenteDAO.doSave(u);

                        session.setAttribute("utenteLoggato", u);
                        address = "/WEB-INF/results/areaUtente.jsp";
                    }
                }
            }
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request,response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}