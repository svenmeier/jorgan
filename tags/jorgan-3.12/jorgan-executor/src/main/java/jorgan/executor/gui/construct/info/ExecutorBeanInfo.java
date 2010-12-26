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
package jorgan.executor.gui.construct.info;

import jorgan.executor.disposition.Executor;
import jorgan.gui.construct.editor.BooleanEditor;
import jorgan.gui.construct.editor.StringEditor;
import jorgan.gui.construct.info.SwitchBeanInfo;

/**
 * BeanInfo for {@link Executor}.
 */
public class ExecutorBeanInfo extends SwitchBeanInfo {

	@Override
	protected void registerProperties() {
		super.registerProperties();

		add("save", Executor.class, BooleanEditor.class);
		add("command", Executor.class, StringEditor.class);
	}
}
