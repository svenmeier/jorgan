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

import java.awt.Component;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.text.DocumentNotifier;
import bias.Configuration;

/**
 * A panel for an element.
 */
public class ElementCreationPanel extends JPanel {

	private Configuration config = Configuration.getRoot().get(
			ElementCreationPanel.class);

	private JTextField nameTextField = new JTextField();

	private JList typeList = new JList();

	private List<Class<? extends Element>> elementClasses;

	/**
	 * Constructor.
	 */
	public ElementCreationPanel() {

		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();

		column.term(config.get("name").read(new JLabel()));
		nameTextField.getDocument().addDocumentListener(new DocumentNotifier() {
			public void changed() {
				firePropertyChange("elementName", null, null);
			}
		});
		column.definition(nameTextField).fillHorizontal();

		column.term(config.get("type").read(new JLabel()));
		typeList.setCellRenderer(new DefaultListCellRenderer() {

			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				Class<?> clazz = (Class<?>) value;
				
				super.getListCellRendererComponent(list, Elements.getDisplayName(clazz), index,
						isSelected, cellHasFocus);

				setIcon(Elements.getIcon(clazz));

				return this;
			}

		});
		typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("elementClass", null, null);
			}
		});
		column.definition(new JScrollPane(typeList)).growVertical().fillBoth();
	}

	/**
	 * Set the classes to choose from.
	 * 
	 * @param elementClasses
	 *            the classes for the element to create
	 */
	public void setElementClasses(List<Class<? extends Element>> elementClasses) {
		this.elementClasses = elementClasses;

		Collections.sort(elementClasses, new TypeComparator());
		typeList.setModel(new TypeListModel());
	}

	/**
	 * Set the class for the element.
	 * 
	 * @param elementClass
	 *            the class for the element to create
	 */
	public void setElementClass(Class<?> elementClass) {
		for (int c = 0; c < elementClasses.size(); c++) {
			if (elementClasses.get(c) == elementClass) {
				typeList.setSelectedIndex(c);
				typeList.scrollRectToVisible(typeList.getCellBounds(c, c));
				return;
			}
		}
	}

	/**
	 * Get the class for the element.
	 * 
	 * @return the element class
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Element> getElementClass() {
		return (Class<? extends Element>)typeList.getSelectedValue();
	}

	/**
	 * Get the element name.
	 * 
	 * @return name of element
	 */
	public String getElementName() {
		return nameTextField.getText();
	}

	private class TypeListModel extends AbstractListModel {

		public int getSize() {
			return elementClasses.size();
		}

		public Object getElementAt(int index) {
			return elementClasses.get(index);
		}
	}

	private class TypeComparator implements
			Comparator<Class<? extends Element>> {

		public int compare(Class<? extends Element> c1,
				Class<? extends Element> c2) {

			String name1 = Elements.getDisplayName(c1);
			String name2 = Elements.getDisplayName(c2);

			return name1.compareTo(name2);
		}
	}
}