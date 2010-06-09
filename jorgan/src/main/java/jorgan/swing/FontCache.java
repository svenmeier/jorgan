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
package jorgan.swing;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache of fonts.
 */
public class FontCache {

	private static Map<String, Reference<Font>> fonts = new HashMap<String, Reference<Font>>();

	private static Font get(String key) {
		Reference<Font> reference = fonts.get(key);
		if (reference != null) {
			return reference.get();
		}
		return null;
	}

	private static void put(String key, Font font) {
		fonts.put(key, new SoftReference<Font>(font));
	}

	/**
	 * Flush all cached fonts.
	 */
	public static void flush() {
		fonts.clear();
	}

	public static Font getFont(URL url, int style, float size) {
		String key = "" + style + ":" + size + ":" + url;

		Font font = get(key);
		if (font == null) {
			font = deriveFont(getFont(url), style, size);
			put(key, font);
		}

		return font;
	}

	private static Font deriveFont(Font font, int style, float size) {
		return font.deriveFont(style, size);
	}

	private static Font getFont(URL url) {
		String key = url.toString();

		Font font = get(key);
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

			put(key, font);
		}

		return font;
	}
}