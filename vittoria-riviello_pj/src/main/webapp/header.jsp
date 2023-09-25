<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
</head>

<body>

<nav>
    <ul>
        <li class="border-right"><a href="index.html">Home</a></li>
        <li><a href="cercaFilm.jsp">Cerca Film</a></li>
        <li class="right"><a href="CartServlet"><i class="fas fa-shopping-cart"></i></a></li>
        <% if(session.getAttribute("utenteLoggato") != null) {%>
            <li class="right border-right"><a href = "RedirectServlet?destinazione=areaUtente.jsp">Area Utente<i class="fas fa-user"></i></a></li>
        <% } else if(session.getAttribute("adminLoggato") != null) {%>
        <li class="right border-right"><a href = "RedirectServlet?destinazione=areaAmministratore.jsp">Area Amministratore<i class="fas fa-user"></i></a></li>
        <% } else {%>
        <li class="right border-right"><a href = "login.jsp">Accedi<i class="fas fa-user"></i> </a ></li>
        <% } %>
    </ul>
</nav>

</body>
</html>
