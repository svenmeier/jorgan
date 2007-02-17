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
package jorgan.config.prefs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import jorgan.config.AbstractConfiguration;
import jorgan.midi.log.Configuration;

/**
 * Abstract base class for configurations that can backup to / restore from
 * {@link java.util.prefs.Preferences}.
 */
public abstract class PreferencesConfiguration extends AbstractConfiguration {

	private static Logger logger = Logger.getLogger(Configuration.class
			.getName());

	/**
	 * Constructor.
	 */
	public PreferencesConfiguration() {

		restore(Preferences.userNodeForPackage(getClass()));
	}

	/**
	 * Backup this configuration and all its children.
	 */
	public final void backup() {
		fireConfigurationBackup();

		backup(Preferences.userNodeForPackage(getClass()));
		
		super.backup();
	}

	/**
	 * Backup the values of this configuration. <br>
	 * This default implementation does nothing and should be overridden by
	 * subclasses.
	 * 
	 * @param preferences
	 *            preferences to backup to
	 */
	protected void backup(Preferences preferences) {
	}

	/**
	 * Restore this configuration and all its children.
	 */
	public final void restore() {
		restore(Preferences.userNodeForPackage(getClass()));

		super.restore();
	}

	/**
	 * Restore the values of this configuration. <br>
	 * This default implementation does nothing and should be overridden by
	 * subclasses.
	 * 
	 * @param preferences
	 *            preferences to restore from
	 */
	protected void restore(Preferences preferences) {
	}

	/**
	 * Reset this configuration. <br>
	 * Children of this configuration are not changed.
	 */
	public void reset() {
		restore(new ResetPreferences());
	}

	protected void put(Preferences prefs, String key, String value) {
		if (value == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, value);
		}
	}

	protected void putInt(Preferences prefs, String key, int value) {
		prefs.putInt(key, value);
	}

	protected void putBoolean(Preferences prefs, String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	protected String get(Preferences prefs, String key, String defaultValue) {
		return prefs.get(key, defaultValue);
	}

	protected int getInt(Preferences prefs, String key, int defaultValue) {
		return prefs.getInt(key, defaultValue);
	}

	protected boolean getBoolean(Preferences prefs, String key,
			boolean defaultValue) {
		return prefs.getBoolean(key, defaultValue);
	}

	public static Rectangle getRectangle(Preferences prefs, String key,
			Rectangle def) {
		String rectangle = prefs.get(key, null);
		if (rectangle != null) {
			try {
				StringTokenizer tokens = new StringTokenizer(rectangle, ",");

				int x = Integer.parseInt(tokens.nextToken().trim());
				int y = Integer.parseInt(tokens.nextToken().trim());
				int w = Integer.parseInt(tokens.nextToken().trim());
				int h = Integer.parseInt(tokens.nextToken().trim());

				return new Rectangle(x, y, w, h);
			} catch (Exception ex) {
				logger.log(Level.FINE, "rectangle parsing failed", ex);
			}
		}
		return def;
	}

	public static void putRectangle(Preferences prefs, String key,
			Rectangle rectangle) {
		if (rectangle == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, rectangle.x + ", " + rectangle.y + ", "
					+ rectangle.width + ", " + rectangle.height);
		}
	}

	public static Point getPoint(Preferences prefs, String key, Point def) {
		String point = prefs.get(key, null);
		if (point != null) {
			try {
				StringTokenizer tokens = new StringTokenizer(point, ",");

				int x = Integer.parseInt(tokens.nextToken().trim());
				int y = Integer.parseInt(tokens.nextToken().trim());

				return new Point(x, y);
			} catch (Exception ex) {
				logger.log(Level.FINE, "point parsing failed", ex);
			}
		}
		return def;
	}

	public static void putPoint(Preferences prefs, String key, Point point) {
		if (point == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, point.x + ", " + point.y);
		}
	}

	public static Font getFont(Preferences prefs, String key, Font def) {
		String font = prefs.get(key, null);
		if (font != null) {
			try {
				StringTokenizer tokens = new StringTokenizer(font, ",");

				String name = tokens.nextToken().trim();
				int style = Integer.parseInt(tokens.nextToken().trim());
				int size = Integer.parseInt(tokens.nextToken().trim());

				return new Font(name, style, size);
			} catch (Exception ex) {
				logger.log(Level.FINE, "font parsing failed", ex);
			}
		}
		return def;
	}

	public static void putFont(Preferences prefs, String key, Font font) {
		if (font == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, font.getName() + ", " + font.getStyle() + ", "
					+ font.getSize());
		}
	}

	public static Color getColor(Preferences prefs, String key, Color def) {
		String color = prefs.get(key, null);
		if (color != null) {
			try {
				StringTokenizer tokens = new StringTokenizer(color, ",");

				int r = Integer.parseInt(tokens.nextToken().trim());
				int g = Integer.parseInt(tokens.nextToken().trim());
				int b = Integer.parseInt(tokens.nextToken().trim());

				return new Color(r, g, b);
			} catch (Exception ex) {
				logger.log(Level.FINE, "color parsing failed", ex);
			}
		}
		return def;
	}

	public static void putColor(Preferences prefs, String key, Color color) {
		if (color == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, color.getRed() + ", " + color.getGreen() + ", "
					+ color.getBlue());
		}
	}

	protected static File getFile(Preferences prefs, String key, File def) {
		String file = prefs.get(key, null);
		if (file != null) {
			return new File(file);
		}
		return def;
	}

	protected static void putFile(Preferences prefs, String key, File file) {
		if (file == null) {
			prefs.remove(key);
		} else {
			prefs.put(key, file.getPath());
		}
	}

}