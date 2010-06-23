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
package jorgan.gui.search;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.selection.ElementSelection;
import jorgan.session.OrganSession;
import jorgan.swing.StandardDialog;
import jorgan.swing.list.FilterList;
import bias.Configuration;

/**
 * A dialog for searching.
 */
public class SearchDialog extends StandardDialog {

	private static Configuration config = Configuration.getRoot().get(
			SearchDialog.class);

	private OrganSession session;

	private FilterList<Element> list;

	/**
	 * Constructor.
	 */
	private SearchDialog(OrganSession session, JDialog owner) {
		super(owner);

		init(session);
	}

	/**
	 * Constructor.
	 * 
	 * @param session
	 */
	private SearchDialog(OrganSession session, JFrame owner) {
		super(owner);

		init(session);
	}

	private void init(final OrganSession session) {
		this.session = session;

		list = new FilterList<Element>(false, new ElementListCellRenderer()) {
			@Override
			protected List<Element> getItems(String filter) {
				List<Element> elements = new ArrayList<Element>();

				String name = filter.toLowerCase();
				if (!name.isEmpty()) {
					for (Element element : session.getOrgan().getElements()) {
						if (Elements.getDisplayName(element).toLowerCase()
								.contains(name)) {
							elements.add(element);
						}
					}
				}
				return elements;
			}

			@Override
			protected void onSelectedItem(Element item) {
				onOK();
			}
		};
		setBody(list);

		addOKAction();
		addCancelAction();
	}

	@Override
	public void onOK() {
		session.lookup(ElementSelection.class).setSelectedElement(
				list.getItem());

		super.onOK();
	}

	/**
	 * Show a preference dialog with the given owner.
	 * 
	 * @param owner
	 *            the owner of the dialog
	 */
	public static void show(OrganSession session, Component owner) {

		Window window = getWindow(owner);

		SearchDialog dialog;

		if (window instanceof JFrame) {
			dialog = new SearchDialog(session, (JFrame) window);
		} else if (window instanceof JDialog) {
			dialog = new SearchDialog(session, (JDialog) window);
		} else {
			throw new Error("unable to get window ancestor");
		}

		config.read(dialog);
		dialog.setVisible(true);
		config.write(dialog);
		dialog.dispose();
	}
}