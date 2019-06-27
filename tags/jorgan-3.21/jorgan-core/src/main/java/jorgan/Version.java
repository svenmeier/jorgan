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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version {

	private static Logger logger = Logger.getLogger(Version.class.getName());

	private static Pattern compatible = Pattern.compile("^\\d+(\\.\\d+)?");

	private String version;

	public Version() {
	}

	Version(String version) {
		this.version = version;
	}

	public String get() {
		if (version == null) {
			version = getClass().getPackage().getImplementationVersion();

			if (version == null) {
				version = "development";
			}
		}
		return version;
	}

	public String getCompatible() {
		return getCompatible(get());
	}

	public boolean isCompatible(String version) {
		return getCompatible().equals(getCompatible(version));
	}

	/**
	 * Get the compatible part of a version, i.e. everything before the second
	 * dot.
	 */
	private String getCompatible(final String string) {

		Matcher matcher = compatible.matcher(string);
		if (matcher.find()) {
			return matcher.group();
		} else {
			return string;
		}
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