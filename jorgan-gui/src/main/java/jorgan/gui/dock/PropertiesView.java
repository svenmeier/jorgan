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
package jorgan.gui.dock;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bias.Configuration;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.construct.editor.ElementAwareEditor;
import jorgan.gui.construct.info.spi.BeanInfoSearchPathRegistry;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.swing.beans.PropertiesPanel;
import spin.Spin;

/**
 * Dockable shows the properties of elements.
 */
public class PropertiesView extends AbstractView {

	private static Configuration config = Configuration.getRoot()
			.get(PropertiesView.class);

	/**
	 * The handler of selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private OrganSession session;

	private PropertiesPanel panel = new PropertiesPanel() {
		@Override
		public BeanInfo getBeanInfo(Class<?> beanClass)
				throws IntrospectionException {
			Introspector.setBeanInfoSearchPath(
					BeanInfoSearchPathRegistry.getBeanInfoSearchPath());

			return super.getBeanInfo(beanClass);
		}

		@Override
		public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
				throws IntrospectionException {
			PropertyEditor editor = super.getPropertyEditor(descriptor);

			if (editor != null && editor instanceof ElementAwareEditor) {
				((ElementAwareEditor) editor).setElement(session,
						(Element) panel.getBeans().get(0));
			}

			return editor;
		}

		@Override
		protected void onWriteProperty(final Method method,
				final Object value) {

			session.lookup(UndoManager.class).compound(new Compound() {
				public void run() {
					writeProperty(method, value);
				};
			});
		}
	};

	public PropertiesView() {
		config.read(this);

		panel.addChangeListener(selectionHandler);

		setContent(new JScrollPane(panel));
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.lookup(ElementSelection.class)
					.removeListener(selectionHandler);
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(selectionHandler));

			selectionHandler.clearProperties();
		}

		this.session = session;

		if (this.session != null) {
			this.session.lookup(ElementSelection.class)
					.addListener(selectionHandler);
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(selectionHandler));

			selectionHandler.updateProperties();
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler extends OrganAdapter
			implements SelectionListener, ChangeListener {

		private boolean changing = false;

		public void selectionChanged() {

			Element element = session.lookup(ElementSelection.class)
					.getSelectedElement();
			if (element == null) {
				setStatus(null);
			} else {
				setStatus(Elements.getDisplayName(element.getClass()));
			}

			updateProperties();
		}

		public void stateChanged(ChangeEvent e) {
			if (!changing) {
				changing = true;

				session.lookup(UndoManager.class).compound();

				String property = panel.getProperty();
				session.lookup(ElementSelection.class).setLocation(property);

				changing = false;
			}
		}

		@Override
		public void propertyChanged(Element element, String name) {
			if (panel.getBeans().contains(element)) {
				updateProperties();
			}
		}

		private void updateProperties() {
			if (!changing) {
				changing = true;

				panel.setBeans(session.lookup(ElementSelection.class)
						.getSelectedElements());

				Object location = session.lookup(ElementSelection.class)
						.getLocation();
				if (location instanceof String) {
					panel.setProperty((String) location);
				} else {
					panel.setProperty(null);
				}

				changing = false;
			}
		}

		private void clearProperties() {
			if (!changing) {
				changing = true;

				panel.setBean(null);

				changing = false;
			}
		}
	}
}