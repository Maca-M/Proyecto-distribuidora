package Views;

import Models.Clientes;
import Models.Conexion;
import Models.Productos;
import Models.ProductoDao;
import Models.Tables;
import com.sun.jdi.connect.spi.Connection;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class Print extends javax.swing.JFrame implements Printable {
    Productos pro = new Productos();
    ProductoDao proDao = new ProductoDao();
    private PanelAdmin views;
    Conexion cn = new Conexion();
    java.sql.Connection con;
    PreparedStatement ps;
    ResultSet rs;
    
    DefaultTableModel modelo = new DefaultTableModel();
    public Print(int id) {
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        Calendar fecha = new GregorianCalendar();
        int año = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH);
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        
        
        txtFecha.setText(dia + "/" + (mes+1) + "/" + año + " " + hora + ":" + minuto);
        txtFecha1.setText(dia + "/" + (mes+1) + "/" + año + " " + hora + ":" + minuto);
        
        txtNumFact.setText("" + id);
        txtNumFact1.setText("" + id);
        
       
        listar(id);
        listar1(id);
        calcularCompra(id);
        cargarcli();
        llenarfactOriginal();
        llenarfactDuplicado();
        
    }
    
    public double subtotal(double precio, int cant){
        double subtotal = 0.00;
        subtotal = precio * cant;
        return subtotal;
    }
    
    public int getDesc(int id){
        int desc = 0;
        String sql = " SELECT descuento FROM detalle_venta WHERE id = (SELECT MAX(id) from detalle_venta)";
        try{
            con = cn.getConexion();
            ps = con.prepareStatement(sql);            
            rs = ps.executeQuery();
            if(rs.next()){
                desc = rs.getInt("descuento");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return desc;
    }
    
    public void listar(int id) {
        
        Tables color = new Tables();
        jTableFact.setDefaultRenderer(jTableFact.getColumnClass(0), color);
        List<Productos> lista = proDao.ListaDetalle(id);
        modelo = (DefaultTableModel) jTableFact.getModel();
        Object[] ob = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getArticulo();
            ob[1] = lista.get(i).getNombre();
            ob[2] = lista.get(i).getDescripcion();
            ob[3] = lista.get(i).getCantidad();
            ob[4] = lista.get(i).getPrecio_venta();
            ob[5] = subtotal(lista.get(i).getPrecio_venta(),lista.get(i).getCantidad());
            modelo.addRow(ob);
        }
        
        jTableFact.setModel(modelo);
        JTableHeader header = jTableFact.getTableHeader();
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setBackground(Color.white);
    }
    
    public void listar1(int id) {
        Tables color = new Tables();
        jTableFact1.setDefaultRenderer(jTableFact1.getColumnClass(0), color);
        List<Productos> lista = proDao.ListaDetalle(id);
        modelo = (DefaultTableModel) jTableFact1.getModel();
        Object[] ob = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            ob[0] = lista.get(i).getArticulo();
            ob[1] = lista.get(i).getNombre();
            ob[2] = lista.get(i).getDescripcion();
            ob[3] = lista.get(i).getCantidad();
            ob[4] = lista.get(i).getPrecio_venta();
            ob[5] = subtotal(lista.get(i).getPrecio_venta(),lista.get(i).getCantidad());
            modelo.addRow(ob);
        }
        jTableFact1.setModel(modelo);
        JTableHeader header = jTableFact1.getTableHeader();
        header.setOpaque(false);
        header.setBackground(Color.blue);
        header.setBackground(Color.white);
    }
    
    private void calcularCompra(int id){
        double subtotal = 0.00;
        double total;
        txtDescFact.setText("" + getDesc(id));
        txtDescFact1.setText("" + getDesc(id));
        double desc = 1 - (Double.parseDouble(txtDescFact.getText()) / 100);
        
        int numFila = jTableFact.getRowCount();
        for (int i = 0; i < numFila; i++){        
            subtotal = subtotal + (double) jTableFact.getValueAt(i,5);            
        }
        
        total = subtotal * desc;
        txtTotalFact.setText(""+ total);
        txtTotalFact1.setText(""+ total);
    }
    
    private Clientes cargarcli(){
        
        String sql = "SELECT c.* FROM clientes c inner join ventas v on v.id_cliente = c.id inner join detalle_venta d on d.id_venta = v.id WHERE d.id = (SELECT MAX(id) from detalle_venta) LIMIT 1";
        Clientes cl = new Clientes();
        try {
            con = cn.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setId(rs.getInt("id"));
                cl.setNombre(rs.getString("nombre"));
                cl.setApellido(rs.getString("apellido"));
                cl.setTelefono(rs.getString("telefono"));
                cl.setDireccion(rs.getString("direccion"));

            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return cl;
    }
    
    private void llenarfactOriginal(){
        txtNroCli.setText(""+cargarcli().getId());
        txtNomCli.setText(""+cargarcli().getNombre() +" "+cargarcli().getApellido());
        txtTelCli.setText(""+cargarcli().getTelefono());
        txtDirCli.setText("" +cargarcli().getDireccion());
        
    }
    private void llenarfactDuplicado(){
        txtNroCli1.setText(""+cargarcli().getId());
        txtNomCli1.setText(""+cargarcli().getNombre() +" "+cargarcli().getApellido());
        txtTelCli1.setText(""+cargarcli().getTelefono());
        txtDirCli1.setText("" +cargarcli().getDireccion());
        
    }
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        PanelPrint = new javax.swing.JButton();
        Print = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableFact = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtNomCli = new javax.swing.JLabel();
        txtNroCli = new javax.swing.JLabel();
        txtFecha = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtTelefCli = new javax.swing.JLabel();
        txtDireCli = new javax.swing.JLabel();
        txtDirCli = new javax.swing.JLabel();
        txtTelCli = new javax.swing.JLabel();
        txtNumFact = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        txtTotalFact = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtDescFact = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTableFact1 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtFecha1 = new javax.swing.JLabel();
        txtNroCli1 = new javax.swing.JLabel();
        txtNomCli1 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        txtNumFact1 = new javax.swing.JLabel();
        txtTelCli1 = new javax.swing.JLabel();
        txtDirCli1 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        txtTotalFact1 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtDescFact1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PanelPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/4305625 (1).png"))); // NOI18N
        PanelPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        PanelPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PanelPrintActionPerformed(evt);
            }
        });
        jPanel1.add(PanelPrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 410, 60, 66));

        Print.setBackground(new java.awt.Color(255, 255, 255));
        Print.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setText("Original");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jLabel10.setText("Total a Pagar:");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 450, -1, -1));

        jTableFact.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTableFact.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Art", "Nombre", "Descripcion", "Cant", "Precio Un.", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTableFact);
        if (jTableFact.getColumnModel().getColumnCount() > 0) {
            jTableFact.getColumnModel().getColumn(0).setMaxWidth(60);
            jTableFact.getColumnModel().getColumn(2).setMaxWidth(600);
            jTableFact.getColumnModel().getColumn(3).setMaxWidth(40);
            jTableFact.getColumnModel().getColumn(4).setMaxWidth(80);
            jTableFact.getColumnModel().getColumn(5).setMaxWidth(80);
        }

        jPanel3.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 74, 600, 370));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Fecha:");

        jLabel5.setText("N° Cliente:");

        jLabel6.setText("Nombre:");

        txtNomCli.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNomCli.setText("jLabel26");

        txtNroCli.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNroCli.setText("jLabel25");

        txtFecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtFecha.setText("jLabel21");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNroCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtNomCli, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(txtFecha))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNroCli))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtNomCli))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 200, 70));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setText("Factura N°:");

        txtTelefCli.setText("Telefono:");

        txtDireCli.setText("Direccion:");

        txtDirCli.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDirCli.setText("jLabel24");

        txtTelCli.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTelCli.setText("jLabel23");

        txtNumFact.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNumFact.setText("jLabel22");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumFact, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtTelefCli)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelCli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(txtDireCli)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDirCli, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtNumFact))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTelefCli)
                    .addComponent(txtTelCli))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDireCli)
                    .addComponent(txtDirCli))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, 200, 70));

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtTotalFact.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTotalFact.setText("jLabel11");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 93, Short.MAX_VALUE)
                .addComponent(txtTotalFact, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtTotalFact)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 190, 29));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Descuento:");

        jLabel14.setText("%");

        txtDescFact.setText("jLabel15");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescFact, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(txtDescFact))
                .addContainerGap())
        );

        jPanel3.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 440, 160, 29));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Tel. de Contacto: 351 388-6164");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 450, 200, 20));

        jLabel11.setFont(new java.awt.Font("Vladimir Script", 1, 48)); // NOI18N
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/logo_1.png"))); // NOI18N
        jPanel3.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 70));

        Print.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 480));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel33.setText("Duplicado");
        jPanel2.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, -1, -1));

        jTableFact1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jTableFact1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Art", "Nombre", "Descripcion", "Cant", "Precio Un.", "Subtotal"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTableFact1);
        if (jTableFact1.getColumnModel().getColumnCount() > 0) {
            jTableFact1.getColumnModel().getColumn(0).setMaxWidth(60);
            jTableFact1.getColumnModel().getColumn(2).setMaxWidth(600);
            jTableFact1.getColumnModel().getColumn(3).setMaxWidth(40);
            jTableFact1.getColumnModel().getColumn(4).setMaxWidth(80);
            jTableFact1.getColumnModel().getColumn(5).setMaxWidth(80);
        }

        jPanel2.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 74, 600, 370));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel37.setText("Fecha:");

        jLabel36.setText("N° Cliente:");

        jLabel35.setText("Nombre:");

        txtFecha1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtFecha1.setText("jLabel21");

        txtNroCli1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNroCli1.setText("jLabel25");

        txtNomCli1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNomCli1.setText("jLabel26");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFecha1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNroCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(txtFecha1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(txtNroCli1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(txtNomCli1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 0, 200, 70));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel40.setText("Factura N°:");

        jLabel39.setText("Telefono:");

        jLabel38.setText("Direccion:");

        txtNumFact1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtNumFact1.setText("jLabel22");

        txtTelCli1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTelCli1.setText("jLabel23");

        txtDirCli1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtDirCli1.setText("jLabel24");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(0, 17, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumFact1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDirCli1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTelCli1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(txtNumFact1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(txtTelCli1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(txtDirCli1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 0, 200, 70));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        txtTotalFact1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtTotalFact1.setText("jLabel11");

        jLabel34.setText("Total a Pagar:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(txtTotalFact1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotalFact1)
                    .addComponent(jLabel34))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 440, 190, 29));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel15.setText("Descuento:");

        jLabel16.setText("%");

        txtDescFact1.setText("jLabel15");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDescFact1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(txtDescFact1))
                .addContainerGap())
        );

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 440, 160, 29));

        jLabel2.setFont(new java.awt.Font("Vladimir Script", 1, 48)); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/logo_1.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 170, 70));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Tel. de Contacto: 351 388-6164");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 450, 190, 20));

        Print.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 490, 620, 480));

        jPanel1.add(Print, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 620, 970));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 690, 1110));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PanelPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PanelPrintActionPerformed
        printRecord(Print);
    }//GEN-LAST:event_PanelPrintActionPerformed

    
    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Print.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton PanelPrint;
    private javax.swing.JPanel Print;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JTable jTableFact;
    public javax.swing.JTable jTableFact1;
    private javax.swing.JLabel txtDescFact;
    private javax.swing.JLabel txtDescFact1;
    public javax.swing.JLabel txtDirCli;
    public javax.swing.JLabel txtDirCli1;
    private javax.swing.JLabel txtDireCli;
    public javax.swing.JLabel txtFecha;
    public javax.swing.JLabel txtFecha1;
    public javax.swing.JLabel txtNomCli;
    public javax.swing.JLabel txtNomCli1;
    public javax.swing.JLabel txtNroCli;
    public javax.swing.JLabel txtNroCli1;
    public javax.swing.JLabel txtNumFact;
    public javax.swing.JLabel txtNumFact1;
    public javax.swing.JLabel txtTelCli;
    public javax.swing.JLabel txtTelCli1;
    private javax.swing.JLabel txtTelefCli;
    public javax.swing.JLabel txtTotalFact;
    public javax.swing.JLabel txtTotalFact1;
    // End of variables declaration//GEN-END:variables

    private void printRecord(JPanel panel){
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Print Record");
        printerJob.setPrintable(new Printable(){
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if(pageIndex > 0){
                    return NO_SUCH_PAGE;
                }
                Graphics2D graphics2d = (Graphics2D) graphics;
                
                graphics2d.translate(pageFormat.getImageableX()*2,pageFormat.getImageableY()*2);
                
                graphics2d.scale(0.9, 0.8);
                
                panel.paint(graphics2d);
                
                return Printable.PAGE_EXISTS;
            }
        });
        
        boolean returningResult = printerJob.printDialog();
        if(returningResult){
            try{
                printerJob.print();
            }catch(PrinterException printerExcepcion){
                
            }
        }
        
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
