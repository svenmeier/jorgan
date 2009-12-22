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
package jorgan.swing.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import jorgan.gui.OrganPanel;
import jorgan.swing.table.TableUtils;

/**
 * A panel for editing of bean properties.
 */
public class PropertiesPanel extends JPanel implements Scrollable {

	private static Logger logger = Logger.getLogger(OrganPanel.class.getName());

	private static final Object[] EMPTY_ARGUMENTS = new Object[0];

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	private List<Object> beans = new ArrayList<Object>();

	private String property;

	private Class<?> beanClass;

	private BeanInfo beanInfo;

	private PropertyDescriptor[] descriptors;

	private PropertyEditor[] editors;

	private ElementTableModel model = new ElementTableModel();

	private JTable table = new JTable();

	/**
	 * Construtor.
	 */
	public PropertiesPanel() {
		setLayout(new BorderLayout());

		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		PropertyCellRenderer valueRenderer = new PropertyCellRenderer() {
			@Override
			protected String getDescription(int row) {
				return descriptors[row].getShortDescription();
			}

			@Override
			protected PropertyEditor getEditor(int row) {
				return editors[row];
			}
		};
		table.getColumnModel().getColumn(1).setCellRenderer(valueRenderer);
		table.getColumnModel().getColumn(1).setCellEditor(
				new PropertyCellEditor() {
					@Override
					protected PropertyEditor getEditor(int row) {
						return editors[row];
					}
				});
		table.getColumnModel().getSelectionModel().addListSelectionListener(
				model);
		table.getSelectionModel().addListSelectionListener(model);
		TableUtils.pleasantLookAndFeel(table);
		TableUtils.hideHeader(table);
		add(table, BorderLayout.CENTER);
	}

	/**
	 * Add a listener to changes.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addChangeListener(ChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener to changes.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeChangeListener(ChangeListener listener) {
		listeners.remove(listener);
	}

	protected void fireChanged() {
		ChangeEvent event = new ChangeEvent(this);
		for (int l = 0; l < listeners.size(); l++) {
			ChangeListener listener = listeners.get(l);
			listener.stateChanged(event);
		}
	}

	/**
	 * Set the bean.
	 * 
	 * @param bean
	 *            bean to set
	 */
	public void setBean(Object bean) {
		if (bean == null) {
			setBeans(Collections.emptyList());
		} else {
			setBeans(Collections.singletonList(bean));
		}
	}

	/**
	 * Get the beans.
	 * 
	 * @return the beans
	 */
	public List<?> getBeans() {
		return Collections.unmodifiableList(beans);
	}

	/**
	 * Set the beans.
	 * 
	 * @param beans
	 *            the beans
	 */
	public void setBeans(List<?> beans) {
		TableCellEditor cellEditor = table.getCellEditor();
		if (cellEditor != null) {
			cellEditor.stopCellEditing();
		}

		this.beans = new ArrayList<Object>(beans);

		property = null;

		if (beans.size() == 0) {
			descriptors = null;
			editors = null;
		} else {
			try {
				beanClass = getCommonClass(this.beans);

				beanInfo = getBeanInfo(beanClass);

				descriptors = beanInfo.getPropertyDescriptors();

				editors = getEditors(descriptors);
			} catch (IntrospectionException ex) {
				logger.log(Level.WARNING, "unable to introspect", ex);

				descriptors = null;
				editors = null;
			}
		}

		model.fireTableDataChanged();
	}

	/**
	 * Get the class of the current beans.
	 * 
	 * @return the bean class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Select a property.
	 * 
	 * @param property
	 *            property to select
	 */
	public void setProperty(String property) {
		if (this.property == null && property != null || this.property != null
				&& !this.property.equals(property)) {
			this.property = property;

			if (property != null && descriptors != null) {
				for (int d = 0; d < descriptors.length; d++) {
					if (descriptors[d].getName().equals(property)) {
						table.getSelectionModel().setSelectionInterval(d, d);
						break;
					}
				}
			}

			fireChanged();
		}
	}

	/**
	 * Get the selected property.
	 * 
	 * @return selected property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Get the common superclass of all beans in the given list.
	 * 
	 * @param beans
	 *            list of beans to get common superclass for
	 * @return common superclass
	 */
	public static Class<?> getCommonClass(List<Object> beans) {
		Class<?> commonClass = null;
		for (int b = 0; b < beans.size(); b++) {
			Object bean = beans.get(b);

			if (commonClass == null) {
				commonClass = bean.getClass();
			} else {
				while ((commonClass != Object.class)
						&& !(commonClass.isAssignableFrom(bean.getClass()))) {
					commonClass = commonClass.getSuperclass();
				}
			}
		}
		return commonClass;
	}

