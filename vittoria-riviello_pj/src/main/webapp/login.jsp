<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>

    <%@ include file="header.jsp"%>

    <%
        Utente u = (Utente) session.getAttribute("utenteLoggato");
        Utente admin = (Utente) session.getAttribute("adminLoggato");
        if(u == null && admin == null){
    %>

    <h1 id="titolo">Benvenuto nella pagina di Login</h1>
    <h3 style="text-align: center">Inserisci le tue credenziali</h3>

    <form method="post" id="formLogin" name="formLogin" class="formMain" action="CheckLoginServlet" onsubmit="return validateForm()">

        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="mariorossi@gmail.com" onblur="checkEmail()" required>
        <p id="emailError">Formato Email non corretto! Esempio corretto: mariorossi@gmail.com</p><br>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="Psw123" onblur="checkPass()" required>
        <p id="passError">Formato Password non corretto: Inserire minimo 6 caratteri!</p><br>

        <input type="submit" value="Accedi">

    </form>

    <p style="text-align: center">Se non sei registrato clicca il pulsante di seguito <a class="styleGreen" href="RedirectServlet?destinazione=registrazione.jsp">Registrati</a>

    <%
        if(request.getAttribute("errorLogin") != null && request.getAttribute("errorLogin").toString().equalsIgnoreCase("formato")){
    %>

        <script>
            alert("Errore nelle credenziali, riprova");
        </script>

    <% }else if(request.getAttribute("errorLogin") != null && request.getAttribute("errorLogin").toString().equalsIgnoreCase("errorLogin")){ %>

    <script>
        alert("Utente non registrato o credenziali errate, riprova");
    </script>

    <% } else if (request.getAttribute("noLogin") != null){ %>

        <script>
            alert("Utente non registrato, riprova");
        </script>

    <% }
        }else {
    %>
        <script>
            window.location.href = "index.html";
        </script>

    <% } %>

        <%@ include file="footer.html"%>

    <script>

        var emailCheck = false;
        var passCheck = false;

        function checkEmail(){
            let emailVal = document.forms["formLogin"]["email"].value;
            const patternEmail = /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/g;
            if (!patternEmail.test(emailVal) && emailVal != "") {
                document.getElementById("email").style.border = "2px solid red";
                document.getElementById("emailError").style.display = "block";
                emailCheck = false;
            }
            else {
                document.getElementById("email").style.border = null;
                document.getElementById("emailError").style.display = "none";
                emailCheck = true;
            }
        }

        function checkPass(){
            let passVal = document.forms["formLogin"]["password"].value;
            const patternPass = /^.{6,}$/;
            if (!patternPass.test(passVal) && passVal != "") {
                document.getElementById("password").style.border = "2px solid red";
                document.getElementById("passError").style.display = "block";
                passCheck = false;
            }
            else {
                document.getElementById("password").style.border = null;
                document.getElementById("passError").style.display = "none";
                passCheck = true;
            }
        }


        function validateForm() {
            let email = $("#email").val();
            let psw = $("#password").val();

            if(passCheck == false || emailCheck == false || email == "" || psw == "")
                return false;

            return true;
        }


    </script>




</body>
</html>
