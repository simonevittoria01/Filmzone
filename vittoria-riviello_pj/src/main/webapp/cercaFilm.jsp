<%@ page import="model.Genere" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Film" %>
<%@ page import="org.apache.commons.codec.binary.Base64"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cerca Film</title>
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="0">
    <link rel="stylesheet" href="css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>
<body>

    <% ArrayList<Genere> generi = (ArrayList<Genere>) application.getAttribute("generi"); %>

    <%@ include file="header.jsp"%>

    <h1 id="titolo">CERCA UN FILM PER GENERE O PER TITOLO</h1>
    <h3 style="text-align: center">Controlla se il film che stai cercando è in proiezione nelle nostre sale!</h3>


    <form action="SearchFilmServlet" method="post" class="formMain" id="cercafilmform">
        <label for="nomeFilm">Nome</label>
        <input type="text" id="nomeFilm" name="nomeFilm">

        <label for="genere">Genere</label>
        <select id="genere" name="genere">
            <option value=""></option>
            <% for(Genere g : generi){ %>
            <option value="<%= g.getNome() %>"><%= g.getNome() %></option>
            <% } %>

        </select>

        <input type="submit" id="cercaFilm" value="cerca">
    </form>

    <%
        if(request.getAttribute("filmtrovato")!=null) {
            Film f = (Film) request.getAttribute("filmtrovato");
            String base64Image = Base64.encodeBase64String(f.getCopertina());
    %>

    <div class="boxfilm2">
        <a href="PageFilmServlet?id=<%= f.getId() %>"><img src="data:image/jpeg;base64,<%= base64Image %>" alt="Copertina"></a>
    </div>

    <% }
        else if(request.getAttribute("filmstrovati")!=null) {
            ArrayList<Film> films = (ArrayList<Film>) request.getAttribute("filmstrovati");
            for(Film f: films){
                String base64Image = Base64.encodeBase64String(f.getCopertina());
    %>

    <div class="boxfilm2">
        <a href="PageFilmServlet?id=<%= f.getId() %>"><img src="data:image/jpeg;base64,<%= base64Image %>" alt="Copertina"></a>
    </div>

    <% }} %>

    <%
        if(request.getAttribute("errorericerca")!=null) {
    %>

    <script>
        alert("La ricerca non ha prodotto risultati")
    </script>

    <% } %>


    <%@ include file="footer.html"%>

</body>
</html>
