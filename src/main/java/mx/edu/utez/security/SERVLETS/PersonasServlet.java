package mx.edu.utez.security.SERVLETS;

import mx.edu.utez.security.DAOS.PersonasDao;
import mx.edu.utez.security.MODELO.Persona;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/personas")
public class PersonasServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Acceso no autorizado");
            return;
        }

        try {
            // Obtener el parámetro "accion" para determinar la operación
            String accion = request.getParameter("accion");
            String usuario = (String) session.getAttribute("usuario");
            PersonasDao personasDao = new PersonasDao();

            switch (accion) {
                case "findAll":
                    // Obtener la lista de personas
                    List<Persona> personas = personasDao.obtenerPersonas(usuario);
                    sendJsonResponse(response, new Gson().toJson(personas));
                    break;

                case "delete":
                    // Eliminar una persona
                    String idStr = request.getParameter("id");
                    if (idStr == null || idStr.isEmpty()) {
                        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ID es obligatorio");
                        return;
                    }
                    int id = Integer.parseInt(idStr);
                    personasDao.eliminarPersona(id, usuario);
                    sendJsonResponse(response, "{\"status\": \"success\", \"message\": \"Persona eliminada\"}");
                    break;

                default:
                    // Si no se proporciona una acción válida
                    sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Acción no válida");
                    break;
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ID debe ser un número válido");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();

            // Convertir el cuerpo a un objeto JSON
            Gson gson = new Gson();
            Persona persona = gson.fromJson(requestBody, Persona.class);

            // Validar que los campos no estén vacíos
            if (persona.getNombre() == null || persona.getCorreo() == null || persona.getTelefono() == null || persona.getEdad() <= 0) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Todos los campos son obligatorios");
                return;
            }

            // Guardar la persona en la base de datos
            PersonasDao personasDao = new PersonasDao();
            personasDao.agregarPersona(persona, (String) request.getSession().getAttribute("usuario"));

            // Enviar respuesta de éxito
            sendJsonResponse(response, "{\"status\": \"success\", \"message\": \"Persona agregada\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Acceso no autorizado");
            return;
        }

        try {
            String usuario = (String) session.getAttribute("usuario");
            String idStr = request.getParameter("id");
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("correo");
            String telefono = request.getParameter("telefono");
            String edadStr = request.getParameter("edad");

            if (idStr == null || nombre == null || correo == null || telefono == null || edadStr == null) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Todos los campos son obligatorios");
                return;
            }

            int id = Integer.parseInt(idStr);
            int edad = Integer.parseInt(edadStr);

            Persona persona = new Persona();
            persona.setId(id);
            persona.setNombre(nombre);
            persona.setCorreo(correo);
            persona.setTelefono(telefono);
            persona.setEdad(edad);

            PersonasDao personasDao = new PersonasDao();
            personasDao.actualizarPersona(persona, usuario);
            sendJsonResponse(response, "{\"status\": \"success\", \"message\": \"Persona actualizada\"}");
        } catch (NumberFormatException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ID y edad deben ser números válidos");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al actualizar la persona");
        }
    }


    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        sendJsonResponse(response, "{\"status\": \"error\", \"message\": \"" + message + "\"}");
    }
}
