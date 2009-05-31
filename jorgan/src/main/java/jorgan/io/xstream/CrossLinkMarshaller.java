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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.SequenceGenerator;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.ReferenceByIdMarshaller.IDGenerator;
import com.thoughtworks.xstream.core.util.FastStack;
import com.thoughtworks.xstream.core.util.ObjectIdDictionary;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class CrossLinkMarshaller extends TreeMarshaller {

	private final IDGenerator idGenerator;

	private ObjectIdDictionary references = new ObjectIdDictionary();

	private FastStack parentStack = new FastStack(16);

	public CrossLinkMarshaller(HierarchicalStreamWriter writer,
			ConverterLookup converterLookup, Mapper mapper) {
		this(writer, converterLookup, mapper, new SequenceGenerator(1));
	}

	public CrossLinkMarshaller(HierarchicalStreamWriter writer,
			ConverterLookup converterLookup, Mapper mapper,
			IDGenerator idGenerator) {
		super(writer, converterLookup, mapper);

		this.idGenerator = idGenerator;
	}

	public void convert(Object item, Converter converter) {
		if (getMapper().isImmutableValueType(item.getClass())) {
			// strings, ints, dates, etc... don't bother using references.
			converter.marshal(item, writer, this);
		} else {
			if (isCrossLink(parentStack.peek(), item)) {
				String reference = getId(item);

				String attributeName = getMapper().aliasForSystemAttribute(
						"reference");
				if (attributeName != null) {
					writer.addAttribute(attributeName, reference);
				}
			} else {
				if (isCrossLinked(item)) {
					String id = getId(item);

					String attributeName = getMapper().aliasForSystemAttribute(
							"id");
					if (attributeName != null) {
						writer.addAttribute(attributeName, id);
					}
				}

				parentStack.push(item);
				try {
					super.convert(item, converter);
				} finally {
					parentStack.pop();
				}
			}
		}
	}

	protected boolean isCrossLinked(Object object) {
		return false;
	}

	protected boolean isCrossLink(Object parent, Object object) {
		return false;
	}

	private String getId(Object item) {
		Object referenceKey = references.lookupId(item);
		if (referenceKey == null) {
			referenceKey = idGenerator.next(item);
			references.associateId(item, referenceKey);
		}
		return referenceKey.toString();
	}
}
