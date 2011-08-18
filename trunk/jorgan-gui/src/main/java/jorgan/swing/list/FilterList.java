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
package jorgan.swing.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jorgan.swing.border.BorderSubstitute;
import jorgan.swing.border.RuleBorder;

public abstract class FilterList<I> extends JPanel {

	private List<I> items = new ArrayList<I>();

	private JTextField textField = new JTextField();

	private JList list = new JList();

	private SelectAction selectAction = new SelectAction();

	private JScrollPane scrollPane;

	public FilterList() {
		this(true);
	}

	public FilterList(boolean light) {
		setLayout(new BorderLayout());

		textField.setColumns(20);
		if (light) {
			textField.setBorder(new BorderSubstitute(textField));
			textField.setOpaque(false);
		}
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				filterChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				filterChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				filterChanged();
			}
		});
		textField.addActionListener(selectAction);
		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				this);
		textField.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), this);
		textField.getActionMap().put(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (items.size() > 0) {
					list.requestFocusInWindow();
				}
			}
		});
		add(textField, BorderLayout.NORTH);
		textField.requestFocusInWindow();

		ListUtils.addActionListener(list, light ? 1 : 2, selectAction);
		if (light) {
			ListUtils.addHoverSelection(list);
		}

		scrollPane = new JScrollPane(list);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		if (light) {
			scrollPane.setBorder(new CompoundBorder(
					new EmptyBorder(10, 0, 0, 0),
					new RuleBorder(RuleBorder.TOP)));
			scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		}
		add(scrollPane, BorderLayout.CENTER);

		list.setCellRenderer(new DefaultListCellRenderer() {
			@SuppressWarnings("unchecked")
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, FilterList.this
						.toString((I) value), index, isSelected, cellHasFocus);
			}
		});

		filterChanged();
	}

	public FilterList(ListCellRenderer renderer) {
		this(true, renderer);
	}

	public FilterList(boolean light, ListCellRenderer renderer) {
		this(light);

		list.setCellRenderer(renderer);
	}

	@Override
	public void setOpaque(boolean isOpaque) {
		super.setOpaque(isOpaque);

		if (textField != null) {
			textField.setOpaque(isOpaque);
		}

		if (list != null) {
			list.setOpaque(isOpaque);
		}

		if (scrollPane != null) {
			scrollPane.setOpaque(isOpaque);
		}
	}

	@Override
	public void setBackground(Color background) {
		super.setBackground(background);

		if (textField != null) {
			textField.setBackground(background);
		}

		if (list != null) {
			list.setBackground(background);
		}

		if (scrollPane != null) {
			scrollPane.setBackground(background);
		}
	}

	private void filterChanged() {
		I item = getItem();

		items = getItems(textField.getText());

		list.setModel(new ItemModel());

		if (items.contains(item)) {
			list.setSelectedValue(item, true);
		} else if (items.size() > 0) {
			list.setSelectedIndex(0);
		}
	}

	protected abstract List<I> getItems(String filter);

	protected abstract void onSelectedItem(I item);

	/**
	 * Default presentation of items in the list.
	 * 
	 * @param item
	 * @return
	 */
	protected String toString(I item) {
		return item.toString();
	}

	@SuppressWarnings("unchecked")
	public I getItem() {
		return (I) list.getSelectedValue();
	}

	private class SelectAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			I item = getItem();
			if (item != null) {
				onSelectedItem(item);
			}
		}
	}

	private class ItemModel extends AbstractListModel {
		public int getSize() {
			return items.size();
		}

		public Object getElementAt(int index) {
			return items.get(index);
		}
	}

	public void setSelectedItem(I item) {
		list.setSelectedValue(item, true);
	}
}