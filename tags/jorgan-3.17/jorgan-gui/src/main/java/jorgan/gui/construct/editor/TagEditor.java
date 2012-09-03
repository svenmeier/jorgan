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
import java.util.Arrays;

/**
 * Abstract base class for propertyEditors that support tags.
 */
public abstract class TagEditor extends PropertyEditorSupport {

	private String[] tags;

	@Override
	public final String[] getTags() {
		if (tags == null) {
			tags = withNull(createTags());
		}

		return tags;
	}
	
	@Override
	public String getAsText() {

		return (String) getValue();
	}

	@Override
	public void setAsText(String string) {

		setValue(string);
	}
	
	protected abstract String[] createTags();

	private String[] withNull(String[] items) {
		String[] copy = Arrays.copyOf(items, items.length + 1);
		System.arraycopy(items, 0, copy, 1, items.length);
		copy[0] = null;

		return copy;
	}
}
