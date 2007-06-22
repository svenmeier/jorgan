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
package jorgan.gui.console;

import jorgan.disposition.Memory;
import jorgan.skin.TextLayer;

/**
 * A view that shows a {@link Memory}.
 */
public class MemoryView extends ContinuousView<Memory> {

	/**
	 * The key of the {@link Memory#getTitle()} text for {@link TextLayer}s.
	 */
	public static final String TEXT_TITLE = "title";

	/**
	 * Constructor.
	 * 
	 * @param memory
	 *            memory to view
	 */
	public MemoryView(Memory memory) {
		super(memory);
	}

	protected void initTexts() {
		super.initTexts();

		setText(TEXT_TITLE, getElement().getTitle());
	}
}