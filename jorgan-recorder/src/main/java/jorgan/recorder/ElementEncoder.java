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
package jorgan.recorder;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import bias.Configuration;

/**
 * An encoder of {@link Element}s.
 */
public class ElementEncoder {

	private static Configuration config = Configuration.getRoot().get(
			ElementEncoder.class);

	private boolean name = false;

	private Organ organ;

	public ElementEncoder(Organ organ) {
		this.organ = organ;
		
		config.read(this);
	}

	public String encode(Element element) {
		StringBuilder text = new StringBuilder();

		if (name) {
			text.append(element.getName());
			text.append(" ");
		}
		text.append("[");
		text.append(element.getId());
		text.append("]");

		return text.toString();
	}

	public Element decode(String string) throws IllegalArgumentException {

		long id;

		try {
			int to = string.lastIndexOf(']');
			int from = string.lastIndexOf('[', to);

			id = Long.parseLong(string.substring(from + 1, to));
		} catch (Exception e) {
			throw new IllegalArgumentException(string);
		}

		return organ.getElement(id);
	}
}