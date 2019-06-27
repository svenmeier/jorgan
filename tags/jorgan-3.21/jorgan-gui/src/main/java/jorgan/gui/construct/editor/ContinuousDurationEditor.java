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

import java.beans.PropertyEditorSupport;

import bias.Configuration;

/**
 * Property editor for a boolean property.
 */
public class ContinuousDurationEditor extends PropertyEditorSupport {

	private static Configuration config = Configuration.getRoot().get(
			ContinuousDurationEditor.class);

	private String[] tags = { "-1", "0" };

	/**
	 * Constructor.
	 */
	public ContinuousDurationEditor() {
		config.read(this);
	}

	public void setTags(String[] tags) {
		if (tags.length != this.tags.length) {
			throw new IllegalArgumentException();
		}

		this.tags = tags;
	}

	@Override
	public String[] getTags() {
		return tags;
	}

	@Override
	public String getAsText() {

		Integer value = (Integer) getValue();
		if (value == null) {
			return "";
		}

		if (value <= 0) {
			return tags[value + 1];
		}

		return Integer.toString(value);
	}

	@Override
	public void setAsText(String string) {

		for (int i = 0; i < tags.length; i++) {
			if (string.equals(tags[i])) {
				setValue(i - 1);
				return;
			}
		}

		setValue(Integer.parseInt(string));
	}
}
