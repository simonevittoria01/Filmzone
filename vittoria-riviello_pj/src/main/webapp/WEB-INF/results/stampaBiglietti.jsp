<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Biglietto" %>
<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Stampa Biglietti Ordine</title>
    <link rel="stylesheet" href="./css/styles.css">
</head>
<body>
<%@ include file="../../header.jsp"%>

    <% Utente u = (Utente) session.getAttribute("utenteLoggato");
    if(u!=null){
        ArrayList<Biglietto> acquistati = (ArrayList<Biglietto>) session.getAttribute("bigliettiAcquistati");
        if(acquistati == null){ %>
        <h1 style="text-align: center">Nessun biglietto da mostrare</h1>
   <% }else if(acquistati.isEmpty()){ %>
    <h2 style="text-align: center">I biglietti acquistati sono stati eliminati per annullamento della proiezione, verrai rimborsato dei soldi spesi!</h2>
  <% }else{ %>
    <h1 id="titolo">I tuoi biglietti</h1>
    <%
        for(Biglietto b: acquistati){
        String orario = b.getDataOra();
        String[] parte = orario.split(" "); // Divide la stringa in base allo spazio
        String data = parte[0]; // Parte 0 contiene la data (GG/MM/AAAA)
        String ora = parte[1]; // Parte 1 contiene l'ora (HH:MM)

        // Divisione della data in giorno, mese e anno
        String[] dataParti = data.split("-");
        String giorno = dataParti[2]; // Parte 2 contiene il giorno
        String mese = dataParti[1]; // Parte 1 contiene il mese
        String anno = dataParti[0]; // Parte 0 contiene l'anno

        // Divisione dell'ora in ore e minuti
        String[] oraParti = ora.split(":");
        String ore = oraParti[0];
        String minuti = oraParti[1];%>
    <div class="biglietto">
        <h2 class="titolo-film">Film: <%=b.getFilm().getNome()%></h2>
        <p class="sala">Sala: <%=b.getNumSala()%></p>
        <p class="data-ora">Giorno: <%=giorno%>/<%=mese%>/<%=anno%> - <%=ore%>:<%=minuti%></p>
        <p class="fila-posto">Fila: <%=b.getFila()%> - Posto: <%=b.getPosto()%></p>
        <p class="prezzo">Prezzo: <%=b.getPrezzo()%> €</p>
    </div>

    <% }}}else {%>
    <script>
        alert('Accesso negato, non sei loggato');
    </script>
    <%}%>

<%@ include file="../../footer.html"%>

</body>
</html>
