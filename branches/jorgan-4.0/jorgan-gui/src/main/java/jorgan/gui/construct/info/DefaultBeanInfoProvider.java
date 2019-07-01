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
package jorgan.gui.construct.info;

import java.beans.BeanInfo;

import jorgan.gui.construct.info.spi.BeanInfoProvider;

/**
 * The default provider of {@link BeanInfo}s.
 */
public class DefaultBeanInfoProvider implements BeanInfoProvider {

	private static final String BEAN_INFO_SEARCH_PATH = "jorgan.gui.construct.info";

	public String getBeanInfoSearchPath() {
		return BEAN_INFO_SEARCH_PATH;
	}
}
