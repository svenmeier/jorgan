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
package jorgan.memory.gui.construct.info;

import jorgan.gui.construct.editor.FileEditor;
import jorgan.gui.construct.editor.IntegerEditor;
import jorgan.gui.construct.info.IndexedContinuousBeanInfo;
import jorgan.memory.disposition.Memory;

/**
 * BeanInfo for {@link jorgan.memory.disposition.Memory}.
 */
public class MemoryBeanInfo extends IndexedContinuousBeanInfo {

	@Override
	protected void registerProperties() {
		super.registerProperties();

		add("size", Memory.class, IntegerEditor.class);
		add("storage", Memory.class, FileEditor.class);
	}
}