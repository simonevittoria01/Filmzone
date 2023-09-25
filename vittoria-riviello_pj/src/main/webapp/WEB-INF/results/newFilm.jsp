<%@ page import="java.util.ArrayList" %>
<%@ page import="model.Genere" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="model.Utente" %>

<html>
<head>
    <title>Inserimento Film</title>
    <link rel="stylesheet" href="./css/styles.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>

<body>

    <%
        Utente u = (Utente) session.getAttribute("adminLoggato");
        if(u!=null){
            ArrayList<Genere> generi = (ArrayList<Genere>) application.getAttribute("generi");
    %>

    <%@ include file="../../header.jsp"%>


    <h1 id="titolo">Benvenuto nella pagina di Inserimento Film</h1>
    <h3 style="text-align: center">Inserisci le informazioni del film</h3>

    <p style="text-align: center">Il film verrà trasmesso per 7 giorni a partire dal lunedì seguente alla data selezionata</p>
    <form method = "post" action = "AddFilmServlet" name="formFilm" id="newfilm" class="formMain" enctype="multipart/form-data" onsubmit="return validateFormFilm()">

        <label for="nome">Titolo</label>
        <input type="text" id="nome" name="nome" placeholder="Hobbs & Shaw" onblur="checkTitolo()" required>
        <p id="nomeError">Titolo troppo lungo, inserire al massimo 255 caratteri</p><br>

        <label for="durata">Durata (in minuti)</label>
        <input type="number" id="durata" name="durata" min="1" placeholder="120" required>

        <label for="datainizio">Data inizio proiezioni</label>
        <input type="date" id="datainizio" name="datainizio" oninput="checkData()" required>

        <label for="genere">Genere </label>
        <select id="genere" name="genere">

            <% for(Genere g : generi){ %>
                <option value="<%= g.getNome() %>"><%= g.getNome() %></option>
            <% } %>

        </select>

        <label for="prezzo">Prezzo</label>
        <input type="number" id="prezzo" name="prezzo" placeholder="4.50" min="1" step="0.01" required>

        <label for="descrizione">Descrizione</label>
        <textarea rows="4" cols="50" id="descrizione" name="descrizione" placeholder="Inserisci una breve descrizione del film" style="resize: none" onblur="checkDesc()" required></textarea>
        <p id="descError">Descrizione troppo lunga, inserire al massimo 255 caratteri</p><br>

        <label for="copertina">Copertina</label>
        <input type="file" id="copertina" name="copertina" accept="image/jpeg, image/png">

        <label for="regista">Regista</label>
        <input type="text" id="regista" name="regista" placeholder="David Leitch" onblur="checkRegista()" required>
        <p id="registaError">Formato regista non corretto: Inserire un solo nome e un solo cognome separati dallo spazio (solo lettere)! Esempio corretto: David Leitch</p><br>

        <label for="attori">Attori</label>
        <textarea rows="4" cols="50" id="attori" name="attori" placeholder="Dwayne Johnson,Jason Statham" style="resize: none" onblur="checkAttori()" required></textarea>
        <p id="attoriError">Formato attori non corretto: Per ogni attore inserire un solo nome e un solo cognome, gli attori sono separati dalla virgola senza spazio! Esempio corretto: Dwayne Johnson,Jason Statham</p><br>

        <input type="submit" value="Inserisci Film">

    </form>

    <%
        if(request.getAttribute("errorNewFilm") != null){
    %>

    <script>
        alert("Si è verificato un errore nell'inserimento del film, riprova");
    </script>


    <%
        }else if(request.getAttribute("filmEsistente") != null){
    %>

    <script>
        alert("Esiste già un film con questo titolo, riprova");
    </script>

    <%
        }else if(request.getAttribute("erroreData") != null){
    %>

    <script>
        alert("Il formato o la data inserita non è corretta");
    </script>

    <% }
       }else { %>

    <script>
        alert('Accesso negato, non hai i giusti permessi per accedere a questa pagina');
        window.location.href = "index.html";
    </script>

    <% } %>


    <script>
        //setto l'attributo min del campo datainizio a oggi
        let todayString = new Date().toISOString().split('T')[0];
        document.forms["formFilm"]["datainizio"].setAttribute("min", todayString)

        //controllo input
        let registaCheck = false;
        let attoriCheck = false;
        let dataCheck = false;
        let descCheck = false;
        let titoloCheck = false;

        function checkData(){
            let dataInput = document.forms["formFilm"]["datainizio"];
            let data = new Date(dataInput.value);

            switch (data.getDay()){
                case 0 :
                    data.setDate(data.getDate() + 1);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
                case 2 :
                    data.setDate(data.getDate() + 6);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
                case 3 :
                    data.setDate(data.getDate() + 5);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
                case 4 :
                    data.setDate(data.getDate() + 4);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
                case 5 :
                    data.setDate(data.getDate() + 3);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
                case 6 :
                    data.setDate(data.getDate() + 2);
                    dataInput.value = data.toISOString().slice(0, 10);
                    break;
            }

            verificaDisponibilita(dataInput.value);
        }

        function verificaDisponibilita(dataString) {

            $.post("CheckProiezioniServlet",
                {
                    "data": dataString
                },
                function (data) {
                    dataCheck = true;
                    if(data == dataString) {
                        alert("Ci sono sale disponibili nella data che hai scelto, prosegui nell'inserimento del film!");
                    }
                    else {
                        alert("Non ci sono sale disponibili nella data selezionata, la prima disponibilità è in data: " + data.substring(8,10)+"-"+data.substring(5,7)+"-"+data.substring(0,4))
                        $("#datainizio").val(data)
                    }
                }
                );
        }


        function checkRegista(){
            let registaVal = document.forms["formFilm"]["regista"].value;

            const pattern = /^[A-Za-z]+\s[A-Za-z]+$/;

            if (!pattern.test(registaVal) && registaVal != "") {
                document.getElementById("regista").style.border = "2px solid red";
                document.getElementById("registaError").style.display = "block";
                registaCheck = false;
            }
            else {
                document.getElementById("regista").style.border = null;
                document.getElementById("registaError").style.display = "none";
                registaCheck = true;
            }

        }

        function checkAttori(){
            let attoriVal = document.forms["formFilm"]["attori"].value;

            const pattern = /^[A-Za-z]+\s[A-Za-z]+(,[A-Za-z]+\s[A-Za-z]+)*$/;

            if (!pattern.test(attoriVal) && attoriVal != "") {
                document.getElementById("attori").style.border = "2px solid red";
                document.getElementById("attoriError").style.display = "block";
                attoriCheck = false;
            }
            else {
                document.getElementById("attori").style.border = null;
                document.getElementById("attoriError").style.display = "none";
                attoriCheck = true;
            }

        }

        function checkDesc(){
            let descval = document.forms["formFilm"]["descrizione"].value;

            if (descval.length>255) {
                document.getElementById("descrizione").style.border = "2px solid red";
                document.getElementById("descError").style.display = "block";
                descCheck = false;
            }
            else {
                document.getElementById("descrizione").style.border = null;
                document.getElementById("descError").style.display = "none";
                descCheck = true;
            }

        }

        function checkTitolo(){
            let titoloval = document.forms["formFilm"]["nome"].value;

            if (titoloval.length>255) {
                document.getElementById("nome").style.border = "2px solid red";
                document.getElementById("nomeError").style.display = "block";
                titoloCheck = false;
            }
            else {
                document.getElementById("nome").style.border = null;
                document.getElementById("nomeError").style.display = "none";
                titoloCheck = true;
            }

        }

        function validateFormFilm(){

            let titolo = $("#nome").val().trim();
            let durata = $("#durata").val();
            let datainizio = $("#datainizio").val();
            let prezzo = $("#prezzo").val();
            let copertina = $("#copertina").val();
            let regista = $("#regista").val();
            let attori = $("#attori").val();
            let descrizione = $("#descrizione").val().trim();


            if(titoloCheck == false||descCheck == false ||registaCheck == false || attoriCheck == false || dataCheck == false || titolo == "" || durata == "" || datainizio == "" || prezzo == "" || descrizione == "" || copertina == "" || regista == "" || attori == "" || descrizione == "")
                return false;

            return true
        }

    </script>

</body>
</html>