	private PropertyEditor[] getEditors(PropertyDescriptor[] descriptors)
			throws IntrospectionException {

		PropertyEditor[] editors = new PropertyEditor[descriptors.length];

		for (int d = 0; d < descriptors.length; d++) {
			PropertyDescriptor descriptor = descriptors[d];

			if (descriptor instanceof IndexedPropertyDescriptor) {
				// indexed properties are not supported
				continue;
			}

			editors[d] = getPropertyEditor(descriptor);
		}

		return editors;
	}

	private class ElementTableModel extends AbstractTableModel implements
			ListSelectionListener {

		private boolean setting = false;

		@Override
		public String getColumnName(int column) {
			return null;
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			if (descriptors == null) {
				return 0;
			} else {
				return descriptors.length;
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return descriptors[rowIndex].getDisplayName();
			} else {
				try {
					Method method = descriptors[rowIndex].getReadMethod();

					Object value = null;
					for (int b = 0; b < beans.size(); b++) {
						Object bean = beans.get(b);

						Object temp = method.invoke(bean, EMPTY_ARGUMENTS);
						if (value == null) {
							value = temp;
						} else {
							if (temp != null && !temp.equals(value)) {
								return null;
							}
						}
					}

					return value;
				} catch (Exception ex) {
					logger.log(Level.WARNING, "unable to get property value '"
							+ descriptors[rowIndex].getName() + "'", ex);
				}
				return null;
			}
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return false;
			}
			return (descriptors[rowIndex].getWriteMethod() != null && editors[rowIndex] != null);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Method method = descriptors[rowIndex].getWriteMethod();

			if (!setting) {
				try {
					setting = true;
					onWriteProperty(method, aValue);
				} finally {
					setting = false;
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (descriptors != null && descriptors.length > 0) {
				if (e.getSource() == table.getColumnModel().getSelectionModel()) {
					if (table.getSelectedColumn() == 0) {
						table.getColumnModel().getSelectionModel()
								.setSelectionInterval(1, 1);
					}
				} else {
					int row = table.getSelectedRow();
					if (row == -1) {
						setProperty(null);
					} else {
						setProperty(descriptors[row].getName());
					}
				}
			}
		}
	}

	protected void onWriteProperty(Method method, Object value) {
		writeProperty(method, value);
	}

	protected void writeProperty(Method method, Object value) {
		for (int b = 0; b < beans.size(); b++) {
			try {
				Object bean = beans.get(b);
				method.invoke(bean, new Object[] { value });
			} catch (InvocationTargetException ex) {
				Throwable cause = ex.getCause();
				if (cause instanceof Exception) {
					logger.log(Level.WARNING, "unable to set property value",
							ex);
				} else {
					// let anything more severe bubble up
					throw new Error(cause);
				}
			} catch (Exception ex) {
				logger.log(Level.WARNING, "unable to set property value", ex);
			}
		}

	}

	public BeanInfo getBeanInfo(Class<?> beanClass)
			throws IntrospectionException {
		return new WriteableBeanInfo(new SortingBeanInfo(Introspector
				.getBeanInfo(beanClass)));
	}

	public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
			throws IntrospectionException {
		if (descriptor.getPropertyEditorClass() == null) {
			return findPropertyEditor(descriptor.getPropertyType());
		} else {
			try {
				return (PropertyEditor) descriptor.getPropertyEditorClass()
						.newInstance();
			} catch (Exception ex) {
				IntrospectionException introspectionException = new IntrospectionException(
						ex.getMessage());
				introspectionException.initCause(ex);
				throw introspectionException;
			}
		}
	}

	/**
	 * Hook method for subclasses that want to implement a custom find of a
	 * {@link PropertyEditor} if none is defined by a {@link PropertyDescriptor}
	 * .
	 * 
	 * @see java.beans.PropertyDescriptor#getPropertyEditorClass()
	 * 
	 * @param propertyType
	 *            type of property to find editor for
	 */
	protected PropertyEditor findPropertyEditor(Class<?> propertyType)
			throws IntrospectionException {
		return PropertyEditorManager.findEditor(propertyType);
	}

	public Dimension getPreferredScrollableViewportSize() {
		return table.getPreferredScrollableViewportSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return table.getScrollableBlockIncrement(visibleRect, orientation,
				direction);
	}

	public boolean getScrollableTracksViewportHeight() {
		return table.getScrollableTracksViewportHeight();
	}

	public boolean getScrollableTracksViewportWidth() {
		return table.getScrollableTracksViewportWidth();
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return table.getScrollableUnitIncrement(visibleRect, orientation,
				direction);
	}
}