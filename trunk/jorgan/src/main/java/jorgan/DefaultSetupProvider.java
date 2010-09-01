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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.LogManager;

import jorgan.spi.SetupProvider;
import jorgan.util.IOUtils;

public class DefaultSetupProvider implements SetupProvider {

	@Override
	public void setup(File home) {
		initLogging(home);
	}

	private void initLogging(File home) {
		File logging = new File(home, "logging.properties");
		if (!logging.exists()) {
			InputStream input = getClass().getResourceAsStream(
					"logging.properties-setup");

			OutputStream output = null;
			try {
				output = new FileOutputStream(logging);

				IOUtils.copy(input, output);
			} catch (Exception ex) {
			} finally {
				IOUtils.closeQuietly(input);
				IOUtils.closeQuietly(output);
			}
		}

		FileInputStream input = null;
		try {
			input = new FileInputStream(logging);
			LogManager.getLogManager().readConfiguration(input);
		} catch (Exception ex) {
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
}