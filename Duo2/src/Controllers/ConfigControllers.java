
package Controllers;

import Views.PanelAdmin;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ConfigControllers implements MouseListener{
    
    private PanelAdmin views;

    public ConfigControllers(PanelAdmin views) {
        this.views = views;
        this.views.jLabelClientes.addMouseListener(this);
        this.views.jLabelProductos.addMouseListener(this);
        this.views.jLabelFacturas.addMouseListener(this);
        
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
        if(e.getSource() == views.jLabelClientes){
            views.jPanelClientes.setBackground(new Color(255,240,240));
        }else if(e.getSource() == views.jLabelProductos){
            views.jPanelProductos.setBackground(new Color(255,240,240));
        }else if(e.getSource() == views.jLabelFacturas){
            views.jPanelFacturas.setBackground(new Color(255,240,240));}}
        
    

    @Override
    public void mouseExited(MouseEvent e) {
        if(e.getSource() == views.jLabelClientes){
            views.jPanelClientes.setBackground(new Color(255,255,255));
        }else if(e.getSource() == views.jLabelProductos){
            views.jPanelProductos.setBackground(new Color(255,255,255));
        }else if(e.getSource() == views.jLabelFacturas){
            views.jPanelFacturas.setBackground(new Color(255,255,255));
        }
    }
    
}
