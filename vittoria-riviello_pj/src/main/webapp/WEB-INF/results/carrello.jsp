<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="model.*" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Carrello</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>

    <script defer>
        $(document).ready(function() {
            calculateTotal();
        });
        function calculateTotal() {
                var items = document.getElementsByClassName("cart-item");
                var totale = 0;

                for (var i = 0; i < items.length; i++) {
                    var item = items[i];
                    var quantita = parseInt(item.querySelector(".item-quantity").value);
                    var prezzo = parseFloat(item.querySelector(".item-price").innerText);

                    var subTotale = quantita * prezzo;
                    totale += subTotale;
                }
                $("#total-amount").text(totale.toFixed(2));
        }

        function updateQuantity(selectElement, data, ora, film, sala, ordine) {
            var nuovo = selectElement.value;

            $.ajax({
                method: 'POST',
                url: 'AddTicketServlet',
                data: {
                    nuovo: nuovo,
                    ordine: ordine,
                    sala: sala,
                    film: film,
                    data: data,
                    ora: ora
                }
            })
                .done(function() {
                    calculateTotal();
                })
                .fail(function() {
                    alert('Verificato un errore durante l\'aggiornamento.');
                });
        }

        function removeAll(data, ora, film, sala, ordine) {

            $.ajax({
                method: 'POST',
                url: 'RemoveAllTicket',
                data: {
                    ordine: ordine,
                    sala: sala,
                    film: film,
                    data: data,
                    ora: ora
                }
            })
                .done(function() {
                    alert('Eliminazione di tutti i biglietti della proiezione scelta effettuata');
                    location.reload();
                })
                .fail(function(jqXHR, textStatus, errorThrown) {
                    console.error("Errore durante l'aggiornamento:", errorThrown, textStatus, jqXHR);
                    alert('Verificato un errore durante l\'aggiornamento. Controlla la console per ulteriori dettagli.');
                });
        }
    </script>
</head>
<body>
<%@ include file="../../header.jsp"%>

    <% Utente u = (Utente) session.getAttribute("utenteLoggato");
    if(u!= null){
        LinkedHashMap<Film, LinkedHashMap<String, Integer>> biglietti = (LinkedHashMap<Film, LinkedHashMap<String, Integer>>) session.getAttribute("biglietti");
        Ordine o = (Ordine) session.getAttribute("ordine");
        ArrayList<Proiezione> proiezioni = (ArrayList<Proiezione>) session.getAttribute("proiezioni");
        if(biglietti == null || biglietti.isEmpty()){
    %>

    <h2 style="text-align: center">Il carrello attualmete è vuoto</h2>
    <%
        }else{
    %>

    <h1 id="titolo">Il tuo carrello</h1>

<div class="cart-container">
    <%
        int i = 0;
        for (Film film : biglietti.keySet()) {
            LinkedHashMap<String, Integer> conteggioPerOrario = biglietti.get(film);
            int max = proiezioni.get(i).getPostiDisponibili();
            if(max > 5)
                max = 5; //massimo 5 biglietti per film
            for (Map.Entry<String, Integer> entry : conteggioPerOrario.entrySet()) {
                String orario = entry.getKey();
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
                String minuti = oraParti[1];
    %>
    <div class="cart-item">
        <img src="./img/biglietto.jpg">
        <div class="item-details">
            <h2 class="item-name"><%=film.getNome()%></h2>
            <p class = "item-datetime">Giorno Proiezione: <%=giorno%>/<%=mese%>/<%=anno%></p>
            <p class = "item-datetime">Orario Proiezione: <%=ore%>:<%=minuti%></p>
            <p class="item-price"><%=film.getPrezzo()%> &euro;</p>
        </div>
        <div class="quantity-container">
            <label for="quantitaArticoli">
                <select id = "quantitaArticoli" class="item-quantity"  onchange="updateQuantity(this,'<%=parte[0]%>', '<%=parte[1]%>',<%=proiezioni.get(i).getFilm()%>, '<%=proiezioni.get(i).getNumSala()%>', <%=o.getId()%>)">
                    <% for(int j = 1; j<=max ; j++){%>
                    <option value="<%=j%>" <%if(j == conteggioPerOrario.get(orario)){%>selected<%}%>><%=j%></option>
                    <%}%>
                </select>
            </label>
        </div>
        <button class="remove-btn" onclick="removeAll('<%=parte[0]%>', '<%=parte[1]%>',<%=proiezioni.get(i).getFilm()%>, '<%=proiezioni.get(i).getNumSala()%>', <%=o.getId()%>, <%=i%>)">Rimuovi</button>

    </div>
    <%i++;}%>
    <%}%>
    <div class="checkout-container">
        <a href = "RedirectServlet?destinazione=pagamento.jsp"><button class="checkout-btn">Acquista</button></a>
    </div>
    <div id="total-container">
        <h2>Totale: <span id="total-amount">0</span> &euro;</h2>
    </div>
    <%}%>


</div>

<% }else {%>
<script>
    alert('Accesso negato, non sei loggato');
</script>
<%}%>

</body>


</html>