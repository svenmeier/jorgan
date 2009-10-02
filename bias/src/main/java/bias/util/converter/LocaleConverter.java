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

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.StringTokenizer;


/**
 * Converter for {@link Locale}s.
 */
public class LocaleConverter implements Converter {

	public Object fromString(String string, Type type) {
		StringTokenizer tokens = new StringTokenizer(string, "_");

		String language = tokens.nextToken();
		if (tokens.hasMoreTokens()) {
			return new Locale(language, tokens.nextToken());
		} else {
			return new Locale(language);
		}
	}

	public String toString(Object object, Type type) {
		return ((Locale)object).toString();
	}
}
