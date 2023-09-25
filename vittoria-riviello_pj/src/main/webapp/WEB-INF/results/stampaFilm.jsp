<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Ordine" %>
<%@ page import="model.Utente" %>
<%@ page import="model.Film" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Gestione Film</title>
  <link rel="stylesheet" href="./css/styles.css">
</head>
<body>
<%@ include file="../../header.jsp"%>

<%
  Utente u = (Utente) session.getAttribute("adminLoggato");
  if(u!=null){
    ArrayList<Film> film = (ArrayList<Film>) session.getAttribute("films");
    if(!film.isEmpty())
    {
%>
  <h1 id="titolo">Elenco dei Film</h1>
  <h3 style="text-align: center">Qui puoi modificare il prezzo di un film o eliminare una sua proiezione</h3>

<%
      for(Film f: film){
%>

<div class="order-details">
  <h2>Dettagli del film</h2>
  <p><strong>Titolo:</strong> <%=f.getNome()%></p>
  <p><strong>Data uscita:</strong> <%=f.getDataUscita()%></p>
  <p><strong>Prezzo:</strong> <%=f.getPrezzo()%></p>
  <p><strong>Genere:</strong> <%=f.getGenere()%></p>
  <a href='LoadFilmServlet?id=<%=f.getId()%>' class="styleGreen">Modifica Prezzo</a>
  <a href='LoadFilmServlet?id2=<%=f.getId()%>' class="styleGreen" style="background-color:red">Rimuovi Proiezione</a>
</div>
<%
  }
}else {%>
<h2 style="text-align: center">Nessun Film Disponibile</h2>
<%}%>

<%@ include file="../../footer.html"%>

<% }else {%>
<script>
  alert('Accesso negato, non sei loggato');
</script>
<%}%>

</body>
</html>
