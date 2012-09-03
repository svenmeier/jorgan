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
package bias.util.converter;

import java.awt.Font;
import java.lang.reflect.Type;
import java.util.StringTokenizer;



/**
 * Converter for {@link Font}s.
 */
public class FontConverter implements Converter {

	public String toString(Object object, Type type) {
		Font font = (Font) object;

		StringBuffer buffer = new StringBuffer();

		buffer.append(font.getName());
		buffer.append(",");
		buffer.append(font.getStyle());
		buffer.append(",");
		buffer.append(font.getSize());

		return buffer.toString();
	}

	public Object fromString(String string, Type type) {
		StringTokenizer tokens = new StringTokenizer(string, ",");

		return new Font(tokens.nextToken(), Integer
				.parseInt(tokens.nextToken()), Integer.parseInt(tokens
				.nextToken()));
	}
}
