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
import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
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

  private JEditorPane editor = new JEditorPane(); 
  private JScrollPane scrollPane = new JScrollPane();

  /**
   * The handler of selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  private OrganSession session;
    
  public InstructionsPanel() {
    super(new BorderLayout());
        
    editor.setEditable(false);
    editor.setForeground(Color.BLACK);
    editor.setBackground(new Color(255, 255, 225));
    editor.setContentType("text/html");
    
    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setViewportView(editor);
    
    add(scrollPane);
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
      
    
  /**
   * The handler of selections.
   */
  private class SelectionHandler implements ElementSelectionListener {

    public void selectionChanged(ElementSelectionEvent ev) {
      if (session.getSelectionModel().isElementSelected()) {
        Class   clazz    = PropertiesPanel.getCommonClass(session.getSelectionModel().getSelectedElements());
        String  property = session.getSelectionModel().getSelectedProperty();
        try {
          URL url;
          if (property == null) {
             url = Documents.getInstance().getInstructions(clazz);
          } else {
             url = Documents.getInstance().getInstructions(clazz, property);
          }
          editor.setPage(url);
          return;
        } catch (IOException ex) {
        }
      }
      editor.setDocument(editor.getEditorKit().createDefaultDocument());
    }
  }
}