const url = "http://localhost:3001/api/formulario"  //creamos la url con host y el puerto

const btnCrear = document.getElementById("btnCrear")  // creamos una const btnCrear que hara que se inserten los datos al dale enviar 

const form = document.querySelector("form")

const tbody = document.querySelector("tbody#data")
//agregamos form con los datos que tenemos en las tablas de la bd y getelement traera los datos del html por el ID
const nombresform = document.getElementById("nombres")
const apellidosform = document.getElementById("apellidos")
const tipo_documentoform = document.getElementById("tipodocumento")
const numero_documentoform = document.getElementById("numdocumento")
const emailform = document.getElementById("email")
const celularform = document.getElementById("celular")
const asuntoform = document.getElementById("asunto")
const mensajeform = document.getElementById("mensaje")

var opcion = ''; // insertar datos 

btnCrear.addEventListener('click', () => {
	console.log("Acción de listar activada");
	opcion = 'crear';
});

form.addEventListener('submit',
	(e) => {
		e.preventDefault();
		if (opcion == 'crear') {
			if (nombresform.value == "" || apellidosform.value == "" || tipo_documentoform.value == "" || numero_documentoform.value == "" || emailform.value == "" || celularform.value == "" || asuntoform.value == "" || mensajeform.value == "") {
				alert("Asegúrese de que todos los campos estén completos");
				return false;
			} else {
				insertData({
					nombresform: nombresform.value,
					apellidosform: apellidosform.value,
					tipo_documentoform: tipo_documentoform.value,
					numero_documentoform: numero_documentoform.value,
					emailform: emailform.value,
					celularform: celularform.value,
					asuntoform: asuntoform.value,
					mensajeform: mensajeform.value,
				});
				console.log(nombresform.value)
			}
		} else if (opcion == 'editar') {
			console.log("Activado el ");
		}
	}
);

function insertData(data) {
	fetch(url,
		{
			method: 'POST',
			headers: {
				'content-Type': 'application/json'
			},
			body: JSON.stringify(data)
		}
	)
		.then(
			response => response.json()
		)
		.then(
			response => location.reload()
		);
}

//
tbody.innerHTML = ""
//creamos la funcion para que recoge el contenido y la app json
function getData() {
	fetch(url, { method: 'GET', headers: { 'Content-Type': 'application/json' } })
		.then(res => res.json())
		.then(data => {
			buildTable(data)
		})
}
//creamos una funcion y llamamos a getData
function init() {
	getData();
}
//creamos la funcion para que al completar el formulario se muestren quienés lo han completado, tambien se pueden omitir datos para que no se muestren 
function buildTable(data) {
	if (data.length > 0) {
		data.forEach((data) => {
			tbody.innerHTML += `
			<tr>
				<td>${data['nombres']}</td>
				<td>${data['apellidos']}</td>
				<td>${data['tipo_documento']}</td>
				<td>${data['numero_documento']}</td>
				<td>${data['email']}</td>
				<td>${data['celular']}</td>
				<td>${data['asunto']}</td>
				<td>${data['mensaje']}</td>
				
			</tr>`;
		})
	}
}

init()