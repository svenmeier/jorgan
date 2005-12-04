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
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import spin.Spin;
import swingx.docking.DockedPanel;

import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;

import jorgan.play.*;
import jorgan.play.event.*;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.gui.OrganPanel;
import jorgan.gui.OrganSession;

/**
 * Panel shows the problems.
 */
public class ProblemsPanel extends DockedPanel {

  private static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

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

  private OrganSession session;

  private JTable table = new JTable();
  
  private ProblemsModel problemsModel = new ProblemsModel();
  
  private List rows = new ArrayList();
  
  private JPopupMenu popup = new JPopupMenu();
  
  private GotoAction gotoAction = new GotoAction();
  
  /**
   * Create a tree panel.
   */
  public ProblemsPanel() {

    table.setModel(problemsModel);    
    TableUtils.addActionListener(table, gotoAction);
    TableUtils.addPopup(table, popup);
    TableUtils.pleasantLookAndFeel(table);
    Map iconMap = new HashMap();
    iconMap.put("warning", warningIcon);    
    iconMap.put("error"  , errorIcon);    
    IconTableCellRenderer.configureTableColumn(table, 0, iconMap);   
    setScrollableBody(table, true, false);
    
    popup.add(gotoAction);
  }
  
  public void setOrgan(OrganSession session) {
    if (this.session != null) {
      this.session.getPlay().removePlayerListener((PlayListener)Spin.over(problemsModel));
      
      rows.clear();
    }
    
    this.session = session;
    
    if (this.session != null) {
      this.session.getPlay().addPlayerListener((PlayListener)Spin.over(problemsModel));

      Organ organ = this.session.getOrgan();
      for (int e = 0; e < organ.getElementCount(); e++) {
        addProblems(organ.getElement(e));
      }
    }
    
    problemsModel.fireTableDataChanged();
  }

  private void addProblems(Element element) {

    List problems = session.getPlay().getProblems(element);
    if (problems != null) {
      for (int p = 0; p < problems.size(); p++) {
        rows.add(new Row(element, (Problem)problems.get(p)));
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

    public void inputAccepted() { }

    public void outputProduced() { }

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
    private Problem problem;
    private String        message;
    
    public Row(Element element, Problem problem) {
      this.element = element;      
      this.problem = problem;  
      
      String pattern = resources.getString("problems." + problem);

      message = MessageFormat.format(pattern, new Object[]{problem.getValue()});
    }
 
    public Element getElement() {
      return element;   
    }
    
    public Problem getProblem() {
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
  
  private class GotoAction extends AbstractAction {

    public GotoAction() {
      putValue(Action.NAME             , resources.getString("problems.action.goto.name"));
      putValue(Action.SHORT_DESCRIPTION, resources.getString("problems.action.goto.description"));
      putValue(Action.SMALL_ICON       , new ImageIcon(ProblemsPanel.class.getResource("/jorgan/gui/img/goto.gif")));
   }

    public void actionPerformed(ActionEvent ev) {
      int index = table.getSelectedRow();

      Row row = (Row)rows.get(index);

      session.getSelectionModel().setSelectedElement(row.getElement(), row.getProblem().getProperty());
    }    
  }    
}