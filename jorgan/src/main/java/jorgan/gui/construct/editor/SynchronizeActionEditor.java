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

import jorgan.disposition.Synchronizer;
import jorgan.disposition.Synchronizer.Action;

/**
 * Property editor for an action property of a <code>Keyable</code>.
 */
public class SynchronizeActionEditor extends PropertyEditorSupport {

	private String[] tags;

	/**
	 * Constructor.
	 */
	public SynchronizeActionEditor() {
		Action[] actions = Synchronizer.Action.values();

		tags = new String[actions.length];
		for (int t = 0; t < tags.length; t++) {
			tags[t] = actions[t].name();
		}
	}

	@Override
	public String[] getTags() {
		return tags;
	}

	@Override
	public String getAsText() {
		return ((Action) getValue()).name();
	}

	@Override
	public void setAsText(String string) {
		setValue(Action.valueOf(string));
	}
}
