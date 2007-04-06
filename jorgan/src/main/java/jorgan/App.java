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
import java.util.logging.Logger;

import jorgan.gui.GUI;
import jorgan.io.DispositionStream;
import jorgan.shell.OrganShell;
import bias.Bias;
import bias.Store;
import bias.store.CachingStore;
import bias.store.DefaultingStore;
import bias.store.PreferencesStore;
import bias.store.PropertiesStore;

/**
 * The jOrgan application.
 */
public class App {

	private static Logger logger = Logger.getLogger(App.class.getName());

	private static App instance;

	private static Bias bias;

	private String version;

	private boolean openRecentOnStartup = false;

	public void start(Arguments arguments) {
		File file = arguments.getFile();
		if (file == null && openRecentOnStartup) {
			file = new DispositionStream().getRecentFile();
		}

		info();

		UI ui;
		if (arguments.getHeadless()) {
			ui = new OrganShell();
		} else {
			ui = new GUI();
		}
		ui.display(file);
	}

	private void info() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("jOrgan " + version);
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

	private void info(StringBuffer buffer, String key) {
		buffer.append("\n");
		buffer.append(key);
		buffer.append(" = ");
		buffer.append(System.getProperty(key));
	}

	public boolean getOpenRecentOnStartup() {
		return openRecentOnStartup;
	}

	public void setOpenRecentOnStartup(boolean openRecentOnStartup) {
		this.openRecentOnStartup = openRecentOnStartup;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get the current version of jOrgan.
	 * 
	 * @return the current version
	 */
	public String getVersion() {
		return version;
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

		Store store = new DefaultingStore(new CachingStore(PreferencesStore
				.user()), new CachingStore(new PropertiesStore(App.class,
				"app.properties")));
		bias = new Bias(store);

		instance = new App();
		App.getBias().register(instance);
		instance.start(arguments);

		System.exit(0);
	}

	public static App getInstance() {
		return instance;
	}

	public static Bias getBias() {
		return bias;
	}
}