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
package jorgan.gui.construct;

import java.text.MessageFormat;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import jorgan.swing.table.IconTableCellRenderer;

import jorgan.play.*;
import jorgan.play.event.*;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.gui.ElementSelectionModel;
import jorgan.gui.OrganPanel;

/**
 * Panel shows the problems.
 */
public class ProblemsPanel extends JPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  /**
   * Icon used for indication of a warning.
   */
  private static final Icon warningIcon =
    new ImageIcon(OrganPanel.class.getResource("img/warning.gif"));

  /**
   * Icon used for indication of an error.
   */
  private static final Icon errorIcon =
    new ImageIcon(OrganPanel.class.getResource("img/error.gif"));

  private OrganPlay play;
  
  /**
   * The model for selection.
   */
  private ElementSelectionModel selectionModel;

  /**
   * The handler of selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  private JTable table = new JTable();
  
  private ProblemsModel problemsModel = new ProblemsModel();
  
  private List rows = new ArrayList();
  
  /**
   * Create a tree panel.
   */
  public ProblemsPanel() {
    
    setLayout(new BorderLayout());

    table.setModel(problemsModel);
    table.addMouseListener(selectionHandler);
    table.setGridColor(getBackground());
    
    Map iconMap = new HashMap();
    iconMap.put("warning", warningIcon);    
    iconMap.put("error"  , errorIcon);    
    IconTableCellRenderer.configureTableColumn(table, 0, iconMap);
    
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(new EmptyBorder(0,0,0,0));
    scrollPane.getViewport().setBackground(table.getBackground());
    add(scrollPane, BorderLayout.CENTER);
    
    setSelectionModel(new ElementSelectionModel());
  }
  
  public void setPlay(OrganPlay play) {
    if (this.play != null) {
      this.play.removePlayerListener(problemsModel);
      
      rows.clear();
    }
    
    this.play = play;
    
    if (this.play != null) {
      this.play.addPlayerListener(problemsModel);

      Organ organ = play.getOrgan();
      for (int e = 0; e < organ.getElementCount(); e++) {
        addProblems(organ.getElement(e));
      }
    }
    
    problemsModel.fireTableDataChanged();
  }

  private void addProblems(Element element) {

    List problems = play.getProblems(element);
    if (problems != null) {
      for (int p = 0; p < problems.size(); p++) {
        rows.add(new Row(element, (PlayerProblem)problems.get(p)));
      }
    }
  }
  
  private void removeProblems(Element element) {

    for (int r = rows.size() - 1; r >= 0; r--) {
      Row row = (Row)rows.get(r);
      if (row.getElement() == element) {
        rows.remove(row);
      }
    }
  }

  public void setSelectionModel(ElementSelectionModel selectionModel) {
    if (selectionModel == null) {
      throw new IllegalArgumentException("selectionModel must not be null");
    }

    this.selectionModel = selectionModel;
  }  
  
  /**
   * The handler of element selection.
   */
  private class SelectionHandler extends MouseAdapter {
    
    public void mouseClicked(MouseEvent e) {
      
      if (e.getClickCount() == 2) {
        if (table.getSelectedRowCount() == 1) {
          int index = table.getSelectedRow();

          Row row = (Row)rows.get(index);

          selectionModel.setSelectedElement(row.getElement(), row.getProblem().getProperty());
        }    
      }
    }
  }

  private class ProblemsModel extends AbstractTableModel implements PlayListener {

    private String[] columns = new String[]{" ", "Description", "Element"};
    
    public int getColumnCount() {
      return columns.length;
    }

    public String getColumnName(int column) {
      return columns[column];
    }

    public int getRowCount() {
      return rows.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      Row row = (Row)rows.get(rowIndex);
      
      switch (columnIndex) {
        case 0:
          return row.getProblem().getLevel();
        case 1:
          return row.getMessage();
        case 2:
          return row.getElement();
      }
      
      return null;
    }

    public void io(boolean input, boolean output) { }

    public void playerAdded(PlayEvent ev) {
      
      addProblems(ev.getElement());

      problemsModel.fireTableDataChanged();
    }
  
    public void playerRemoved(PlayEvent ev) {
      removeProblems(ev.getElement());

      problemsModel.fireTableDataChanged();
    }

    public void problemAdded(PlayEvent ev) {

      rows.add(new Row(ev.getElement(), ev.getProblem()));
      
      fireTableDataChanged();
    }
        
    public void problemRemoved(PlayEvent ev) {

      rows.remove(new Row(ev.getElement(), ev.getProblem()));
      
      fireTableDataChanged();
    }    
  }  

  private class Row {
    
    private Element       element;
    private PlayerProblem problem;
    private String        message;
    
    public Row(Element element, PlayerProblem problem) {
      this.element = element;      
      this.problem = problem;  
      
      String pattern = resources.getString("play." + problem);

      message = MessageFormat.format(pattern, new Object[]{problem.getValue()});
    }
 
    public Element getElement() {
      return element;   
    }
    
    public PlayerProblem getProblem() {
      return problem; 
    }
    
    public String getMessage() {
      return message;
    }
    
    public int hashCode() {
      return element.hashCode() + problem.hashCode();
    }
    
    public boolean equals(Object object) {
      if (!(object instanceof Row)) {
        return false;
      }
      
      Row row = (Row)object;

      return row.element.equals(this.element) &&
             row.problem.equals(this.problem);
    }
  }
}