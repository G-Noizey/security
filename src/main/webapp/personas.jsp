<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="javax.servlet.http.HttpSession" %>

<%
    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("usuario") == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<html>
<head>
    <title>Gestión de Personas</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- SweetAlert2 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
</head>
<body>
<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1>Gestión de Personas</h1>
        <a href="logout" class="btn btn-danger">Cerrar Sesión</a>
    </div>

    <div class="row">
        <!-- Formulario a la izquierda -->
        <div class="col-md-4">
            <h2>Formulario</h2>
            <form id="personaForm">
                <input type="hidden" id="idPersona">
                <div class="mb-3">
                    <label>Nombre</label>
                    <input type="text" class="form-control" id="nombre" placeholder="Ingresa el nombre" required>
                </div>
                <div class="mb-3">
                    <label>Correo</label>
                    <input type="email" class="form-control" id="correo" placeholder="Ingresa el correo electrónico" required>
                </div>
                <div class="mb-3">
                    <label>Teléfono</label>
                    <input type="text" class="form-control" id="telefono" placeholder="Ingresa el número de teléfono" required>
                </div>
                <div class="mb-3">
                    <label>Edad</label>
                    <input type="number" class="form-control" id="edad" placeholder="Ingresa la edad" required>
                </div>
                <button type="submit" class="btn btn-primary">Guardar</button>
            </form>
        </div>

        <!-- Tabla de datos a la derecha -->
        <div class="col-md-8">
            <h2>Lista de Personas</h2>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Teléfono</th>
                    <th>Edad</th>
                    <th>Acciones</th>
                </tr>
                </thead>
                <tbody id="tablaPersonas">
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        cargarPersonas();

        document.getElementById("personaForm").addEventListener("submit", (event) => {
            event.preventDefault();
            guardarPersona();
        });

        // Previene el retroceso y la manipulación del historial
        window.history.pushState(null, null, window.location.href); // Cambia la URL sin recargar
        window.onpopstate = function () {
            window.history.pushState(null, null, window.location.href); // Previene la acción de retroceder
        };
    });

    function cargarPersonas() {
        let xhr = new XMLHttpRequest();
        xhr.open("GET", "personas?accion=findAll", true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                let personas = JSON.parse(xhr.responseText);
                let tabla = document.getElementById("tablaPersonas");
                tabla.innerHTML = ""; // Limpiar la tabla antes de agregar nuevos datos

                personas.forEach(p => {
                    let fila = document.createElement("tr");

                    let celdaId = document.createElement("td");
                    celdaId.textContent = p.id;
                    fila.appendChild(celdaId);

                    let celdaNombre = document.createElement("td");
                    celdaNombre.textContent = p.nombre;
                    fila.appendChild(celdaNombre);

                    let celdaCorreo = document.createElement("td");
                    celdaCorreo.textContent = p.correo;
                    fila.appendChild(celdaCorreo);

                    let celdaTelefono = document.createElement("td");
                    celdaTelefono.textContent = p.telefono;
                    fila.appendChild(celdaTelefono);

                    let celdaEdad = document.createElement("td");
                    celdaEdad.textContent = p.edad;
                    fila.appendChild(celdaEdad);

                    let celdaAcciones = document.createElement("td");

                    let botonEditar = document.createElement("button");
                    botonEditar.textContent = "Editar";
                    botonEditar.classList.add("btn", "btn-warning");
                    botonEditar.onclick = () => editarPersona(p.id);
                    celdaAcciones.appendChild(botonEditar);

                    let botonEliminar = document.createElement("button");
                    botonEliminar.textContent = "Eliminar";
                    botonEliminar.classList.add("btn", "btn-danger");
                    botonEliminar.onclick = () => eliminarPersona(p.id);
                    celdaAcciones.appendChild(botonEliminar);

                    fila.appendChild(celdaAcciones);

                    tabla.appendChild(fila);
                });
            } else {
                Swal.fire("Error", "Error al cargar las personas.", "error");
            }
        };
        xhr.onerror = function () {
            Swal.fire("Error", "Error de conexión.", "error");
        };
        xhr.send();
    }

    function editarPersona(id) {
        let xhr = new XMLHttpRequest();
        xhr.open("GET", `personas/${id}`, true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                let persona = JSON.parse(xhr.responseText);

                // Rellenar el formulario con los datos de la persona
                document.getElementById("idPersona").value = persona.id;
                document.getElementById("nombre").value = persona.nombre;
                document.getElementById("correo").value = persona.correo;
                document.getElementById("telefono").value = persona.telefono;
                document.getElementById("edad").value = persona.edad;
            } else {
                Swal.fire("Error", "Error al obtener los datos de la persona.", "error");
            }
        };
        xhr.onerror = function () {
            Swal.fire("Error", "Error de conexión.", "error");
        };
        xhr.send();
    }

    function guardarPersona() {
        let id = document.getElementById("idPersona").value;
        let nombre = document.getElementById("nombre").value;
        let correo = document.getElementById("correo").value;
        let telefono = document.getElementById("telefono").value;
        let edad = document.getElementById("edad").value;

        // Validar que todos los campos estén llenos
        if (!nombre || !correo || !telefono || !edad) {
            Swal.fire("Error", "Todos los campos son obligatorios.", "error");
            return;
        }

        // Crear el objeto de datos
        let data = { nombre, correo, telefono, edad: parseInt(edad) }; // Sin el id
        console.log("Datos a enviar:", data); // Depuración

        // Enviar la solicitud POST
        let xhr = new XMLHttpRequest();
        xhr.open("POST", "personas", true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.onload = function () {
            if (xhr.status === 200) {
                let resultado = JSON.parse(xhr.responseText);
                Swal.fire("Éxito", resultado.mensaje, "success");
                document.getElementById("personaForm").reset();
                cargarPersonas();
            } else {
                Swal.fire("Error", "Error al guardar la persona.", "error");
            }
        };
        xhr.onerror = function () {
            Swal.fire("Error", "Error de conexión.", "error");
        };
        xhr.send(JSON.stringify(data));
    }

    function eliminarPersona(id) {
        console.log("Intentando eliminar persona con ID:", id); // Verifica si el ID es correcto

        Swal.fire({
            title: "¿Estás seguro?",
            text: "Esta acción no se puede deshacer",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                let url = `personas?accion=delete&id=${id}`; // Usa directamente el id recibido
                console.log("URL generada:", url); // Verifica la URL antes de enviarla

                let xhr = new XMLHttpRequest();
                xhr.open("GET", url, true);
                xhr.onload = function () {
                    if (xhr.status === 200) {
                        Swal.fire("Eliminado", "La persona ha sido eliminada", "success")
                            .then(() => location.reload());
                    } else {
                        Swal.fire("Error", "No se pudo eliminar la persona", "error");
                    }
                };
                xhr.send();
            }
        });
    }
</script>
</body>
</html>
