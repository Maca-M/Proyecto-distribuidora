
package Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class ClienteDao {
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    
    public boolean registrar(Clientes cl){
       String sql = "INSERT INTO clientes (nombre, apellido, telefono, direccion) VALUES (?, ?, ?, ?)";
       try {
           con= cn.getConexion();
           ps = con.prepareStatement(sql);
           ps.setString(1, cl.getNombre());
           ps.setString(2, cl.getApellido());
           ps.setString(3, cl.getTelefono());
           ps.setString(4, cl.getDireccion());           
           ps.execute();
           return true;
       
       } catch (SQLException e){
           
           JOptionPane.showMessageDialog(null, e.toString());
           return false;
       }
    }
    public List ListaClientes(String valor){
        List<Clientes> listaClientes = new ArrayList();
        String sql = "SELECT * FROM clientes ORDER BY estado ASC";
        String buscar = "SELECT * FROM clientes WHERE nombre LIKE '%"+valor+"%' OR telefono LIKE '%"+valor+"%'";
        try{
            con = cn.getConexion();
            if(valor.equalsIgnoreCase("")){
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            }else{
                ps = con.prepareStatement(buscar);
                rs = ps.executeQuery();
            }
            while(rs.next()){
                Clientes cl = new Clientes();
                cl.setId(rs.getInt("id"));
                cl.setNombre(rs.getString("nombre"));
                cl.setApellido(rs.getString("apellido"));
                cl.setTelefono(rs.getString("telefono"));
                cl.setDireccion(rs.getString("direccion"));
                cl.setEstado(rs.getString("estado"));
                listaClientes.add(cl);
            }
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());          
        }return listaClientes;
        
    }
    
    public boolean modificar(Clientes cl){
        String sql = "UPDATE clientes SET nombre = ?, apellido = ?, telefono = ?, direccion = ? WHERE id = ? ";
        try {
            con = cn.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, cl.getNombre());
            ps.setString(2, cl.getApellido());
            ps.setString(3, cl.getTelefono());
            ps.setString(4, cl.getDireccion());
            ps.setInt(5, cl.getId());
            ps.execute();
            return true;
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
    }
    
    public boolean accion(String estado, int id){
        String sql = "UPDATE clientes SET estado = ? WHERE id = ?";
        try{
            con = cn.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch(SQLException e){
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
            
        }
    }

