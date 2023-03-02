
package Controllers;

import Models.ClienteDao;
import Models.Clientes;
import Models.Combo;
import Models.Tables;
import Views.PanelAdmin;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import static javax.swing.UIManager.get;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;


public class ClientesControllers implements ActionListener, MouseListener, KeyListener{
    private Clientes cl;
    private ClienteDao clDao;
    private PanelAdmin views;
    
    DefaultTableModel modelo = new DefaultTableModel();
    
    

    public ClientesControllers(Clientes cl, ClienteDao clDao, PanelAdmin views) {
        this.cl = cl;
        this.clDao = clDao;
        this.views = views;
        this.views.btnNuevoCliente.addActionListener(this);
        this.views.btnEditarCliente.addActionListener(this);
        this.views.btnLimpiarCliente.addActionListener(this);
        this.views.jTableCliente.addMouseListener(this);
        this.views.jMenuItemEliminarCliente.addActionListener(this);
        this.views.jMenuItemReingresarCliente.addActionListener(this);
        this.views.txtBuscarCliente.addKeyListener(this);
        this.views.jLabelClientes.addMouseListener(this);
        listarClientes();
        llenarClientes();
        AutoCompleteDecorator.decorate(views.seleccionarCliente);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == views.btnNuevoCliente){
            
            if(views.txtNomCliente.getText().equals("")
                    || views.txtApCliente.getText().equals("")
                    || views.txtDirCliente.getText().equals("")
                    || views.txtTelCliente.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            }else{
                cl.setNombre(views.txtNomCliente.getText());
                cl.setApellido(views.txtApCliente.getText());
                cl.setDireccion(views.txtDirCliente.getText());
                cl.setTelefono(views.txtTelCliente.getText());
                if(clDao.registrar(cl)){
                    limpiarTable();
                    listarClientes();
                    llenarClientes();
                    limpiar();
                    JOptionPane.showMessageDialog(null, "Cliente registrado");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al registrar");
                }
            }
        }else if (e.getSource() == views.btnEditarCliente){
            if(views.txtIdCliente.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Seleccione una fila");
            }else{
                if(views.txtNomCliente.getText().equals("")
                    || views.txtApCliente.getText().equals("")
                    || views.txtDirCliente.getText().equals("")
                    || views.txtTelCliente.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
            }else{
                cl.setNombre(views.txtNomCliente.getText());
                cl.setApellido(views.txtApCliente.getText());
                cl.setDireccion(views.txtDirCliente.getText());
                cl.setTelefono(views.txtTelCliente.getText());
                cl.setId(Integer.parseInt(views.txtIdCliente.getText()));
                    if(clDao.modificar(cl)){
                        limpiarTable();
                        listarClientes();
                        llenarClientes();
                        limpiar();
                        JOptionPane.showMessageDialog(null, "Cliente modificado");
                    }else{
                        JOptionPane.showMessageDialog(null, "Error al editar");
                    }
                }
            }
        }else if (e.getSource() == views.btnLimpiarCliente){
                 limpiar();
        }else if(e.getSource() == views.jMenuItemReingresarCliente){
            if(views.txtIdCliente.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
            }else{
                int id = Integer.parseInt(views.txtIdCliente.getText());
                if(clDao.accion("Activo", id)){
                    limpiarTable();
                    listarClientes();
                    llenarClientes();
                    limpiar();
                    JOptionPane.showMessageDialog(null, "Cliente reingresado");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al reingresar cliente");
                }
            }
        }else if(e.getSource() == views.jMenuItemEliminarCliente){
            if(views.txtIdCliente.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
            }else{
                int id = Integer.parseInt(views.txtIdCliente.getText());
                if(clDao.accion("Inactivo", id)){
                    limpiarTable();
                    listarClientes();
                    llenarClientes();
                    limpiar();
                    JOptionPane.showMessageDialog(null, "Cliente eliminado");
                }else{
                    JOptionPane.showMessageDialog(null, "Error al eliminar cliente");
                }
            }
        }else{
            limpiar();
        }
    }
    
    public void listarClientes(){
        
        List<Clientes> lista = clDao.ListaClientes(views.txtBuscarCliente.getText());
        modelo = (DefaultTableModel) views.jTableCliente.getModel();
        Object[] ob = new Object [6];
        for (int i = 0; i< lista.size(); i++){
            ob[0] = lista.get(i).getId();
            ob[1] = lista.get(i).getNombre();
            ob[2] = lista.get(i).getApellido();
            ob[3] = lista.get(i).getTelefono();
            ob[4] = lista.get(i).getDireccion();
            ob[5] = lista.get(i).getEstado();
            modelo.addRow(ob);
        }
        views.jTableCliente.setModel(modelo);
        
    }
    
    public void limpiarTable(){
        for (int i = 0; i < modelo.getRowCount(); i++){
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource() == views.jTableCliente){
            int fila = views.jTableCliente.rowAtPoint(e.getPoint());
            views.txtIdCliente.setText(views.jTableCliente.getValueAt(fila, 0).toString());
            views.txtNomCliente.setText(views.jTableCliente.getValueAt(fila, 1).toString());
            views.txtApCliente.setText(views.jTableCliente.getValueAt(fila, 2).toString());
            views.txtTelCliente.setText(views.jTableCliente.getValueAt(fila, 3).toString());
            views.txtDirCliente.setText(views.jTableCliente.getValueAt(fila, 4).toString());
            
        }else if(e.getSource() == views.jLabelClientes){
            views.jTabbedPane1.setSelectedIndex(1);
            limpiarTable();
            listarClientes();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    private void limpiar(){
        views.txtNomCliente.setText("");
        views.txtApCliente.setText("");
        views.txtDirCliente.setText("");
        views.txtTelCliente.setText("");
        views.txtIdCliente.setText("");
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getSource() == views.txtBuscarCliente){
            limpiarTable();
            listarClientes();
            
            
        }
   }

    private void llenarClientes() {
        List<Clientes> lista = clDao.ListaClientes(views.txtBuscarCliente.getText());
        views.seleccionarCliente.removeAllItems() ;
        for (int i = 0; i < lista.size(); i++){
            int id = lista.get(i).getId();
            String nombre = lista.get(i).getNombre();
            views.seleccionarCliente.addItem(new Combo(id, nombre));
        }
    }
}
        
