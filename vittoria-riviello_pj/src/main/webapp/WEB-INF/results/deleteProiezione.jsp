<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.*" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<html>
<head>
    <title>Elimina Proiezione</title>
    <link rel="stylesheet" href="./css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>

<body>

<%@ include file="../../header.jsp"%>

<%
    Utente admin = (Utente) session.getAttribute("adminLoggato");
    if(admin != null){
        Film f = (Film) session.getAttribute("filmDelete");
        if(f!=null){
            ArrayList<Attore> attori = f.getAttori();
            ArrayList<Proiezione> proiezioni = (ArrayList<Proiezione>) session.getAttribute("proiezioni");
            String copertina = Base64.encodeBase64String(f.getCopertina());
%>

<h1 id="titolo">Funzione Amministratore</h1>
<h3 style="text-align: center">Scegli la proiezione che desideri eliminare</h3>

<div id="containerPagefilm">

    <div id="filmimage">
        <img src="data:image/jpeg;base64,<%= copertina %>" alt="Copertina">
    </div>

    <div id="infofilm">
        <p class="film-title">Titolo:<span class="film-info"><%= f.getNome()%></span></p>
        <p class="film-title">Durata:<span class="film-info"><%= f.getDurata()%> min</span></p>
        <p class="film-title">Data uscita:<span class="film-info"><%= f.getDataUscita()%></span></p>
        <p class="film-title">Genere:<span class="film-info"><%= f.getGenere()%></span></p>
        <p class="film-title">Prezzo:<span class="film-info"><%= f.getPrezzo()%> &euro;</span></p>
        <p class="film-title">Descrizione:<span class="film-info"><%= f.getDescrizione()%></span></p>
        <p class="film-title">Regista:<span class="film-info"><%= f.getRegista().getNome() + " " + f.getRegista().getCognome() %></span></p>
        <p class="film-title">Attori:<span class="film-info">

                <%
                    for(Attore a: attori){
                        if(a.equals(attori.get(attori.size()-1))){
                %>

                <%= a.getNome()%> <%=a.getCognome() %>

                <%  }else{  %>

                <%= a.getNome()%> <%=a.getCognome() %>,

                <%  }}  %></span></p>

        <p class="film-title" style="display: inline-block">Proiezioni:</p>

        <select id="data" name="data" oninput="verificaorario()" style="display: inline-block; margin-left: 10px">

            <option value="<%=proiezioni.get(0).getDataOra().substring(0,10)%>"><%= proiezioni.get(0).getDataOra().substring(8,10) %>-<%= proiezioni.get(0).getDataOra().substring(5,7) %>-<%= proiezioni.get(0).getDataOra().substring(0,4) %></option>

            <%
                for(int i = 1; i < proiezioni.size(); i++){
                    if(!(proiezioni.get(i).getDataOra().substring(0,10).equalsIgnoreCase(proiezioni.get(i-1).getDataOra().substring(0,10)))){
            %>
            <option value="<%=proiezioni.get(i).getDataOra().substring(0,10)%>"><%= proiezioni.get(i).getDataOra().substring(8,10) %>-<%= proiezioni.get(i).getDataOra().substring(5,7) %>-<%= proiezioni.get(i).getDataOra().substring(0,4) %></option>
            <% }} %>

        </select>

        <select id="ora" name="ora"></select>

        <button id="rimuoviproiezione">Rimuovi Proiezione</button>

    </div>
</div>


<%@ include file="../../footer.html"%>


<script>

    $(document).ready(function (){
        verificaorario();

        $('#rimuoviproiezione').click(function() {
            var film = '<%=f.getId()%>';
            var sala = '<%=proiezioni.get(0).getNumSala()%>';
            var data = $('#data').val();
            var ora = $('#ora').val();

            rimuoviProiezione(film, sala, data, ora);
        });
    });

    function verificaorario(){

        let selectdata = document.getElementById("data");
        let optionselected = selectdata.options[selectdata.selectedIndex].value;  //elemento selezionato

        $.post("CheckOrariProiezioniServlet",
            {
                "data": optionselected
            },
            function (data) {
                $("#ora").empty();
                const orari = data.split(" ");
                orari.forEach(function (value){
                    let orario = $("<option></option>").text(value.substring(0,5));
                    $("#ora").append(orario)
                })
            }
        );
    }


    function rimuoviProiezione(film, sala, data, ora){
        var selectData = document.getElementById("data");
        var selectOrario = document.getElementById("ora");
        $.ajax({
            method: 'POST',
            url: 'DeleteProiezioneServlet',
            data: {
                sala: sala,
                film: film,
                data: data,
                ora: ora
            }
        })
            .done(function() {
                /*se e l'ultimo ad essere eliminato stampo alert diverso*/
                if(selectData.options.length == 1 && selectOrario.options.length ==1){
                    alert('Tutte le proiezioni sono eliminate');
                    window.location.href = "index.html";
                }else{
                    alert('Proiezione eliminata con successo');
                    location.reload();
                }
            })
            .fail(function() {
                alert('Verificato un errore durante l\'eliminazione.');
            });
    }

</script>

    <% }
        //se il film è null
        else{ %>

            <script>
                alert("Si è verificato un errore nella tua richiesta, riprova")
                window.location.href = "index.html";
            </script>

    <% }
        //se l'admin è null
        }else {
    %>

        <script>
            alert("Accesso negato, non hai i permessi per accedere a questa pagina")
            window.location.href = "index.html";
        </script>

    <% } %>

</body>
</html>