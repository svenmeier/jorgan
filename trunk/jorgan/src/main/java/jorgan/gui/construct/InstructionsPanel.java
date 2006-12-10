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

import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;

import swingx.docking.DockedPanel;

import jorgan.docs.Documents;
import jorgan.gui.OrganSession;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.beans.PropertiesPanel;

/**
 * Panel for instructions for the currently selected element.
 */
public class InstructionsPanel extends DockedPanel {

	private static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

	private JEditorPane editor = new JEditorPane();

	/**
	 * The handler of selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private OrganSession session;

	/**
	 * Constructor.
	 */
	public InstructionsPanel() {
		addHierarchyListener(selectionHandler);

		editor.setEditable(false);
		editor.setMargin(new Insets(0, 0, 0, 0));
		editor.setContentType("text/html");
		editor.setVisible(false);

		setScrollableBody(editor, true, false);
	}

	/**
	 * Set the organ.
	 * 
	 * @param session
	 *            organ session
	 */
	public void setOrgan(OrganSession session) {
		if (this.session != null) {
			this.session.getSelectionModel().removeSelectionListener(
					selectionHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getSelectionModel().addSelectionListener(
					selectionHandler);
		}
	}

	protected void updateInstructions(Class clazz, String property) {
		if (clazz == null) {
			editor.setVisible(false);
			setMessage(null);
		} else {
			try {
				URL url;
				if (property == null) {
					url = Documents.getInstance().getInstructions(clazz);
				} else {
					url = Documents.getInstance().getInstructions(clazz,
							property);
				}
				editor.setPage(url);

				editor.setVisible(true);

				StringBuffer buffer = new StringBuffer();
				buffer.append(Documents.getInstance().getDisplayName(clazz));
				if (property != null) {
					buffer.append(" - ");
					buffer.append(Documents.getInstance().getDisplayName(clazz,
							property));
				}
				setMessage(buffer.toString());
			} catch (Exception ex) {
				editor.setVisible(false);
				setMessage(resources
						.getString("construct.instructions.failure"));
			}
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements ElementSelectionListener,
			HierarchyListener {

		private Class clazz;

		private String property;

		public void selectionChanged(ElementSelectionEvent ev) {
			if (session.getSelectionModel().isElementSelected()) {
				clazz = PropertiesPanel.getCommonClass(session
						.getSelectionModel().getSelectedElements());
				property = session.getSelectionModel().getSelectedProperty();
			} else {
				clazz = null;
				property = null;
			}

			if (isShowing()) {
				flush();
			}
		}

		public void hierarchyChanged(HierarchyEvent e) {
			if (clazz != null && isShowing()) {
				flush();
			}
		}

		protected void flush() {
			updateInstructions(clazz, property);
			this.clazz = null;
			this.property = null;
		}
	}
}