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

import java.util.logging.Logger;

import bias.Configuration;

public class Info {

	private static Configuration config = Configuration.getRoot().get(
			Info.class);

	private static Logger logger = Logger.getLogger(Info.class.getName());

	private String version = "";

	public Info() {
		config.read(this);
	}

	public String getVersion() {
		return version;
	}

	public void log() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("jOrgan " + version);
		appendProperty(buffer, "os.arch");
		appendProperty(buffer, "os.name");
		appendProperty(buffer, "os.version");

		appendProperty(buffer, "java.home");
		appendProperty(buffer, "java.version");
		appendProperty(buffer, "java.runtime.name");
		appendProperty(buffer, "java.runtime.version");

		appendProperty(buffer, "user.dir");
		appendProperty(buffer, "user.home");
		appendProperty(buffer, "user.country");
		appendProperty(buffer, "user.language");
		appendProperty(buffer, "user.name");

		logger.info(buffer.toString());
	}

	private void appendProperty(StringBuffer buffer, String key) {
		buffer.append("\n");
		buffer.append(key);
		buffer.append(" = ");
		buffer.append(System.getProperty(key));
	}
}