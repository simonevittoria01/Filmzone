<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Ordine" %>
<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>I miei ordini</title>
    <link rel="stylesheet" href="./css/styles.css">
</head>
<body>
<%@ include file="../../header.jsp"%>

<%
    Utente u = (Utente) session.getAttribute("utenteLoggato");
    if(u!=null){
    ArrayList<Ordine> ordiniConfermati = (ArrayList<Ordine>) session.getAttribute("ordiniConfermati");
    if(!ordiniConfermati.isEmpty())
    {
%>

<h1 id="titolo">I miei ordini</h1>
<h3 style="text-align: center">Qui puoi visualizzare i tuoi biglietti</h3>

    <%
        for(Ordine o: ordiniConfermati){
    %>

<div class="order-details">
    <h2>Dettagli dell'ordine</h2>
    <p><strong>Codice ordine:</strong> <%=o.getId()%></p>
    <p><strong>Data d'acquisto:</strong> <%=o.getDataAcquisto()%></p>
    <form action="VisualizzaOrdineServlet" method="POST">
        <input type="hidden" name="id" value="<%=o.getId()%>">
        <button type="submit">Visualizza</button>
    </form>
</div>
<%
     }
    }else {%>
    <h2 style="text-align: center">Nessun Ordine confermato</h2>
<%}%>

  <%@ include file="../../footer.html"%>

<% }else {%>
<script>
    alert('Accesso negato, non sei loggato');
</script>
<%}%>

</body>
</html>
