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
package jorgan.gui.imports;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import jorgan.disposition.*;
import jorgan.swing.table.TableUtils;

/**
 * A selection of stops.
 */
public class StopSelectionPanel extends JPanel {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private Action allAction  = new AllAction();
  private Action noneAction = new NoneAction();

  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  
  private StopModel stopModel = new StopModel();
  
  private java.util.List stops = new ArrayList();
  
  /**
   * Constructor.
   */
  public StopSelectionPanel() {
    setLayout(new BorderLayout(10, 10));

    add(scrollPane, BorderLayout.CENTER);
     
      table.setModel(stopModel);
      table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
          firePropertyChange("selectedStops", null, null);
        }
      });
      TableUtils.pleasantLookAndFeel(scrollPane, table);
      scrollPane.setViewportView(table);
    
    JPanel buttonPanel = new JPanel(new BorderLayout());
    add(buttonPanel, BorderLayout.SOUTH);
    
      JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));
      buttonPanel.add(gridPanel, BorderLayout.EAST);
   
        gridPanel.add(new JButton(allAction)); 

        gridPanel.add(new JButton(noneAction)); 
  }
  
  public void setStops(java.util.List stops) {
    this.stops = stops;
    
    stopModel.fireTableDataChanged();
  }
 
  public java.util.List getSelectedStops() {
    int[] rows = table.getSelectedRows();
    
    ArrayList selectedStops = new ArrayList();    
    for (int r = 0; r < rows.length; r++) {
      selectedStops.add(stops.get(rows[r]));  
    }
    
    return selectedStops;
  }
  
  private class AllAction extends AbstractAction {

    public AllAction() {
      putValue(Action.NAME, resources.getString("import.stop.all"));
    }

    public void actionPerformed(ActionEvent ev) {
      table.selectAll();
    }
  }
    
  private class NoneAction extends AbstractAction {

    public NoneAction() {
      putValue(Action.NAME, resources.getString("import.stop.none"));
    }
  
    public void actionPerformed(ActionEvent ev) {
      table.clearSelection();
    }
  }
  
  private class StopModel extends AbstractTableModel {

    public Class getColumnClass(int columnIndex) {
      
      return String.class;
    }

    public String getColumnName(int column) {
      if (column == 0) {
        return resources.getString("import.stop.name");
      } else {
        return resources.getString("import.stop.program");
      }
    }

    public int getColumnCount() {
      return 2;
    }

    public int getRowCount() {
      return stops.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      Stop stop = (Stop)stops.get(rowIndex);
      if (columnIndex == 0) {
        return stop.getName();
      } else {
        return new Integer(stop.getProgram());
      }
    }
  } 
}