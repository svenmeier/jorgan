/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.swing.table;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * Utility method for tables.
 */
public class TableUtils {

  /**
   * Fix the width of a table column.
   *  
   * @param table     table to fix column for
   * @param column    the index of the column to fix width
   * @param prototype prototype value to use for calculating the preferred width
   */  
  public static void fixColumnWidth(JTable table, int column, Object prototype) {

    TableCellEditor editor = table.getCellEditor(0, column);
    
    Component component = editor.getTableCellEditorComponent(table, prototype, false, 0, column);

    int width = component.getPreferredSize().width;
    
    width += table.getIntercellSpacing().width;
    
    TableColumn tableColumn = table.getColumnModel().getColumn(column);
    tableColumn.setMinWidth(width);
    tableColumn.setPreferredWidth(width);
    tableColumn.setMaxWidth(width);
  }

  public static void pleasantLookAndFeel(final JTable table) {
      
    table.setGridColor(new JLabel().getBackground());
    table.setSurrendersFocusOnKeystroke(true);

    ToolTipManager.sharedInstance().registerComponent(table);

    table.addHierarchyListener(new HierarchyListener() {
        public void hierarchyChanged(HierarchyEvent e) {
            Component parent = table.getParent();
            if (parent != null && parent instanceof JViewport) {
                JViewport viewport = (JViewport)parent;
                viewport.setBackground(table.getBackground());
            }
        }        
    });
  }

  public static void hideHeader(JTable table) {
    table.getTableHeader().setPreferredSize(new Dimension(0, 0));
  }
  
  public static void addActionListener(final JTable table, final ActionListener listener) {
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          if (table.getSelectedRowCount() == 1) {
            listener.actionPerformed(new ActionEvent(table, ActionEvent.ACTION_PERFORMED, null));
          }    
        }
      }
    });
  }
  
  public static void addPopup(final JTable table, final JPopupMenu popup) {
    table.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }
        public void checkPopup(MouseEvent e) {
          if (e.isPopupTrigger()) {
            int index = table.rowAtPoint(e.getPoint());
            if (index != -1) {
              table.setRowSelectionInterval(index, index);
              popup.show(table, e.getX(), e.getY());
            }
          }
        }
    });
  }
}