<%@ page import="java.util.ArrayList"%>
<%@ page import="model.Film"%>
<%@ page import="org.apache.commons.codec.binary.Base64"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" href="./css/styles.css">
</head>

<body>

    <%@ include file="header.jsp"%>

    <div class="slideshow-container">
        <%
            if(session.getAttribute("films")!=null) {
                ArrayList<Film> films = (ArrayList<Film>) session.getAttribute("films");
                for(Film f: films){
                    String base64Image = Base64.encodeBase64String(f.getCopertina());
        %>

        <div class="mySlides fade">
            <img src="data:image/jpeg;base64,<%= base64Image %>" alt="Copertina">
        </div>

    <% } %>
    </div>

    <div id="divdot" style="text-align:center">
    <% for(int i = 0; i<films.size();i++){ %>

        <span class="dot"></span>

    <% }} %>
    </div>

    <h1 id="titolo">FILM IN PROIEZIONE NELLE PROSSIME SETTIMANE</h1>
    <%
        if(session.getAttribute("films")!=null) {
            ArrayList<Film> films = (ArrayList<Film>) session.getAttribute("films");
            for(Film f: films){
                String base64Image = Base64.encodeBase64String(f.getCopertina());
    %>

    <div id="boxfilm">

        <div id="filmimageindex">
            <img src="data:image/jpeg;base64,<%= base64Image %>" alt="Copertina">
        </div>

        <div id="infofilm">
            <p class="film-title">Titolo:<a href="PageFilmServlet?id=<%= f.getId() %>"><%=f.getNome()%></a></p>
            <p class="film-title">Durata:<span class="film-info"><%= f.getDurata()%> min</span></p>
            <p class="film-title">Data uscita:<span class="film-info"><%= f.getDataUscita()%></span></p>
            <p class="film-title">Genere:<span class="film-info"><%= f.getGenere()%></span></p>
        </div>
    </div>

    <%
            }
            if(films.size()==0){
    %>

        <h3 style="text-align: center">Non ci sono film in proiezione nelle prossime settimane</h3>

    <%
        }
        }else{
    %>

    <script>
        alert("Si è verificato un errore durante il caricamento dei film");
    </script>

    <%
        }
    %>

    <%@ include file="footer.html"%>

    <script>
        var index = 0;
        showSlides();

        function showSlides() {
            var i;
            var slides = document.getElementsByClassName("mySlides");
            var dots = document.getElementsByClassName("dot");
            for (i = 0; i < slides.length; i++) {
                slides[i].style.display = "none";
            }
            index++;
            if (index > slides.length) {index = 1}
            for (i = 0; i < dots.length; i++) {
                dots[i].className = dots[i].className.replace(" active", "");
            }
            slides[index-1].style.display = "block";
            dots[index-1].className += " active";
            setTimeout(showSlides, 6000);
        }
    </script>

    <%
        if(request.getAttribute("errore") != null && request.getAttribute("errore").toString().equalsIgnoreCase("permessi")){
    %>

        <script>
            alert("Non hai i giusti permessi per accedere a questa pagina");
        </script>

    <% } else if(request.getAttribute("errore") != null && request.getAttribute("errore").toString().equalsIgnoreCase("errore")){ %>

    <script>
        alert("Si è verificato un errore nella tua richiesta");
    </script>

    <% }else if(request.getAttribute("filmSettato")!=null){ %>
    <script>
        alert("Film e proiezioni aggiunte con successo!");
    </script>
    <% } else if(request.getAttribute("aggiuntaCarrello")!=null){ %>
    <script>
        alert('Hai aggiunto un biglietto nel tuo carrello! In caso di superamento del limite massimo di 5 biglietti, il biglietto non verrà aggiunto');
    </script>
    <% } else if(request.getAttribute("erroreAcquistoBigl")!=null){ %>
    <script>
        alert("Nessun biglietto aggiunto al carrello e stato acquistato per mancanza di disponibilita");
    </script>
    <% }else if(request.getAttribute("cambioPrezzo") !=null){ %>
    <script>
        alert("Il prezzo di qualche film all'interno del carrello e' cambiato, guarda i cambiamenti nel carrello e decidi se continuare a comprare.");
    </script>

    <% } else if(request.getAttribute("removeAllTicket") !=null){ %>
    <script>
        alert("Tutti i biglietti sono stati rimossi dal carrello");
    </script>
    <%}else if(request.getAttribute("addTicketServlet") !=null){ %>
    <script>
        alert("I biglietti nel carrello sono stati aggiornati");
    </script>
    <%}%>

</body>
</html>
