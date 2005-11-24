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
package jorgan.gui;

import java.awt.BorderLayout;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import spin.Spin;

import jorgan.disposition.Memory;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;

/**
 * Panel for editing of a {@link jorgan.disposition.Memory}.
 */
public class MemoryPanel extends JPanel {

  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private JScrollPane scrollPane = new JScrollPane();
  private JTable table = new JTable();
  private JLabel label = new JLabel();
  
  private LevelsModel model = new LevelsModel();

  private OrganSession session;
  
  private Memory memory;
  
  public MemoryPanel() {
      setLayout(new BorderLayout());

      label.setText(resources.getString("memory.none"));
      label.setHorizontalAlignment(JLabel.LEFT);
      label.setVerticalAlignment(JLabel.TOP);
      add(label, BorderLayout.NORTH);
      
      scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scrollPane, BorderLayout.CENTER);
      
      table.setModel(model);
      table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      table.getSelectionModel().addListSelectionListener(model);
      table.getColumnModel().getColumn(1).setCellEditor(new StringCellEditor());
      TableUtils.hideHeader(table);
      TableUtils.fixColumnWidth(table, 0, "888");
      TableUtils.pleasantLookAndFeel(scrollPane, table);
      scrollPane.setViewportView(table);      
      
      setMemory(null);
  }

  public void setOrgan(OrganSession session) {
    if (this.session != null) {
      this.session.getOrgan().removeOrganListener((OrganListener)Spin.over(model));

      setMemory(null);
    }

    this.session = session;

    if (this.session != null) {
      this.session.getOrgan().addOrganListener((OrganListener)Spin.over(model));

      findMemory();
    }
  }

  private void findMemory() {
    List memories = this.session.getOrgan().getCandidates(Memory.class);
    if (memories.isEmpty()) {
      setMemory(null);
    } else {
      setMemory((Memory)memories.get(0));        
    }
  }
  
  private void setMemory(Memory memory) {
    this.memory = memory;
        
    model.fireTableDataChanged();
        
    if (memory == null) {
      label.setVisible(true);
      scrollPane.setVisible(false);
    } else {
      label.setVisible(false);
      scrollPane.setVisible(true);
      
      updateLevel();
    }    
  }      
  
  private void updateLevel() {
    int level = memory.getCurrent();
    if (level != table.getSelectedRow()) {
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        table.getSelectionModel().setSelectionInterval(level, level);
        table.scrollRectToVisible(table.getCellRect(level, 0, false));
    }
    table.setColumnSelectionInterval(0, 0);
  }
  
  private class LevelsModel extends AbstractTableModel implements OrganListener, ListSelectionListener {
    public int getColumnCount() {
      return 2;
    }
    
    public int getRowCount() {
      if (memory == null) {
        return 0;
      } else {
        return 128;
      }
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (columnIndex == 0) {
        return "" + (rowIndex + 1);
      }
      return memory.getTitle(rowIndex);
    }
    
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 1;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      memory.setTitle(rowIndex, (String)aValue);
    }
    
    public void elementAdded(OrganEvent event) {
      if (event.getElement() instanceof Memory) {
        setMemory((Memory)event.getElement());
      }
    }
    
    public void elementChanged(OrganEvent event) {
      if (event.getElement() == memory) {
        updateLevel();
      }
    }
    
    public void elementRemoved(OrganEvent event) {
      if (event.getElement() instanceof Memory) {
        findMemory();
      }
    }
    
    public void referenceAdded(OrganEvent event) { }
    public void referenceChanged(OrganEvent event) { }
    public void referenceRemoved(OrganEvent event) { }
    
    public void valueChanged(ListSelectionEvent e) {
      int row = table.getSelectedRow();
      if (row != -1 && row != memory.getCurrent()) {
        memory.setCurrent(row);
      }
    }
  }  
}