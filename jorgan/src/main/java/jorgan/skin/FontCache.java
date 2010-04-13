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

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache of fonts.
 */
public class FontCache {

	private static Map<URL, Font> fonts = new HashMap<URL, Font>();

	/**
	 * Flush all cached fonts.
	 */
	public static void flush() {
		fonts.clear();
	}

	/**
	 * Get an image for the given URL.
	 * 
	 * @param url
	 *            url to get image for
	 * @return image
	 */
	public static Font getFont(URL url) {
		if (url == null) {
			throw new IllegalArgumentException("url must not be null");
		}

		Font font = fonts.get(url);
		if (font == null) {
			try {
				InputStream input = url.openStream();
				try {
					font = Font.createFont(Font.TRUETYPE_FONT, input);
				} finally {
					try {
						input.close();
					} catch (IOException ignore) {
					}
				}
			} catch (Exception e) {
			}

			fonts.put(url, font);
		}

		return font;
	}
}