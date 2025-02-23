package mx.edu.utez.security.SERVLETS;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener la sesión actual
        HttpSession session = request.getSession(false); // false para no crear una nueva si no existe

        if (session != null) {
            // Invalidar la sesión
            session.invalidate();
        }

        // Redirigir al usuario a la página de inicio de sesión o página pública
        response.sendRedirect("index.jsp");
    }
}
