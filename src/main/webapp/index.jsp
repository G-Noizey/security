<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- SweetAlert2 CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header">
                    <h3 class="text-center">Iniciar Sesión</h3>
                </div>
                <div class="card-body">
                    <form id="loginForm">
                        <div class="mb-3">
                            <label for="usuario" class="form-label">Usuario</label>
                            <input type="text" class="form-control" id="usuario" name="usuario" required>
                        </div>
                        <div class="mb-3">
                            <label for="contrasena" class="form-label">Contraseña</label>
                            <input type="password" class="form-control" id="contrasena" name="contrasena" required>
                        </div>
                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary">Iniciar Sesión</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS y dependencias -->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>
<!-- jQuery para manejar AJAX -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- SweetAlert2 JS -->
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<script>
    $(document).ready(function() {
        $('#loginForm').on('submit', function(event) {
            event.preventDefault(); // Evitar el envío tradicional del formulario

            var usuario = $('#usuario').val();
            var contrasena = $('#contrasena').val();

            $.ajax({
                url: 'login', // URL del LoginServlet
                type: 'POST',
                data: {
                    usuario: usuario,
                    contrasena: contrasena
                },
                success: function(response) {
                    var data = JSON.parse(response);
                    if (data.status === 'success') {
                        // Mostrar alerta de éxito con SweetAlert2
                        Swal.fire({
                            icon: 'success',
                            title: 'Éxito',
                            text: data.message,
                            showConfirmButton: false,
                            timer: 1500 // Cierra automáticamente después de 1.5 segundos
                        }).then(() => {
                            // Redirigir a la página de personas.jsp después de la alerta
                            window.location.href = 'personas.jsp';
                        });
                    } else {
                        // Mostrar alerta de error con SweetAlert2
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: data.message
                        });
                    }
                },
                error: function() {
                    // Mostrar alerta de error en la conexión con SweetAlert2
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error en la conexión con el servidor.'
                    });
                }
            });
        });
    });
</script>
</body>
</html>