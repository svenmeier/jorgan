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
package bias.swing;

import javax.swing.JComponent;

/**
 * A category of properties.
 */
public interface Category {

	/**
	 * Get the name of this category.
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Get a component for this category.
	 * 
	 * @return component
	 */
	public JComponent getComponent();

	/**
	 * Get the class of the parent category or <code>null</code> if none.
	 * 
	 * @return parent category class
	 */
	public Class<? extends Category> getParentCategory();

	/**
	 * Apply all changes.
	 */
	public void apply();

	/**
	 * Restore.
	 */
	public void restore();
}