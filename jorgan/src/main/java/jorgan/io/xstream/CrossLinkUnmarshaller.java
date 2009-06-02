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
package jorgan.io.xstream;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.mapper.Mapper;

public class CrossLinkUnmarshaller extends TreeUnmarshaller {

	private CrossLink crossLink;
	
	private Map<Object, Object> values = new HashMap<Object, Object>();

	public CrossLinkUnmarshaller(Object root, HierarchicalStreamReader reader,
			ConverterLookup converterLookup, Mapper mapper, CrossLink crossLink) {
		super(root, reader, converterLookup, mapper);
		
		this.crossLink = crossLink;
	}

	protected Object convert(final Object parent, Class type,
			Converter converter) {
		String attributeName = getMapper().aliasForSystemAttribute("reference");

		final String reference = attributeName == null ? null : reader
				.getAttribute(attributeName);
		if (reference != null) {
			addCompletionCallback(new Runnable() {
				public void run() {
					Object value = values.get(reference);

					if (value == null) {
						final ConversionException ex = new ConversionException(
								"Invalid reference");
						ex.add("reference", reference);
						throw ex;
					}

					crossLink.crossLink(parent, value);
				}
			}, 0);

			return null;
		} else {
			attributeName = getMapper().aliasForSystemAttribute("id");
			final String id = attributeName == null ? null : reader
					.getAttribute(attributeName);

			Object item = super.convert(parent, type, converter);
			if (id != null) {
				values.put(id, item);
			}

			return item;
		}
	}
}
