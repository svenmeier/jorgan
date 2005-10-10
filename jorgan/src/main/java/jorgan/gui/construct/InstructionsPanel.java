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

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jorgan.docs.Documents;
import jorgan.gui.OrganSession;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.beans.PropertiesPanel;

/**
 * Panel for instructions for the currently selected element.
 */
public class InstructionsPanel extends JPanel {

  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private JEditorPane editor = new JEditorPane(); 
  private JScrollPane scrollPane = new JScrollPane();
  
  private JLabel label = new JLabel();

  /**
   * The handler of selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  private OrganSession session;
  
  public InstructionsPanel() {
    super(new BorderLayout());
    
    setOpaque(false);
    
    addHierarchyListener(selectionHandler);
        
    label.setText(" ");
    label.setHorizontalAlignment(JLabel.LEFT);
    label.setVerticalAlignment(JLabel.TOP);
    add(label, BorderLayout.NORTH);
    
    editor.setEditable(false);
    editor.setMargin(new Insets(0,0,0,0));
    editor.setContentType("text/html");
    
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(editor);
    scrollPane.setVisible(false);
    add(scrollPane, BorderLayout.CENTER);
  }

  public void setOrgan(OrganSession session) {
    if (this.session != null) {
      this.session.getSelectionModel().removeSelectionListener(selectionHandler);
    }

    this.session = session;

    if (this.session != null) {
      this.session.getSelectionModel().addSelectionListener(selectionHandler);        
    }
  }

  protected void updateInstructions(Class clazz, String property) {
    if (clazz == null) {
      scrollPane.setVisible(false);
      label.setText(" ");
    } else {
      try {
        URL url;
        if (property == null) {
          url = Documents.getInstance().getInstructions(clazz);
        } else {
          url = Documents.getInstance().getInstructions(clazz, property);
        }
        editor.setPage(url);
          
        scrollPane.setVisible(true);
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(Documents.getInstance().getDisplayName(clazz));
        if (property != null) {
            buffer.append(" - ");
            buffer.append(Documents.getInstance().getDisplayName(clazz, property));
        }
        label.setText(buffer.toString());
      } catch (Exception ex) {
        scrollPane.setVisible(false);
        label.setText(resources.getString("construct.instructions.failure"));
      }
    }
  }
      
  /**
   * The handler of selections.
   */
  private class SelectionHandler implements ElementSelectionListener, HierarchyListener {

    private Class  clazz;
    private String property;
        
    public void selectionChanged(ElementSelectionEvent ev) {
      if (session.getSelectionModel().isElementSelected()) {
        clazz    = PropertiesPanel.getCommonClass(session.getSelectionModel().getSelectedElements());
        property = session.getSelectionModel().getSelectedProperty();
      } else {
        clazz    = null;
        property = null;
      }
      
      if (isShowing()) {
        flush();
      }
    }
    
    public void hierarchyChanged(HierarchyEvent e) {
      if (clazz != null && isShowing()) {
        flush();
      }
    }
    
    protected void flush() {
      updateInstructions(clazz, property);
      this.clazz    = null;
      this.property = null;
    }
  }
}