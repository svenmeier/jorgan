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
import java.awt.BorderLayout;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import javax.swing.*;

import jorgan.disposition.*;
import jorgan.gui.ElementSelectionModel;
import jorgan.gui.construct.editor.ElementAwareEditor;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.beans.DefaultBeanCustomizer;
import jorgan.swing.beans.PropertiesPanel;

/**
 * Panel shows the properties of elements.
 */
public class ElementPropertiesPanel extends JPanel {

  private static final String[] BEAN_INFO_SEARCH_PATH = new String[]{"jorgan.gui.construct.info"};

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  /**
   * The handler of selection changes.
   */
  private SelectionHandler selectionHandler = new SelectionHandler();

  /**
   * The model for selection.
   */
  private ElementSelectionModel selectionModel;

  private PropertiesPanel propertiesPanel = new PropertiesPanel();
  
  public ElementPropertiesPanel() {
    super(new BorderLayout());
    
    ElementCustomizer customizer = new ElementCustomizer();
    propertiesPanel.setBeanCustomizer(customizer);
    add(propertiesPanel, BorderLayout.CENTER);
  }

  public void setSelectionModel(ElementSelectionModel selectionModel) {
    if (selectionModel == null) {
      throw new IllegalArgumentException("selectionModel must not be null");
    }

    // only null if called from constructor
    if (this.selectionModel != null) {
      this.selectionModel.removeSelectionListener(selectionHandler);
    }

    this.selectionModel = selectionModel;

    selectionModel.addSelectionListener(selectionHandler);     
  }
    
  /**
   * The handler of selections.
   */
  private class SelectionHandler implements ElementSelectionListener {

    public void selectionChanged(ElementSelectionEvent ev) {
      propertiesPanel.setBeans(selectionModel.getSelectedElements());
      propertiesPanel.setProperty(selectionModel.getSelectedProperty());
    }
  }
  
  private class ElementCustomizer extends DefaultBeanCustomizer {

    public BeanInfo getBeanInfo(Class beanClass) throws IntrospectionException {
        Introspector.setBeanInfoSearchPath(BEAN_INFO_SEARCH_PATH);
        
        return super.getBeanInfo(beanClass);
    }
     
    public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
            throws IntrospectionException {
        PropertyEditor editor = super.getPropertyEditor(descriptor);
        
        if (editor != null && editor instanceof ElementAwareEditor) {
            ((ElementAwareEditor)editor).setElement((Element)propertiesPanel.getBeans().get(0));
        }
        
        return editor;
    }
  }
}