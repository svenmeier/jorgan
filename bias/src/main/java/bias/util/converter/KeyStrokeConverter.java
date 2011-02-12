/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util.converter;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.lang.reflect.Type;

import javax.swing.KeyStroke;

/**
 * Converter for {@link KeyStroke}s. <br>
 * Supports a custom <code>shortcut</code> modifier, which is replaced with the
 * system's menu shortcut key.
 * 
 * @see Toolkit#getMenuShortcutKeyMask()
 */
public class KeyStrokeConverter implements Converter {

	private static String shortcutReplacement;

	/**
	 * Do lazily as AWT might be headless and this converter never used.
	 */
	private static void init() {
		if (shortcutReplacement == null) {
			int shortcutKeyMask = Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask();

			String stroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
					shortcutKeyMask).toString();
			int space = stroke.indexOf(' ');

			shortcutReplacement = stroke.substring(0, space);
		}
	}

	public String toString(Object object, Type type) {
		KeyStroke keyStroke = (KeyStroke) object;

		return keyStroke.toString();
	}

	public Object fromString(String string, Type type) {
		init();

		string = string.replace("shortcut", shortcutReplacement);

		return KeyStroke.getKeyStroke(string);
	}
}
