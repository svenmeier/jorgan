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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jorgan.gui.OrganPanel;
import jorgan.swing.table.TableUtils;

/**
 * A panel for editing of bean properties.
 */
public class PropertiesPanel extends JPanel implements Scrollable {

	private static Logger logger = Logger.getLogger(OrganPanel.class.getName());

	private static final Object[] EMPTY_ARGUMENTS = new Object[0];

	private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	private BeanCustomizer customizer = new DefaultBeanCustomizer();

	private List<Object> beans = new ArrayList<Object>();

	private String property;

	private Class beanClass;

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
		PropertyCellRenderer nameRenderer = new PropertyCellRenderer(true);
		PropertyCellRenderer valueRenderer = new PropertyCellRenderer(false);
		table.getColumnModel().getColumn(0).setCellRenderer(nameRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(valueRenderer);
		table.getColumnModel().getColumn(1).setCellEditor(
				new PropertyCellEditor());
		table.setRowHeight(nameRenderer.getPreferredSize().height);
		table.getColumnModel().getSelectionModel().addListSelectionListener(
				model);
		table.getSelectionModel().addListSelectionListener(model);
		TableUtils.pleasantLookAndFeel(table);
		TableUtils.hideHeader(table);
		add(table, BorderLayout.CENTER);
	}

	/**
	 * Set the customizer of beans.
	 * 
	 * @param customizer
	 *            the customizer
	 */
	public void setBeanCustomizer(BeanCustomizer customizer) {
		this.customizer = customizer;
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
		setBeans(new ArrayList<Object>(beans));
	}

	/**
	 * Get the beans.
	 * 
	 * @return the beans
	 */
	public List getBeans() {
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

				beanInfo = customizer.getBeanInfo(beanClass);

				descriptors = beanInfo.getPropertyDescriptors();

				editors = getEditors(descriptors);
			} catch (IntrospectionException ex) {
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
	public Class getBeanClass() {
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
	public static Class getCommonClass(List<Object> beans) {
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

			editors[d] = customizer.getPropertyEditor(descriptor);
		}

		return editors;
	}

	private class ElementTableModel extends AbstractTableModel implements
			ListSelectionListener {
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
					logger.log(Level.WARNING, "unable to get property value", ex);
				}
				return null;
			}
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return false;
			}
			return (descriptors[rowIndex].getWriteMethod() != null && editors[rowIndex] != null);
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Method method = descriptors[rowIndex].getWriteMethod();

			for (int b = 0; b < beans.size(); b++) {
				try {
					Object bean = beans.get(b);
					method.invoke(bean, new Object[] { aValue });
				} catch (Exception ex) {
					logger.log(Level.WARNING, "unable to set property value", ex);
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

	private class PropertyCellEditor extends AbstractCellEditor implements
			TableCellEditor {

		private PropertyEditor editor;

		private JTextField textField = new JTextField();

		private JComboBox comboBox = new JComboBox();

		private PropertyCellEditor() {
			textField.setOpaque(false);
			textField.setBorder(null);
			textField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					stopCellEditing();
				}
			});

			comboBox.setEditable(false);
			comboBox.setBorder(null);
			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					stopCellEditing();
				}
			});
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {

			editor = editors[row];
			editor.setValue(value);

			Component component;
			if (editor.supportsCustomEditor()) {
				component = editor.getCustomEditor();
			} else {
				String[] tags = editor.getTags();
				if (tags == null) {
					textField.setText(editor.getAsText());
					textField.selectAll();
					component = textField;
				} else {
					comboBox.setModel(new DefaultComboBoxModel(tags));
					comboBox.setSelectedItem(editor.getAsText());
					component = comboBox;
				}
			}

			return component;
		}

		public Object getCellEditorValue() {
			if (!editor.supportsCustomEditor()) {
				try {
					if (editor.getTags() == null) {
						editor.setAsText(textField.getText());
					} else {
						editor.setAsText((String) comboBox.getSelectedItem());
					}
				} catch (IllegalArgumentException ex) {
					logger.log(Level.WARNING, "unable to get value", ex);
				}
			}
			return editor.getValue();
		}
	}

	private class PropertyCellRenderer extends JLabel implements
			TableCellRenderer {

		private boolean name;

		private PropertyCellRenderer(boolean name) {
			this.name = name;

			setOpaque(true);
			setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0));
			if (name) {
				setText("name");
			} else {
				setText("value");
			}
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
			setFont(table.getFont());

			if (name) {
				setText((String) value);
				setToolTipText(null);
				if (row == beanInfo.getDefaultPropertyIndex()) {
					setFont(getFont().deriveFont(Font.BOLD));
				}
			} else {
				if (editors[row] == null) {
					if (value == null) {
						setText("");
					} else {
						setText(value.toString());
					}
					setToolTipText(null);
				} else {
					editors[row].setValue(value);

					setText(editors[row].getAsText());
				}
			}
			setToolTipText(descriptors[row].getShortDescription());
			return this;
		}
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