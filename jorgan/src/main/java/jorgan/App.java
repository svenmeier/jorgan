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
import java.util.Collection;

import jorgan.cli.CLI;
import jorgan.gui.GUI;
import jorgan.io.DispositionStream;
import jorgan.spi.ProviderRegistry;
import bias.Configuration;
import bias.store.CLIStore;
import bias.store.DefaultingStore;
import bias.store.PreferencesStore;
import bias.store.PropertiesStore;
import bias.store.ResourceBundlesStore;
import bias.util.cli.ArgsParser;
import bias.util.cli.CLIException;
import bias.util.cli.Option;

/**
 * The jOrgan application.
 */
public class App {

	private static Configuration configuration = Configuration.getRoot().get(
			App.class);

	private boolean openRecentOnStartup = false;

	private boolean headless = false;

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	public void setOpenRecentOnStartup(boolean openRecentOnStartup) {
		this.openRecentOnStartup = openRecentOnStartup;
	}

	public void start(File file) {
		if (file == null && openRecentOnStartup) {
			file = new DispositionStream().getRecentFile();
		}

		UI ui;
		if (headless) {
			ui = new CLI();
		} else {
			ui = new GUI();
		}
		ui.display(file);
	}

	/**
	 * Initialize the configuration.
	 * 
	 * @return the command line options of the configuration
	 */
	private static Collection<Option> initConfiguration() {
		Configuration configuration = Configuration.getRoot();

		configuration
				.addStore(new PropertiesStore(App.class, "app.properties"));
		configuration.addStore(new DefaultingStore(PreferencesStore.user(),
				new PropertiesStore(App.class, "preferences.properties")));
		configuration.addStore(new ResourceBundlesStore("i18n"));

		CLIStore options = ProviderRegistry.getOptions();
		configuration.addStore(options);

		return options.getOptions();
	}

	/**
	 * Main entrance to jOrgan.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		Collection<Option> options = initConfiguration();

		ArgsParser parser = new ArgsParser("java -jar jOrgan.jar",
				"[disposition]", options);
		parser.addOption(parser.new HelpOption());

		String[] operands = null;
		try {
			operands = parser.parse(args);
		} catch (CLIException ex) {
			ex.write();
			System.exit(1);
		}

		File file = null;
		if (operands.length == 1) {
			file = new File(operands[0]);
		} else if (operands.length > 1) {
			parser.writeUsage();
			System.exit(1);
		}

		new Info().log();

		configuration.read(new App()).start(file);

		System.exit(0);
	}
}