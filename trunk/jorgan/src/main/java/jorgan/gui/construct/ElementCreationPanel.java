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

import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;

/**
 * A panel for an element.
 */
public class ElementCreationPanel extends JPanel {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  protected Insets standardInsets = new Insets(2,2,2,2);

  private JLabel       nameLabel        = new JLabel();
  private JTextField   nameTextField    = new JTextField();
  private JLabel       typeLabel        = new JLabel();
  private JList        typeList         = new JList();
  
  private Class[] elementClasses = new Class[0];
   
  public ElementCreationPanel() {
    setLayout(new GridBagLayout());

    nameLabel.setText(resources.getString("construct.create.element.name"));
    add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    nameTextField.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        firePropertyChange("name", null, null);
      }
      public void insertUpdate(DocumentEvent e) {
        firePropertyChange("name", null, null);
      }
      public void removeUpdate(DocumentEvent e) {
        firePropertyChange("name", null, null);
      }
    });
    add(nameTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

    typeLabel.setText(resources.getString("construct.create.element.type"));
    add(typeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    typeList.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        firePropertyChange("type", null, null);
      }
    });
    add(new JScrollPane(typeList), new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, standardInsets, 0, 0));
  }
  
  public void setElementClasses(Class[] elementClasses) {
    this.elementClasses = elementClasses;

    typeList.setModel(new TypeListModel());  
  }
  
  public Element getElement() {

    String name  = nameTextField.getText();
    int    index = typeList.getSelectedIndex();
    
    if (index != -1 && !"".equals(name)) {
      try {
        Element element = (Element)elementClasses[index].newInstance();
        
        element.setName(name);
        
        return element;
      } catch (Exception ex) {
      }
    }
    return null; 
  }
  
  private class TypeListModel extends AbstractListModel {
    
    public int getSize() {
      return elementClasses.length;
    }

    public Object getElementAt(int index) {
      return ElementUtils.getTypeName(elementClasses[index]);
    }   
  }
}