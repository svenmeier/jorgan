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

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class PackageStrippingMapper extends MapperWrapper {

	private final String PREFIX = "jorgan.disposition";

	private Mapper wrapped;

	public PackageStrippingMapper(Mapper wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	public String serializedClass(Class type) {
		if (type.getPackage().getName().startsWith(PREFIX)) {
			return fromClass(type.getName().substring(PREFIX.length() + 1));
		} else {
			return wrapped.serializedClass(type);
		}
	}

	private String fromClass(String name) {

		StringBuffer buffer = new StringBuffer(name.length());
		int index = 0;
		while (true) {
			buffer.append(Character.toLowerCase(name.charAt(index)));
			
			int next = name.indexOf('$', index);
			if (next == -1) {
				buffer.append(name.substring(index + 1));
				break;
			}
			
			buffer.append(name.substring(index + 1, next));
			buffer.append('-');
			index = next + 1;
		}
		
		return buffer.toString();
	}
	
	public Class realClass(String name) {
		String temp = PREFIX + '.' + toClass(name);

		try {
			return wrapped.realClass(temp.replace('-', '$'));
		} catch (Exception ex) {
			return wrapped.realClass(name);
		}
	}
	
	private String toClass(String name) {
		StringBuffer buffer = new StringBuffer(name.length());

		int index = 0;
		while (true) {
			buffer.append(Character.toUpperCase(name.charAt(index)));
			
			int next = name.indexOf('-', index);
			if (next == -1) {
				buffer.append(name.substring(index + 1));
				break;
			}
			
			buffer.append(name.substring(index + 1, next));
			buffer.append('$');
			index = next + 1;
		}
		
		return buffer.toString();
	}
}