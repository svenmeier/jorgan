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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for a message.
 */
public class MessageCreationPanel extends JPanel {

	private Configuration config = Configuration.getRoot().get(
			MessageCreationPanel.class);

	private JList typeList = new JList();

	private List<Class<? extends Message>> messageClasses;

	/**
	 * Constructor.
	 */
	public MessageCreationPanel() {
		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("type").read(new JLabel()));

		typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		typeList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("message", null, null);
			}
		});
		column.definition(new JScrollPane(typeList)).growVertical();
	}

	/**
	 * Set the classes to choose from.
	 * 
	 * @param messageClasses
	 *            the classes for the message to create
	 */
	public void setMessageClasses(List<Class<? extends Message>> messageClasses) {
		this.messageClasses = messageClasses;

		Collections.sort(messageClasses, new TypeComparator());
		typeList.setModel(new TypeListModel());
	}

	/**
	 * Get the message.
	 * 
	 * @return the message
	 */
	public Message getMessage() {
		Message message = null;

		int index = typeList.getSelectedIndex();
		if (index != -1) {
			try {
				message = messageClasses.get(index).newInstance();
			} catch (Exception ex) {
				throw new Error(ex);
			}
		}
		return message;
	}

	private class TypeListModel extends AbstractListModel {

		public int getSize() {
			return messageClasses.size();
		}

		public Object getElementAt(int index) {
			return Elements.getDisplayName(messageClasses.get(index));
		}
	}

	private class TypeComparator implements
			Comparator<Class<? extends Message>> {

		public int compare(Class<? extends Message> c1,
				Class<? extends Message> c2) {

			String name1 = Elements.getDisplayName(c1);
			String name2 = Elements.getDisplayName(c2);

			return name1.compareTo(name2);
		}
	}
}