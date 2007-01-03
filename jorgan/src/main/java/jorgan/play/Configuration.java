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
package jorgan.play;

import java.util.prefs.Preferences;

import jorgan.config.prefs.PreferencesConfiguration;

/**
 * Configuration of the play package.
 */
public class Configuration extends PreferencesConfiguration {

	private static final boolean WARN_WITHOUT_DEVICE = true;

	private static final boolean WARN_WITHOUT_MESSAGE = false;

	private static final boolean RELEASE_DEVICES_WHEN_DEACTIVATED = false;

	private static Configuration sharedInstance = new Configuration();

	private boolean warnWithoutDevice;

	private boolean warnWithoutMessage;

	protected void restore(Preferences prefs) {
		warnWithoutDevice = getBoolean(prefs, "warnWithoutDevice",
				WARN_WITHOUT_DEVICE);
		warnWithoutMessage = getBoolean(prefs, "warnWithoutMessage",
				WARN_WITHOUT_MESSAGE);
	}

	protected void backup(Preferences prefs) {
		putBoolean(prefs, "warnWithoutDevice", warnWithoutDevice);
		putBoolean(prefs, "warnWithoutMessage", warnWithoutMessage);
	}

	public boolean getWarnWithoutDevice() {
		return warnWithoutDevice;
	}

	public boolean getWarnWithoutMessage() {
		return warnWithoutMessage;
	}

	public void setWarnWithoutDevice(boolean warnWithoutDevice) {
		this.warnWithoutDevice = warnWithoutDevice;

		fireConfigurationChanged();
	}

	public void setWarnWithoutMessage(boolean warnWithoutMessage) {
		this.warnWithoutMessage = warnWithoutMessage;

		fireConfigurationChanged();
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