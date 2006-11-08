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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.gui.OrganSession;
import jorgan.gui.construct.editor.ElementAwareEditor;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.beans.DefaultBeanCustomizer;
import jorgan.swing.beans.PropertiesPanel;
import swingx.docking.DockedPanel;

/**
 * Panel shows the properties of elements.
 */
public class ElementPropertiesPanel extends DockedPanel {

    private static final String[] BEAN_INFO_SEARCH_PATH = new String[] { "jorgan.gui.construct.info" };

    protected static final ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    /**
     * The handler of selection changes.
     */
    private SelectionHandler selectionHandler = new SelectionHandler();

    private OrganSession session;

    private PropertiesPanel propertiesPanel = new PropertiesPanel();

    public ElementPropertiesPanel() {

        ElementCustomizer customizer = new ElementCustomizer();
        propertiesPanel.setBeanCustomizer(customizer);
        propertiesPanel.addChangeListener(selectionHandler);
        setScrollableBody(propertiesPanel, true, false);
    }

    public void setOrgan(OrganSession session) {
        if (this.session != null) {
            this.session.removeSelectionListener(selectionHandler);
            this.session.removeOrganListener(selectionHandler);
            
            selectionHandler.clearProperties();
        }

        this.session = session;

        if (this.session != null) {
            this.session.addSelectionListener(selectionHandler);
            this.session.addOrganListener(selectionHandler);
                        
            selectionHandler.updateProperties();
        }
    }

    /**
     * The handler of selections.
     */
    private class SelectionHandler extends OrganAdapter implements
            ElementSelectionListener, ChangeListener {

        private boolean changing = false;

        public void selectionChanged(ElementSelectionEvent ev) {
            updateProperties();
        }

        public void stateChanged(ChangeEvent e) {
            if (!changing) {
                changing = true;

                String property = propertiesPanel.getProperty();
                session.getSelectionModel().setSelectedProperty(property);

                changing = false;
            }
        }

        public void elementChanged(OrganEvent event) {
            if (propertiesPanel.getBeans().contains(event.getElement())) {
                updateProperties();
            }
        }

        public void referenceAdded(OrganEvent event) {
            List beans = propertiesPanel.getBeans();
            if (beans.contains(event.getElement())
                    || beans.contains(event.getReference().getElement())) {
                updateProperties();
            }
        }

        public void referenceRemoved(OrganEvent event) {
            List beans = propertiesPanel.getBeans();
            if (beans.contains(event.getElement())
                    || beans.contains(event.getReference().getElement())) {
                updateProperties();
            }
        }

        private void updateProperties() {
            if (!changing) {
                changing = true;

                propertiesPanel.setBeans(session.getSelectionModel()
                        .getSelectedElements());
                propertiesPanel.setProperty(session.getSelectionModel()
                        .getSelectedProperty());

                changing = false;
            }
        }
        
        private void clearProperties() {
            if (!changing) {
                changing = true;

                propertiesPanel.setBean(null);

                changing = false;
            }
        }
    }

    private class ElementCustomizer extends DefaultBeanCustomizer {

        public BeanInfo getBeanInfo(Class beanClass)
                throws IntrospectionException {
            Introspector.setBeanInfoSearchPath(BEAN_INFO_SEARCH_PATH);

            return super.getBeanInfo(beanClass);
        }

        public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
                throws IntrospectionException {
            PropertyEditor editor = super.getPropertyEditor(descriptor);

            if (editor != null && editor instanceof ElementAwareEditor) {
                ((ElementAwareEditor) editor)
                        .setElement((Element) propertiesPanel.getBeans().get(0));
            }

            return editor;
        }
    }
}