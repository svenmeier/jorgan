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

/**
 * A cross-link between objects in a tree.
 */
public interface CrossLink {

	/**
	 * Is the given object cross-linked from other objects.
	 * 
	 * @param object
	 * @return
	 */
	public boolean isLinked(Object object);

	/**
	 * Is the reference from the given parent to a child a cross-link.
	 * 
	 * @param parent
	 * @param child
	 * @return
	 */
	public boolean isLink(Object parent, Object child);

	/**
	 * Cross-link the parent with the given child.
	 * 
	 * @param parent
	 * @param child
	 */
	public void link(Object parent, Object child);
}
