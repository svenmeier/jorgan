package jorgan;

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

import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.bootstrap.Classpath;
import jorgan.bootstrap.Exceptions;
import jorgan.bootstrap.Logging;
import jorgan.bootstrap.Main;

/**
 * Bootstrapping for {@link App}.
 */
public class Bootstrap {

	private static Logger logger = Logger.getLogger(Bootstrap.class.getName());

	public void start(final String[] args) {
		try {
			new Logging();
			new Exceptions(logger);
			new Classpath("lib");
			new Main("jorgan.App", args);
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "bootstrapping failed", t);
		}
	}

	public static void main(final String[] args) {

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.start(args);
	}
}