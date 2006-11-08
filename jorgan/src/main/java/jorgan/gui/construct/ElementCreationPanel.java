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
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;

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

import jorgan.docs.Documents;
import jorgan.swing.GridBuilder;

/**
 * A panel for an element.
 */
public class ElementCreationPanel extends JPanel {

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

	protected Insets standardInsets = new Insets(2, 2, 2, 2);

	private JLabel nameLabel = new JLabel();

	private JTextField nameTextField = new JTextField();

	private JLabel typeLabel = new JLabel();

	private JList typeList = new JList();

	private Class[] elementClasses = new Class[0];

	public ElementCreationPanel() {
		super(new GridBagLayout());
		
		GridBuilder builder = new GridBuilder(new double[]{0.0d, 1.0d});
		
		builder.nextRow();

		nameLabel.setText(resources.getString("construct.create.element.name"));
		add(nameLabel, builder.nextColumn());

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
		add(nameTextField, builder.nextColumn().gridWidthRemainder().fillHorizontal());

		builder.nextRow(1.0d);

		typeLabel.setText(resources.getString("construct.create.element.type"));
		add(typeLabel, builder.nextColumn().alignNorthWest());

		typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("type", null, null);
			}
		});
		add(new JScrollPane(typeList), builder.nextColumn().gridWidthRemainder().gridHeight(2).fillBoth());
	}

	public void setElementClasses(Class[] elementClasses) {
		this.elementClasses = elementClasses;

		Arrays.sort(elementClasses, new TypeComparator());
		typeList.setModel(new TypeListModel());
	}

	public void setElementClass(Class elementClass) {
		for (int c = 0; c < elementClasses.length; c++) {
			if (elementClasses[c] == elementClass) {
				typeList.setSelectedIndex(c);
				return;
			}
		}
	}

	public Class getElementClass() {
		int index = typeList.getSelectedIndex();

		if (index == -1) {
			return null;
		} else {
			return elementClasses[index];
		}
	}

	public String getElementName() {
		return nameTextField.getText();
	}

	private class TypeListModel extends AbstractListModel {

		public int getSize() {
			return elementClasses.length;
		}

		public Object getElementAt(int index) {
			return Documents.getInstance()
					.getDisplayName(elementClasses[index]);
		}
	}

	private class TypeComparator implements Comparator {

		public int compare(Object o1, Object o2) {

			String name1 = Documents.getInstance().getDisplayName((Class) o1);
			String name2 = Documents.getInstance().getDisplayName((Class) o2);

			return name1.compareTo(name2);
		}
	}
}