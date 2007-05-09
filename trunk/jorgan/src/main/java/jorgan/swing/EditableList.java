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
package jorgan.swing;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * An editable list.
 */
public class EditableList extends JPanel {

	private JTextField textField = new JTextField();

	private JList list = new JList();

	private boolean checking = false;

	private java.util.List<String> values = new ArrayList<String>();

	/**
	 * Constructor.
	 */
	public EditableList() {
		super(new BorderLayout());

		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				checkSelectedValue();
			}

			public void removeUpdate(DocumentEvent e) {
				checkSelectedValue();
			}

			public void changedUpdate(DocumentEvent e) {
				checkSelectedValue();
			}
		});
		add(textField, BorderLayout.NORTH);

		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!checking) {
					textField.setText((String) list.getSelectedValue());
				}
			}
		});
		add(new JScrollPane(list), BorderLayout.CENTER);
	}

	private void checkSelectedValue() {
		if (values != null) {
			checking = true;
			int index = values.indexOf(textField.getText());
			if (index != -1) {
				list.setSelectedIndex(index);
			}
			checking = false;
		}
	}

	/**
	 * Set the values of this list.
	 * 
	 * @param values
	 *            the list values
	 */
	public void setValues(List<String> values) {
		this.values = values;

		DefaultListModel model = new DefaultListModel();

		for (int v = 0; v < values.size(); v++) {
			model.addElement(values.get(v));
		}
		list.setModel(model);
	}

	/**
	 * Set the values of this list.
	 * 
	 * @param values
	 *            the list values
	 */
	public void setValuesAsArray(String[] values) {
		setValues(Arrays.asList(values));
	}

	/**
	 * Get the values of this list.
	 * 
	 * @return values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Set the selected value.
	 * 
	 * @param value
	 *            the selectedvalues
	 */
	public void setSelectedValue(String value) {
		textField.setText(value);

		checkSelectedValue();
	}

	/**
	 * Get the selected value.
	 * 
	 * @return the selected value
	 */
	public String getSelectedValue() {
		return textField.getText();
	}

	/**
	 * Get the selected index.
	 * 
	 * @return the selected index
	 */
	public int getSelectedIndex() {

		return values.indexOf(textField.getText());
	}
}