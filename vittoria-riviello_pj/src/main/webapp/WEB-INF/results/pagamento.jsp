<%@ page import="model.Utente" %>
<%@ page contentType="text/html;charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Pagamento con Mastercard</title>
  <link rel="stylesheet" type="text/css" href="./css/styles.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
</head>
<body>
<%@ include file="../../header.jsp"%>

<% Utente u = (Utente) session.getAttribute("utenteLoggato");
  if(u!= null){ %>
<h1 id="titolo">Pagamento con Mastercard</h1>
<h3 style="text-align: center">Inserisci i dati della tua carta</h3>

<form class="payment-form" method="post" action="ConfirmPaymentServlet">
  <div class="form-group">
    <label for="card-number">Numero di carta:</label>
    <input type="text" id="card-number" name="card-number" placeholder="Numero di carta" required>
  </div>

  <div class="form-group">
    <label for="card-holder">Titolare della carta:</label>
    <input type="text" id="card-holder" name="card-holder" placeholder="Titolare della carta" required>
  </div>

  <div class="form-group">
    <label for="expiration-date">Data di scadenza:</label>
    <input type="text" id="expiration-date" name="expiration-date" placeholder="MM/AA" required>
  </div>

  <div class="form-group">
    <label for="cvv">CVV:</label>
    <input type="text" id="cvv" name="cvv" placeholder="CVV" required>
  </div>

  <div class="form-group">
    <input type="submit" value="Paga">
  </div>
</form>

<% }else {%>
<script>
  alert('Accesso negato, non sei loggato');
</script>
<%}%>

<script defer>

  $(document).ready(function() {
    $(".payment-form").on("submit", function(event) {
      var isValid = validaCampiPagamento();

      if (!isValid) {
         /*è utilizzato per interrompere l'azione predefinita associata
          a un evento specifico e gestire l'evento manualmente in base alle nostre esigenze*/
        event.preventDefault();
      }
    });
  });

  function validaCampiPagamento() {
    var cardNumber = $("#card-number").val();
    var cardHolder = $("#card-holder").val();
    var expirationDate = $("#expiration-date").val();
    var cvv = $("#cvv").val();

    // Verifica numero di carta
    /* / ^ indica l'inizio della stringa.
        \d indica la presenza solo di cifre
        + il carattere precedente deve essere ripetuto una o piu volte
        la funzione .test() invece chiama la regexp
        !== serve perche confronta anche il tipo se e sempre uguale
     */
    if (cardNumber.length !== 16 || !/^\d+$/.test(cardNumber)) {
      alert("Il numero di carta non è valido. Assicurati di inserire un numero di carta valido di 16 cifre.");
      return false;
    }

    // Verifica titolare carta

    //\s serve a controllare siano caratteri
    if (!/^[A-Za-z]+\s[A-Za-z]+$/.test(cardHolder)) {
      alert("Inserisci il titolare della carta.");
      return false;
    }

    // Verifica data di scadenza
    var currentDate = new Date();
    var expirationParts = expirationDate.split("/");
    var expirationMonth = parseInt(expirationParts[0]);
    var expirationYear = parseInt(expirationParts[1]);

    if (expirationParts.length != 2 || isNaN(expirationMonth) || isNaN(expirationYear)) {
      alert("Inserisci una data di scadenza valida nel formato MM/AA.");
      return false;
    }

    if (expirationMonth < 1 || expirationMonth > 12) {
      alert("Il mese di scadenza non è valido.");
      return false;
    }

    var currentYear = currentDate.getFullYear() % 100; // Ottiene l'anno corrente a due cifre
    if (expirationYear < currentYear || (expirationYear == currentYear && expirationMonth < (currentDate.getMonth() + 1))) {
      alert("La carta è scaduta.");
      return false;
    }

    // Verifica CVV
    if (cvv.length != 3 || !/^\d+$/.test(cvv)) {
      alert("Il CVV non è valido. Assicurati di inserire un CVV a 3 cifre.");
      return false;
    }

    return true; // Tutti i campi sono validi
  }

</script>

</body>
</html>
