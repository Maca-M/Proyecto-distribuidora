
package Models;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


public class Tables extends DefaultTableCellRenderer{
    
    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if(table.getValueAt(row,col).toString().equals("Inactivo")){
            setBackground(Color.red);
        }else{
            setBackground(Color.white);
        }
        return this;
    }

    
}
