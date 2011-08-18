/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 - 2010 Sven Meier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jorgan;

import java.io.File;
import java.util.Locale;

import jorgan.cli.spi.OptionRegistry;
import jorgan.session.History;
import jorgan.spi.ConfigurationRegistry;
import jorgan.spi.UIRegistry;
import jorgan.util.LocaleUtils;
import bias.Configuration;
import bias.util.cli.ArgsParser;
import bias.util.cli.CLIException;

/**
 * The jOrgan application.
 */
public class App {

	private static Configuration configuration = Configuration.getRoot().get(
			App.class);

	private Locale locale;

	private boolean openRecentOnStartup = false;

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setOpenRecentOnStartup(boolean openRecentOnStartup) {
		this.openRecentOnStartup = openRecentOnStartup;
	}

	public void start(File file) {
		if (locale != null) {
			LocaleUtils.setLocale(locale);
		}

		if (file == null && openRecentOnStartup) {
			file = new History().getRecentFile();
		}

		UI ui = UIRegistry.getUI();
		if (ui == null) {
			System.out.println("no user interface available");
			System.exit(1);
		}
		ui.display(file);
	}

	/**
	 * Main entrance to jOrgan.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		ConfigurationRegistry.init();

		ArgsParser parser = new ArgsParser("java -jar jOrgan.jar",
				"[disposition]", OptionRegistry.getOptions());
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