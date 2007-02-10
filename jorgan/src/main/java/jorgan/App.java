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

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import jorgan.gui.GUI;
import jorgan.shell.OrganShell;

/**
 * The jOrgan application.
 */
public class App {

	private static Logger logger = Logger.getLogger(App.class.getName());

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

		info();

		UI ui;
		if (arguments.getHeadless()) {
			ui = new OrganShell();
		} else {
			ui = new GUI();
		}
		ui.display(file);

		Configuration.instance().backup();

		System.exit(0);
	}

	private static void info() {
		StringBuffer buffer = new StringBuffer();
		
		info(buffer, "os.arch");
		info(buffer, "os.name");
		info(buffer, "os.version");

		info(buffer, "java.home");
		info(buffer, "java.version");
		info(buffer, "java.runtime.name");
		info(buffer, "java.runtime.version");

		info(buffer, "user.dir");
		info(buffer, "user.home");
		info(buffer, "user.country");
		info(buffer, "user.language");
		info(buffer, "user.name");

		logger.info(buffer.toString());
	}

	private static void info(StringBuffer buffer, String key) {
		buffer.append("\n");
		buffer.append(key);
		buffer.append(" = ");
		buffer.append(System.getProperty(key));
	}
}