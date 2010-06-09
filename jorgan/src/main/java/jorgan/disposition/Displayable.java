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
package jorgan.disposition;

import jorgan.util.Null;

public abstract class Displayable extends Element {

	/**
	 * The maximum supported zoom.
	 */
	public static final float MAX_ZOOM = 5.0f;

	/**
	 * The minimum supported zoom.
	 */
	public static final float MIN_ZOOM = 0.5f;

	private String style;

	/**
	 * The zoom.
	 */
	private float zoom = 1.0f;

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style
	 */
	public void setStyle(String style) {
		if (!Null.safeEquals(this.style, style)) {
			String oldStyle = this.style;

			this.style = style;

			fireChange(new PropertyChange(oldStyle, style));
		}
	}

	public float getZoom() {
		if (zoom < 0.5f) {
			// maybe old zoom
			zoom = 0.5f;
		}

		return zoom;
	}

	public void setZoom(float zoom) {
		if (zoom != this.zoom) {
			float oldZoom = this.zoom;

			if (zoom < MIN_ZOOM) {
				zoom = MIN_ZOOM;
			}
			if (zoom > MAX_ZOOM) {
				zoom = MAX_ZOOM;
			}

			this.zoom = zoom;

			fireChange(new PropertyChange(oldZoom, this.zoom));
		}
	}
}