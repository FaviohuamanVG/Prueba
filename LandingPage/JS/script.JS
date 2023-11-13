/*//Funcionalidad 1
//Alerta de Bienvenida
alert("Bienvenidos a la pagina web José Buenaventura Sepúlveda")

//Funcionalidad 4
// Obtener el nombre del usuario mediante un cuadro de diálogo prompt
var Nombre = prompt("Ingrese un nombre: ");

// Verificar si se ingresó un nombre y actualizar el cuadro de usuario si es así
if (Nombre !== null && Nombre !== "") {
    // Seleccionar el elemento <p> por su id
    var name = document.getElementById("user");

    // Actualizar el contenido del elemento <p> con el nombre
    name.textContent = "Nombre: "+ Nombre;
}

//Funcionalidad 5
// Obtén la fecha actual
var fechaActual = new Date();

// Obtiene el día de la semana como un número (0 = Domingo, 1 = Lunes, ..., 6 = Sábado)
var numeroDia = fechaActual.getDay();

// Crea un arreglo con los nombres de los días de la semana
var diasSemana = ["Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"];

// Obtiene el nombre del día actual utilizando el número obtenido
var nombreDia = diasSemana[numeroDia];

// Muestra una alerta con el día actual
alert("Hola "+Nombre+ " hoy es " + nombreDia);

//Funcionalidad 6
function moverTitulo() {
    let pageTitle = document.title; // Obten el título actual de la página
    let position = 0;
    const speed = 1; // Ajusta la velocidad de movimiento aquí (en píxeles por iteración).

    setInterval(function () {
        position += speed;
        document.title = pageTitle.substring(position % pageTitle.length) + pageTitle.substring(0, position % pageTitle.length);

    }, 100); // Actualiza la posición cada 100 ms.
}

// Llama a la función para iniciar el movimiento del título.
moverTitulo();

//Funcionalidad 6
//Mostrar un mensaje de saludo a partir de la hora vigente
//Utilizamos el metodo getHours para obtener la hora actual
var horaActual = fechaActual.getHours();

if (horaActual >= 6 && horaActual <12) {

    alert
alert("Buenos dias, " + Nombre + "!");
} else if (horaActual >= 12 && horaActual < 18) {
    alert("Buenas tardes, " + Nombre + "!");
} else {
    alert("Buenas noches, " + Nombre + "!")
}

//Funcionalidad 7
// Agregar un evento al botón "Cerrar sesión" que muestra un mensaje de despedida
// document.getElementById("cerrarSesion") para obtener el boton "Cerrar Sesion"
var botonCerrarSesion = document.getElementById("Cerrar Sesion");

botonCerrarSesion.addEventListener("click", function() {
    alert("¡Hasta luego, " + Nombre + "!");
});*/

//Boton Hamburgesa
$(document).ready(function() {
    var menuButton = $(".menu-btn");
    var navMenu = $(".nav-main .nav-menu");
    
    menuButton.click(function() {
        navMenu.toggleClass("active");
    });
});