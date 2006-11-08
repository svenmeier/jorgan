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

import java.io.*;
import java.util.Properties;

import jorgan.gui.*;
import jorgan.shell.*;

/**
 * The jOrgan application.
 */
public class App {

	private static Properties properties = new Properties();

	static {
		try {
			properties.load(App.class.getResourceAsStream("app.properties"));
		} catch (IOException ex) {
			throw new Error(ex);
		}
	}

	/**
	 * Get the current version of jOrgan.
	 * 
	 * @return the current version
	 */
	public static String getVersion() {
		return properties.getProperty("jorgan.version");
	}

	/**
	 * Main entrance to jOrgan.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		Arguments arguments = new Arguments();
		if (!arguments.parse(args)) {
			arguments.printUsage();
			System.exit(1);
		}

		File file = arguments.getFile();
		if (file == null
				&& jorgan.io.Configuration.instance().getRecentOpenOnStartup()) {
			file = jorgan.io.Configuration.instance().getRecentFile();
		}

		UI ui;
		if (arguments.getHeadless()) {
			ui = new OrganShell();
		} else {
			ui = new OrganFrame();
		}
		ui.start(file);

		Configuration.instance().backup();

		System.exit(0);
	}
}