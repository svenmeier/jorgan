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
package jorgan.skin;

import java.util.List;

/**
 * Style.
 */
public class Style extends CompositeLayer implements Cloneable {

	private String name = "";

	public void setName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("name cannot be null");
		}
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public Object clone() {
		Style clone = (Style) super.clone();

		return clone;
	}

	public void updateBinding(String name) {
		updateBinding(this, name);
	}

	private void updateBinding(Layer layer, String name) {
		if (name.equals(layer.getBinding())) {
			layer.setView(view);
		}

		if (layer instanceof CompositeLayer) {
			List<Layer> children = ((CompositeLayer) layer).getChildren();

			for (Layer child : children) {
				updateBinding(child, name);
			}
		}
	}

}