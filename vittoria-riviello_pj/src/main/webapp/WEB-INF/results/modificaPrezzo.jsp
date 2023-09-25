<%@ page import="model.Utente" %>
<%@ page import="model.Film" %>
<%@ page import="model.Attore" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Modifica Prezzo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>
<body>
<%@ include file="../../header.jsp"%>

<%
    Utente u = (Utente) session.getAttribute("adminLoggato");
    if(u!=null){
        Film film = (Film) session.getAttribute("filmModify");
        if(film!=null){
%>
<h1 id="titolo">Funzione Amministratore</h1>
<h3 style="text-align: center">Aggiorna il prezzo del film:</h3>
<div class="showCredenziali">

    <p> Titolo:<span><%= film.getNome().toUpperCase() %></span></p>
    <p> Data di Uscita:<span><%= film.getDataUscita() %></span></p>
    <p> Genere:<span><%= film.getGenere().toUpperCase() %></span></p>
    <p> Durata:<span><%= film.getDurata() %></span></p>
    <p> Regista:<span><%= film.getRegista().getCognome().toUpperCase()%> <%= film.getRegista().getNome().toUpperCase() %> </span></p>
    <p> Cast: <span>
                <% ArrayList<Attore> attori = film.getAttori();
                    for(Attore a: attori){
                        if(a.equals(attori.get(attori.size()-1))){%>
                           <%=a.getNome()%> <%=a.getCognome()%>.
                       <%}else{%>
                <%=a.getNome()%> <%=a.getCognome()%>,
                <%}}%>
    </span></p>


</div>
<form method = "post" action = "UpdatePriceServlet" class = "formMain">
    <label for="prezzo" >Prezzo</label>
    <input type="number" id="prezzo" name="prezzo" placeholder="4.50" min="1" step="0.01" value = "<%=film.getPrezzo()%>" required><br>
    <input type="hidden" name="id" value="<%= film.getId() %>">
    <input type="submit" value="Cambia prezzo">
</form>



   <%
        //se il film è null
        }else{
   %>

    <script>
        alert('Si è verificato un errore nella tua richiesta');
        window.location.href = "index.html";
    </script>

  <%
        }

    //se l'utente è null
    }else {

  %>

    <script>
        alert('Accesso negato, non sei loggato come admin');
        window.location.href = "index.html";
    </script>

    <% } %>

    <%@ include file="../../footer.html"%>


</body>
</html>
