const { error } = require ('console');
const http = require('http');
const express = require('express');
const app = express();
const path = require('path');
const mysql = require('mysql');
const port = 3005;
const ip = 'localhost';
const cors = require('cors');

const server = http.createServer(app);

server.listen(port, function(){
    console.log("El servidor esta funcionando en http://"+ ip + ":" + port);
});

//Enrutamiento
app.use(express.static ('DEMO'));

app.use('/LandingPage', express.static('LandingPage'));
app.use('/LandingPage/CSS', express.static('LandingPage/CSS'));
app.use('/LandingPage/img', express.static('LandingPage/img'));
app.use('/LandingPage/JS', express.static('LandingPage/JS'));
app.use('/LandingPage/HTML', express.static('LandingPage/HTML'));

//Conexion BD
const conexion = mysql.createConnection ({
    host: "localhost",
    user: "root",
    password: "",
    database: "calendar"
});

conexion.connect(function(err) {
    if (err) {
      console.error('Error al conectar a la base de datos: ' + err.message);
      throw err;
    } else {
      console.log('Conectado a la base de datos');
    }
  });

app.use(express.json());
app.use(cors());
app.use(express.urlencoded({extended: false}));

//Rutas
app.get("/", function(req, res){
    var filePath = path.join(__dirname, "../index.html");
    res.sendFile(filePath);
});

app.get("/LandingPage/HTML/quienessomos.html", function(req, res) {
    var filePath = path.join(__dirname, "../HTML/quienessomos.html");
    res.sendFile(filePath);
  });
  
  app.get("/LandingPage/HTML/TalleresFormativos.html", function(req, res) {
    var filePath = path.join(__dirname, "../HTML/TalleresFormativos.html");
    res.sendFile(filePath);
  });
  
  app.get("/", function(req, res) {
    var filePath = path.join(__dirname, "../Contáctenos.html");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/CSS", function(req, res) {
    var filePath = path.join(__dirname, "/LandingPage/CSS");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/CSS/quienessomos.css", function(req, res) {
    var filePath = path.join(__dirname, "../CSS/quienessomos.css");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/CSS/style.css", function(req, res) {
    var filePath = path.join(__dirname, "../CSS/style.css");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/CSS/TalleresFormativos.css", function(req, res) {
    var filePath = path.join(__dirname, "../CSS/TalleresFormativos.css");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/img", function(req, res) {
    var filePath = path.join(__dirname, "../img");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/JS/calendar.js", function(req, res) {
    var filePath = path.join(__dirname, "../JS/calendar.js");
    res.sendFile(filePath);
  });

  app.get("/LandingPage/JS/dates.js", function(req, res) {
    var filePath = path.join(__dirname, "../JS/dates.js");
    res.sendFile(filePath);
  });


//BD CONEXION
app.get("/api/dates/:current", (req, res) => {
    var request = req.params.current;
    conexion.query("SELECT NAMECAL, DESCAL, date_format(DATECAL, '%d/%m/%Y') AS DATECAL, DESCRIPCAL FROM FECHAS_CIVICAS WHERE DATECAL = ?", [request], function(err, row, fields) {
      if (err) {
        console.error('Error en la consulta: ' + err.message);
        res.status(500).json({ error: 'Error en la consulta' });
      } else if (row[0] != null) {
        res.json(row[0]);
      } else {
        res.json(null);
      }
    })
  });

