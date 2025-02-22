package mx.edu.utez.security.DAOS;

import mx.edu.utez.security.UTILS.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDao {
    private Connection conexion;

    public LoginDao() {
        this.conexion = ConexionDB.getConexion(); // Obtener la conexión desde ConexionDB
    }

    public boolean validarUsuario(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, contrasena);
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Retorna true si encuentra un usuario con esas credenciales
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void registrarBitacora(String usuario, String operacion) {
        String sql = "INSERT INTO bitacora (usuario, operacion) VALUES (?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, operacion);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}