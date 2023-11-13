/*const http = require('http');

const ip = "localhost";
const port = 3005;
const MAIN_PATH = "http:" + ip + ":" + port + "/api/";

const server = http.createServer(function (req, res) {
  // Manejo de solicitudes HTTP aquí
});

server.listen(port, function () {
  console.log("El servidor está funcionando en http://" + ip + ":" + port);
});

const date = new Date();

let currentDate = date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();

const daysOfWeek = ["Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"];
const dayOfWeek = daysOfWeek[date.getDate()];

const currentDateFormatted = dayOfWeek + " - " + date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear();
//01-11-2023

document.addEventListener('DOMContentLoaded', function() {
    // Este código se ejecutará después de que la página se haya cargado completamente.

    const url = MAIN_PATH + "dates/" + currentDate;
    fetch(url)
      .then((res) => {
        if (!res.ok) {
          throw new Error("La solicitud no pudo completarse correctamente.");
        }
        return res.json();
      })
      .then((data) => {
        console.log("Mostrando Data:", data);
        if (data != null) {
          document.getElementById("showDate").innerHTML = `
            <p class="parrafo">Hoy es${currentDateFormatted}</p>´
            <p class="parrafo">${data.NAMECAL}</p>
            <p class="parrafo">${data.DESCRIPCAL}</p>
            <br> 
          `;
        } else {
          document.getElementById(
            "showDate"
            ).innerHTML = `<p class="parrafo">Hoy es ${currentDateFormatted}</p>`;
        }
      })
      .catch((error) => {
        console.error("Error en la solicitud fetch:", error);
        // Maneja el error, por ejemplo, muestra un mensaje de error al usuario.
      });
});*/