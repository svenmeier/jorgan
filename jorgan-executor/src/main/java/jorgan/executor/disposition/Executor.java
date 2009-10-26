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
package jorgan.executor.disposition;

import jorgan.disposition.Switch;
import jorgan.util.Null;

/**
 */
public class Executor extends Switch {

	private boolean save;

	private String command;

	public Executor() {
		setLocking(false);
	}

	public void setSave(boolean save) {
		if (this.save != save) {
			boolean oldSave = this.save;

			this.save = save;

			fireChange(new PropertyChange(oldSave, this.save));
		}
	}

	public boolean getSave() {
		return save;
	}

	public void setCommand(String command) {
		if (!Null.safeEquals(this.command, command)) {
			String oldCommand = this.command;

			if ("".equals(command)) {
				command = null;
			}
			this.command = command;

			fireChange(new PropertyChange(oldCommand, this.command));
		}
	}

	public String getCommand() {
		return command;
	}
}
