<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Area Amministratore</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/styles.css">
</head>
<body>
    <%@ include file="../../header.jsp"%>

    <%
        Utente u = (Utente) session.getAttribute("adminLoggato");
        if(u!=null){
    %>

    <h1 id="titolo">Benvenuto nell'Area Amministratore</h1>
    <h3 style="text-align: center">Seleziona cosa vuoi fare:</h3>

    <p style="text-align: center">Inserisci un nuovo film <a class="styleGreen" href="RedirectServlet?destinazione=newFilm.jsp">Inserisci</a></p>

    <p style="text-align: center">Modifica il prezzo dei biglietti di un film <a class="styleGreen" href="GestioneFilmServlet">Gestione Film</a></p>

    <p style="text-align: center">Disconnettiti <a class="styleRed" href="LogoutServlet">Esci</a></p>

    <%@ include file="../../footer.html"%>

    <% }else{%>

        <script>
            alert('Accesso negato, non hai i giusti permessi per accedere a questa pagina');
            window.location.href = "login.jsp";
        </script>

    <% } %>

    <% if(request.getAttribute("prezzoOk")!=null){ %>

    <script>
        alert("Il prezzo del film è stato aggiornato con successo")
    </script>

    <% } %>

</body>
</html>
