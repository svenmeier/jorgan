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
 * Property editor for an action property of a <code>Keyable</code>.
 */
public class ActionEditor extends PropertyEditorSupport {

	private static Configuration config = Configuration.getRoot().get(
			ActionEditor.class);

	private String[] tags;

	/**
	 * Constructor.
	 */
	public ActionEditor() {
		tags = new String[] { i18n.getString("straight"),
				i18n.getString("constant"), i18n.getString("highest"),
				i18n.getString("lowest"), i18n.getString("sustain"),
				i18n.getString("sostenuto"), i18n.getString("inverse") };
	}

	public String[] getTags() {
		return tags;
	}

	public String getAsText() {

		Integer value = (Integer) getValue();
		if (value == null) {
			return "";
		} else {
			return tags[value.intValue()];
		}
	}

	public void setAsText(String string) {

		for (int t = 0; t < tags.length; t++) {
			if (tags[t].equals(string)) {
				setValue(new Integer(t));
				return;
			}
		}
		throw new IllegalArgumentException("unkown action");
	}
}
