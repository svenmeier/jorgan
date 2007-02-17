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

/**
 * Arguments of jOrgan.
 */
public class Arguments {

	private boolean headless = false;

	private File file;

	/**
	 * Parse arguments.
	 * 
	 * @param args
	 *            command line arguments to parse
	 * @return <code>true</code if parsing was successfull
	 */
	public boolean parse(String[] args) {

		for (String arg : args) {
			if ("-headless".equals(arg)) {
				headless = true;
				continue;
			}
			if (!(arg.startsWith("-"))) {
				file = new File(arg);
				continue;
			}
			return false;
		}

		return true;
	}

	/**
	 * Print the usage.
	 */
	public void printUsage() {
		System.out
				.println("Usage: java -jar jOrgan.jar [-headless] disposition");
		System.out.println("  disposition   disposition to load on startup");
		System.out.println("  -headless     start without GUI");
	}

	/**
	 * Get the file argument.
	 * 
	 * @return file argument
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Get the headless argument.
	 * 
	 * @return headless argument
	 */
	public boolean getHeadless() {
		return headless;
	}
}