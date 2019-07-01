/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util.cli;

/**
 * An option.
 */
public abstract class Option {

	private char name;

	private String longName;

	private String description;

	protected Option(char name) {
		this.name = name;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getName() {
		return name;
	}

	public String getLongName() {
		return longName;
	}

	public String getDescription() {
		return description;
	}

	protected String getDisplayName() {
		if (name != (char) 0) {
			return "-" + name;
		} else {
			return "--" + longName;
		}
	}

	public int parse(String arg, int from) throws CLIException {

		if (name != (char) 0) {
			if (arg.charAt(from) == name) {
				from = parsed(arg, from + 1);
			}
		}

		return from;
	}

	public int parseLong(String arg, int from) throws CLIException {
		int to = from;

		if (longName != null) {
			int index = arg.indexOf('=', from);
			if (index == -1) {
				index = arg.length();
			}
			if (longName.equals(arg.substring(from, index))) {
				to = parsed(arg, Math.min(index + 1, arg.length()));

				if (to != arg.length()) {
					throw new CLIException("option '" + getDisplayName()
							+ "' does not allow an argument");
				}
			}
		}

		return to;
	}

	protected abstract int parsed(String arg, int to) throws CLIException;

	public String getSyntax() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("[");
		if (name != (char) 0) {
			buffer.append("-");
			buffer.append(name);
		} else {
			buffer.append("--");
			buffer.append(longName);
		}
		buffer.append("]");

		return buffer.toString();
	}

	public String getLongSyntax() {
		StringBuffer buffer = new StringBuffer();

		if (name != (char) 0) {
			buffer.append("-");
			buffer.append(name);
		}
		if (name != (char) 0 && longName != null) {
			buffer.append(", ");
		}
		if (longName != null) {
			buffer.append("--");
			buffer.append(longName);
		}

		return buffer.toString();
	}
}