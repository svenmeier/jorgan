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

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.App;
import jorgan.disposition.Element;
import jorgan.gui.Elements;
import jorgan.swing.GridBuilder;
import bias.Context;

/**
 * A panel for an element.
 */
public class ElementCreationPanel extends JPanel {

	private Context context = App.getBias().get(ElementCreationPanel.class);

	protected Insets standardInsets = new Insets(2, 2, 2, 2);

	private JLabel nameLabel = new JLabel();

	private JTextField nameTextField = new JTextField();

	private JLabel typeLabel = new JLabel();

	private JList typeList = new JList();

	private List<Class<? extends Element>> elementClasses;

	/**
	 * Constructor.
	 */
	public ElementCreationPanel() {
		super(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		context.get("nameLabel").getValues(nameLabel);
		add(nameLabel, builder.nextColumn());

		nameTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				firePropertyChange("elementName", null, null);
			}

			public void insertUpdate(DocumentEvent e) {
				firePropertyChange("elementName", null, null);
			}

			public void removeUpdate(DocumentEvent e) {
				firePropertyChange("elementName", null, null);
			}
		});
		add(nameTextField, builder.nextColumn().gridWidthRemainder()
				.fillHorizontal());

		builder.nextRow(1.0d);

		context.get("typeLabel").getValues(typeLabel);
		add(typeLabel, builder.nextColumn().alignNorthWest());

		typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("elementClass", null, null);
			}
		});
		add(new JScrollPane(typeList), builder.nextColumn()
				.gridWidthRemainder().gridHeight(2).fillBoth());
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
	public void setElementClass(Class elementClass) {
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
	public Class getElementClass() {
		int index = typeList.getSelectedIndex();

		if (index == -1) {
			return null;
		} else {
			return elementClasses.get(index);
		}
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
			return Elements.getDisplayName(elementClasses.get(index));
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