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

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.AbstractTreeMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.core.TreeMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * A {@link TreeMarshallingStrategy} handling {@link CrossLink}s.
 */
public class CrossLinkMarshallingStrategy extends
		AbstractTreeMarshallingStrategy implements CrossLink {

	private List<CrossLink> crosslinks = new ArrayList<CrossLink>();

	/**
	 * Register a {@link CrossLink}.
	 * 
	 * @param crosslink
	 */
	public void register(CrossLink crosslink) {
		crosslinks.add(crosslink);
	}

	protected TreeUnmarshaller createUnmarshallingContext(Object root,
			HierarchicalStreamReader reader, ConverterLookup converterLookup,
			Mapper mapper) {
		return new CrossLinkUnmarshaller(root, reader, converterLookup, mapper,
				this);
	}

	protected TreeMarshaller createMarshallingContext(
			HierarchicalStreamWriter writer, ConverterLookup converterLookup,
			Mapper mapper) {
		return new CrossLinkMarshaller(writer, converterLookup, mapper, this);
	}

	public boolean isCrossLinked(Object object) {
		for (CrossLink crosslink : crosslinks) {
			if (crosslink.isCrossLinked(object)) {
				return true;
			}
		}

		return false;
	}

	public boolean isCrossLink(Object parent, Object child) {
		for (CrossLink crosslink : crosslinks) {
			if (crosslink.isCrossLink(parent, child)) {
				return true;
			}
		}

		return false;
	}

	public void crossLink(Object parent, Object child) {
		for (CrossLink crosslink : crosslinks) {
			if (crosslink.isCrossLink(parent, child)) {
				crosslink.crossLink(parent, child);
				return;
			}
		}
		throw new ConversionException("unable to cross-link");
	}
}
