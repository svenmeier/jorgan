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
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * Utility method for tables.
 */
public class TableUtils {

  /**
   * Fix the width of tableColumn.
   *  
   * @param table     table to fix column for
   * @param column    the index of the column to fix width
   * @param prototype prototype value to use for calculating the preferred width
   */  
  public static void fixTableColumn(JTable table, int column, Object prototype) {

    TableCellEditor editor = table.getCellEditor(0, column);
    
    Component component = editor.getTableCellEditorComponent(table,  prototype, false, 0, column);

    int width = component.getPreferredSize().width;
    
    TableColumn tableColumn = table.getColumnModel().getColumn(column);
    tableColumn.setMinWidth(width);
    tableColumn.setPreferredWidth(width);
    tableColumn.setMaxWidth(width);
  }
}