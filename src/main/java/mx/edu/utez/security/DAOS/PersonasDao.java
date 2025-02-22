package mx.edu.utez.security.DAOS;

import mx.edu.utez.security.UTILS.ConexionDB;
import mx.edu.utez.security.MODELO.Persona;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PersonasDao {
    private Connection conexion;

    public PersonasDao() {
        this.conexion = ConexionDB.getConexion(); // Obtener la conexión desde ConexionDB
    }

    public void agregarPersona(Persona persona, String usuario) {
        String sql = "INSERT INTO personas (nombre, correo, telefono, edad) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getCorreo());
            stmt.setString(3, persona.getTelefono());
            stmt.setInt(4, persona.getEdad());
            stmt.executeUpdate();

            // Registrar en la bitácora
            registrarBitacora(usuario, "CREATE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Persona> obtenerPersonas(String usuario) {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM personas";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Persona persona = new Persona();
                persona.setId(rs.getInt("id"));
                persona.setNombre(rs.getString("nombre"));
                persona.setCorreo(rs.getString("correo"));
                persona.setTelefono(rs.getString("telefono"));
                persona.setEdad(rs.getInt("edad"));
                personas.add(persona);
            }

            // Registrar en la bitácora
            registrarBitacora(usuario, "READ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return personas;
    }

    public void actualizarPersona(Persona persona, String usuario) {
        String sql = "UPDATE personas SET nombre = ?, correo = ?, telefono = ?, edad = ? WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getCorreo());
            stmt.setString(3, persona.getTelefono());
            stmt.setInt(4, persona.getEdad());
            stmt.setInt(5, persona.getId());
            stmt.executeUpdate();

            // Registrar en la bitácora
            registrarBitacora(usuario, "UPDATE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminarPersona(int id, String usuario) {
        String sql = "DELETE FROM personas WHERE id = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

            // Registrar en la bitácora
            registrarBitacora(usuario, "DELETE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarBitacora(String usuario, String operacion) {
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