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

import java.util.Map;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.IndexedContinuous;
import jorgan.disposition.Regulator;

/**
 * A view that shows an {@link IndexedContinuous}.
 */
public class RegulatorView extends IndexedContinuousView<Regulator> {

	/**
	 * Constructor.
	 * 
	 * @param memory
	 *            memory to view
	 */
	public RegulatorView(Regulator element) {
		super(element);
	}

	@Override
	public void update(String name) {
		if ("index".equals(name)) {
			updateBinding(BINDING_TITLE);
		}

		super.update(name);
	}

	protected String getTitle(int index) {
		Element referrenced = getElement().getReference(index).getElement();

		Map<String, String> texts = referrenced.getTexts();
		String name = texts.get(BINDING_NAME);
		if (name == null) {
			name = Elements.getDisplayName(referrenced);
		}
		return name;
	}
}