<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.*" %>
<%@ page import="org.apache.commons.codec.binary.Base64" %>
<html>
<head>
    <title>Scheda Film</title>
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
        Film f = (Film) session.getAttribute("film");

        ArrayList<Attore> attori = f.getAttori();
        ArrayList<Proiezione> proiezioni = (ArrayList<Proiezione>) session.getAttribute("proiezioni");
        ArrayList<Recensione> recensioni = (ArrayList<Recensione>) session.getAttribute("recensioni");
        String copertina = Base64.encodeBase64String(f.getCopertina());

        boolean filmpassato = false;
        if(proiezioni == null || proiezioni.isEmpty()){
            filmpassato = true;
        }

        int utenteId = -1;

        Utente u = (Utente) session.getAttribute("utenteLoggato");
        if(u != null) {
            utenteId = u.getId();
        } else if(session.getAttribute("adminLoggato") != null) {
            utenteId = 1;
        }
    %>


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

            <%
                if(!filmpassato){
            %>

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

            <button id="acquistabiglietto">Aggiungi a Carrello</button>

            <% } %>

        </div>
    </div>

    <div id="divRecensioni">

    <h1>Recensioni degli altri utenti</h1>

    <%
        if(!(recensioni == null || recensioni.isEmpty())){
    %>

    <p align="center">Voto Medio Film: <span id="total-amount">0</span></p>

    <%
        for (Recensione r : recensioni){
    %>

    <div class = "recensione-item">
    <hr width="50%">

    <h4 style="text-align: center">Voto</h4>
    <p style="text-align: center" class = "votorecensione"><%= r.getVoto() %></p>
    <h4 style="text-align: center">Commento</h4>
    <p style="text-align: center"><%= r.getCommento() %></p>
    </div>

    <% }}else{ %>

    <p align="center">Non sono presenti recensioni per questo film</p>

    <% } %>

    </div>

    <%
        if(!filmpassato){
    %>

    <h1 id="rec">Lascia una tua recensione</h1>

    <div id="newRecensione">

        <form class="formMain" name="formRec" method="post" action="RecensioneServlet" onsubmit="return validateFormRecensione()">
            <label for="recensione">Inserisci una recensione</label>
            <textarea rows="4" cols="50" id="recensione" name="recensione" placeholder="Inserisci una commento" style="resize: none" onblur="checkRec()" required></textarea>
            <p id="recError">Recensione troppo lunga, inserire al massimo 255 caratteri</p><br>

            <label for="voto">Voto (da 1 a 5)</label>
            <input type="number" id="voto" name="voto" min="1" max="5" required>

            <input type="submit" value="Salva Recensione">

            <input type="hidden" name="idfilm" value="<%=f.getId()%>">
            <input type="hidden" name="idutente" value="<%=utenteId%>">
        </form>

    </div>

    <% } %>


    <%
        if(request.getAttribute("Recensione") != null){
    %>
        <script>

            alert("Recensione aggiunta correttamente");

        </script>

    <% }else if(request.getAttribute("errorRecensione") != null){ %>

        <script>

            alert("Si è verificato un errore nell'aggiunta della recensione, riprova");

        </script>

    <% }else if(request.getAttribute("RecensioneEsistente") != null){ %>

        <script>

            alert("Non puoi aggiungere più di una recensione per lo stesso film");

        </script>

    <% } else if(request.getAttribute("errore") != null){ %>

        <script>

            alert("Si è verificato un errore nella tua richiesta");

        </script>

    <% } %>


    <%@ include file="../../footer.html"%>


    <script>
        var recCheck = false;


        $(document).ready(function() {
            calculateTotal();
        });

        function calculateTotal() {
            var items = document.getElementsByClassName("recensione-item");
            var totale = 0;

            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var voto = parseFloat(item.querySelector(".votorecensione").innerText);


                totale += voto;
            }
            totale = totale/items.length;
            $("#total-amount").text(totale.toFixed(2));
        }


        <%
            if(!filmpassato){
        %>

            $(document).ready(function (){
                verificaorario();

                $('#acquistabiglietto').click(function() {
                    var film = '<%=f.getId()%>';
                    var sala = '<%=proiezioni.get(0).getNumSala()%>';
                    var data = $('#data').val();
                    var ora = $('#ora').val();
                    if(validateFormAcquistoBiglietti() == true) {
                        aggiungiCarrello(film, sala, data, ora);
                    }
                });
            });

        <% } %>

        function verificaorario(){

            let selectdata = document.getElementById("data");
            let optionselected = selectdata.options[selectdata.selectedIndex].value;

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


        function validateFormRecensione() {
            let validate = <%= utenteId %>;

            let recensione = $("#recensione").val();
            let voto = $("#voto").val();

            if(validate < 1) {
                alert("Devi accedere per poter lasciare una recensione");
                return false;
            }
            else if(validate == 1) {
                alert("L'admin non può lasciare una recensione");
                return false;
            }
            else if(voto == "" || !recCheck || recensione.trim() == ""){
                return false;
            }

            return true;
        }

        function checkRec(){
            let reccval = document.forms["formRec"]["recensione"].value;

            if (reccval.length>255) {
                document.getElementById("recensione").style.border = "2px solid red";
                document.getElementById("recError").style.display = "block";
                recCheck = false;
            }
            else {
                document.getElementById("recensione").style.border = null;
                document.getElementById("recError").style.display = "none";
                recCheck = true;
            }

        }

        function validateFormAcquistoBiglietti() {
            let validate = <%= utenteId %>;

            if(validate < 1) {
                alert("Devi accedere per procedere con l'acquisto dei biglietti");
                return false;
            }
            if(validate == 1) {
                alert("L'admin non può acquistare biglietti");
                return false;
            }

            return true;
        }

        function aggiungiCarrello(film, sala, data, ora){
            $.ajax({
                method: 'POST',
                url: 'FirstAggiuntaServlet',
                data: {
                    sala: sala,
                    film: film,
                    data: data,
                    ora: ora
                }
            })
                .done(function() {
                    alert('Hai aggiunto un biglietto nel tuo carrello! In caso di superamento del limite massimo di 5 biglietti, il biglietto non verrà aggiunto');
                })
                .fail(function() {
                    alert('Verificato un errore durante l\'aggiornamento.');
                });
        }

    </script>

</body>
</html>