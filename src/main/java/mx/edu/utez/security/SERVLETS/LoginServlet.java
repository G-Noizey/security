package mx.edu.utez.security.SERVLETS;

import mx.edu.utez.security.DAOS.LoginDao;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        LoginDao loginDao = new LoginDao();
        if (loginDao.validarUsuario(usuario, contrasena)) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            loginDao.registrarBitacora(usuario, "LOGIN"); // Registrar en la bitácora
            response.getWriter().write("{\"status\": \"success\", \"message\": \"Login exitoso\"}");
        } else {
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Credenciales incorrectas\"}");
        }
    }
}