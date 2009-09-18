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
package jorgan.session;

import java.io.File;
import java.io.IOException;

/**
 * A listener of a {@link OrganSession}.
 */
public interface SessionListener {

	/**
	 * @see OrganSession#setConstructing(boolean)
	 */
	public void constructingChanged(boolean constructing);
	
	/**
	 * @see {@link OrganSession#markModified()}
	 */
	public void modified();
	
	/**
	 * @throws IOException 
	 * @see {@link OrganSession#save(java.io.File)}
	 */
	public void saved(File file) throws IOException;

	/**
	 * @see {@link OrganSession#destroy()}
	 */
	public void destroyed();
}