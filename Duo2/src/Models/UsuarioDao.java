
package Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class UsuarioDao {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    public Usuarios login(String usuario, String clave){
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND clave = ?";
        Usuarios us = new Usuarios();
        try{
            con = cn.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, clave);
            rs = ps.executeQuery();
            if (rs.next()){
                us.setId(rs.getInt("id"));
                us.setUsuario(rs.getString("usuario"));
                us.setNombre(rs.getString("nombre"));
                us.setCaja(rs.getString("caja"));
                us.setRol(rs.getString("rol"));
                us.setEstado(rs.getString("estado"));
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return us;
    }
    
    public boolean registrar(Usuarios us){
       String sql = "INSERT INTO usuarios (usuario, nombre, clave, caja, rol) VALUES (?, ?, ?, ?, ?)";
       try {
           con= cn.getConexion();
           ps = con.prepareStatement(sql);
           ps.setString(1, us.getUsuario());
           ps.setString(2, us.getNombre());
           ps.setString(3, us.getClave());
           ps.setString(4, us.getCaja());
           ps.setString(5, us.getRol());
           ps.execute();
           return true;
       
       } catch (SQLException e){
           
           JOptionPane.showMessageDialog(null, e.toString());
           return false;
       }
    } 
    
}
