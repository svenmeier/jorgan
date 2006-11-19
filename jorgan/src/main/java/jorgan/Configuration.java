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
package jorgan;

// jorgan
import jorgan.config.prefs.*;

/**
 * Configuration of the main package.
 */
public class Configuration extends PreferencesConfiguration {

	private static Configuration sharedInstance = new Configuration();

	private Configuration() {
		addChild(jorgan.io.Configuration.instance());
		addChild(jorgan.gui.Configuration.instance());
		addChild(jorgan.play.Configuration.instance());
		addChild(jorgan.midi.Configuration.instance());
		addChild(jorgan.shell.Configuration.instance());
	}

	/**
	 * Get the shared configuration.
	 * 
	 * @return configuration
	 */
	public static Configuration instance() {
		return sharedInstance;
	}
}