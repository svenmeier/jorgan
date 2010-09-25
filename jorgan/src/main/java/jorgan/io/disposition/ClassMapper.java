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
package jorgan.io.disposition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class ClassMapper extends MapperWrapper {

	private final Pattern pattern = Pattern
			.compile("jorgan\\.(.*\\.)?disposition\\.");

	public ClassMapper(Mapper wrapped) {
		super(wrapped);
	}

	@SuppressWarnings("rawtypes")
	public String serializedClass(Class type) {
		String name = type.getName();

		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			StringBuffer buffer = new StringBuffer();
			String group = matcher.group(1);
			if (group != null) {
				buffer.append(group);
			}

			int index = matcher.end();
			while (true) {
				buffer.append(Character.toLowerCase(name.charAt(index)));
				index++;

				int next = name.indexOf("$", index);
				if (next == -1) {
					buffer.append(name.substring(index, name.length()));
					break;
				} else {
					buffer.append(name.substring(index, next));
					buffer.append("$");
					index = next + 1;
				}
			}

			return buffer.toString();
		} else {
			return super.serializedClass(type);
		}
	}

	@SuppressWarnings("rawtypes")
	public Class realClass(String name) {
		RuntimeException exception;

		try {
			return super.realClass(name);
		} catch (RuntimeException ex) {
			exception = ex;
		}

		String extension = null;

		StringBuffer buffer = new StringBuffer("jorgan.");

		int index = name.indexOf('.');
		if (index == -1) {
			index = 0;
		} else {
			extension = name.substring(0, index);
			index++;
			buffer.append(extension);
			buffer.append(".");
		}
		buffer.append("disposition.");

		while (true) {
			buffer.append(Character.toUpperCase(name.charAt(index)));
			index++;

			int next = name.indexOf("$", index);
			if (next == -1) {
				buffer.append(name.substring(index, name.length()));
				break;
			} else {
				buffer.append(name.substring(index, next));
				buffer.append("$");
				index = next + 1;
			}
		}

		try {
			return Class.forName(buffer.toString());
		} catch (ClassNotFoundException e) {
			if (extension == null) {
				throw exception;
			} else {
				throw new ExtensionException(extension);
			}
		}
	}
}