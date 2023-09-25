<%@ page contentType="text/html;charset=UTF-8"%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Registrazione</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="./css/styles.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>
<body>

    <%@ include file="../../header.jsp"%>

    <h1 id="titolo">Benvenuto nella pagina di Registrazione</h1>
    <h3 style="text-align: center">Inserisci i tuoi dati</h3>

    <form id="formReg" name="formReg" class="formMain" action="RegServlet" method="post" onsubmit="return validateFormReg()">

        <label for="nome">Nome</label>
        <input type="text" id="nome" name="nome" onblur="checkNome()" placeholder="Mario" required>

        <label for="cognome">Cognome</label>
        <input type="text" id="cognome" name="cognome" onblur="checkCognome()" placeholder="Rossi" required>

        <p id="nomeError">Formato Nome o Cognome non corretto: Inserire almeno 3 caratteri, solo lettere e senza spazi!</p><br>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" onblur="checkEmail()" placeholder="mariorossi@gmail.com" required>

        <p id="emailError">Formato Email non corretto! Esempio corretto: mariorossi@gmail.com</p><br>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" onblur="checkPass()" placeholder="Psw123" required>

        <p id="passError">Formato Password non corretto: Inserire minimo 6 caratteri!</p><br>

        <input type="submit" value="Registrati">

    </form>


    <%
        if(request.getAttribute("errorReg") != null){
    %>

    <script>
        alert("Errore nella registrazione, riprova");
    </script>


    <%
        }else if(request.getAttribute("utenteRegistrato") != null){
    %>

    <script>
        alert("Esiste già un account con questo indirizzo email, riprova inserendo un altro indirizzo email oppure effettuando il login");
    </script>

    <% } %>

    <script>

        var nomeCheck = false;
        var cognomeCheck = false;
        var emailCheck = false;
        var passCheck = false;

        function checkNome(){
            let nomeVal = document.forms["formReg"]["nome"].value;

            const pattern = /^[a-zA-Z]{3,}$/;

            if (!pattern.test(nomeVal) && nomeVal != "") {
                document.getElementById("nome").style.border = "2px solid red";
                document.getElementById("nomeError").style.display = "block";
                nomeCheck = false;
            }
            else {
                document.getElementById("nome").style.border = null;
                if(cognomeCheck != false)
                    document.getElementById("nomeError").style.display = "none";
                nomeCheck = true;
            }

        }

        function checkCognome(){
            let cognomeVal = document.forms["formReg"]["cognome"].value;
            const pattern = /^[a-zA-Z]{3,}$/;

            if (!pattern.test(cognomeVal) && cognomeVal != "") {
                document.getElementById("cognome").style.border = "2px solid red";
                document.getElementById("nomeError").style.display = "block";
                cognomeCheck = false;
            }
            else {
                document.getElementById("cognome").style.border = null;
                if(nomeCheck != false)
                    document.getElementById("nomeError").style.display = "none";
                cognomeCheck = true;
            }

        }

        function checkEmail(){
            let emailVal = document.forms["formReg"]["email"].value;
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
            let passVal = document.forms["formReg"]["password"].value;
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

        function validateFormReg(){
            let nome = $("#nome").val();
            let cognome = $("#cognome").val();
            let email = $("#email").val();
            let password = $("#password").val();


            if(nomeCheck == false || cognomeCheck == false || emailCheck == false || passCheck == false
                || nome == "" || cognome == "" || email == "" || password == "")
                return false;

            return true
        }

    </script>

    <%@ include file="../../footer.html"%>

</body>
</html>