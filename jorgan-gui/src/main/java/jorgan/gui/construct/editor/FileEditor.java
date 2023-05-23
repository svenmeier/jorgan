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
package jorgan.gui.construct.editor;

import java.awt.Component;
import java.io.File;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import jorgan.disposition.Element;
import jorgan.session.OrganSession;
import jorgan.swing.FileSelector;

/**
 * PropertyEditor for a file property.
 */
public class FileEditor extends CustomEditor implements ElementAwareEditor {

	private OrganSession session;

	private Element element;

	public FileEditor() {
	}

	@Override
	public void setElement(OrganSession session, Element element) {
		this.session = session;
		this.element = element;
	}

	private FileSelector field = new FileSelector(
			FileSelector.FILES_AND_DIRECTORIES) {
		protected JTextField createTextField() {
			JTextField textField = super.createTextField();
			textField.setBorder(new EmptyBorder(0, 0, 0, 0));
			return textField;
		}

		@Override
		protected File toChooser(File file) {
			if (session == null || file == null) {
				return file;
			}
			return session.resolve(file.getPath());
		}

		@Override
		protected File fromChooser(File file) {
			if (session == null) {
				return file;
			}
			return new File(session.deresolve(file));
		}
	};

	@Override
	public Component getCustomEditor(Object value) {

		if (value == null) {
			field.setSelectedFile(null);
		} else {
			field.setSelectedFile(new File((String) value));
		}

		return field;
	}

	@Override
	protected Object getEditedValue() {

		File file = field.getSelectedFile();
		if (file == null) {
			return null;
		} else {
			return file.getPath();
		}
	}

	@Override
	protected String format(Object value) {

		if (value == null) {
			return "";
		} else {
			return value.toString();
		}
	}
}