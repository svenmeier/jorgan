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
import java.beans.PropertyEditorSupport;

/**
 * Abstract base class for propertyEditors that support a custom editor.
 */
public abstract class CustomEditor extends PropertyEditorSupport {

	@Override
	public final String getAsText() {
		return format(super.getValue());
	}

	@Override
	public final boolean supportsCustomEditor() {
		return true;
	}

	@Override
	public final Component getCustomEditor() {

		return getCustomEditor(super.getValue());
	}

	/**
	 * Get the custom editor initialized with the given value.
	 * 
	 * @param value
	 *            the value to get editor for
	 * @return editor
	 */
	protected abstract Component getCustomEditor(Object value);

	@Override
	public final Object getValue() {

		Object editedValue = getEditedValue();
		if (editedValue == null && super.getValue() != null
				|| editedValue != null && !editedValue.equals(super.getValue())) {
			setValue(editedValue);
		}

		return super.getValue();
	}

	/**
	 * Get the edited value from the editor.
	 * 
	 * @return edited value
	 */
	protected abstract Object getEditedValue();

	/**
	 * Format the given value to be displayed as text.
	 * 
	 * @param value
	 *            value to format
	 * @return formatted value
	 * @see #getAsText()
	 */
	protected abstract String format(Object value);
}
