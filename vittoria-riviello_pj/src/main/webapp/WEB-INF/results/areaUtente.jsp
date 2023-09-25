<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Area Utente</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/styles.css">
</head>
<body>

    <%@ include file="../../header.jsp"%>

    <%
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        if(u!= null){
    %>

    <h1 id="titolo">Benvenuto nell'Area Utente</h1>
    <h3 style="text-align: center">Hai eseguito l'accesso con le seguenti credenziali:</h3>

    <div class="showCredenziali">

        <p> Nome:<span><%= u.getNome().toUpperCase() %></span></p>
        <p> Cognome:<span><%= u.getCognome().toUpperCase() %></span></p>
        <p> Email:<span><%= u.getEmail() %></span></p>

    </div>

    <p style="text-align: center">Visualizza i tuoi ordini <a class="styleGreen" href="MieiOrdiniServlet">I miei ordini</a></p>

    <p style="text-align: center">Disconnettiti <a class="styleRed" href="LogoutServlet">Esci</a></p>

    <%@ include file="../../footer.html"%>

    <% }else{ %>

        <script>
            alert('Accesso negato, non hai effettuato l\'accesso');
            window.location.href = "login.jsp";
        </script>

    <% } %>

</body>
</html>

