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
package jorgan.swing;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public abstract class BaseAction extends AbstractAction {

	public void setName(String name) {
		putValue(NAME, name);
	}

	public String getName() {
		return (String) getValue(Action.NAME);
	}

	public void setShortDescription(String description) {
		putValue(SHORT_DESCRIPTION, description);
	}

	public String getShortDescription() {
		return (String) getValue(Action.SHORT_DESCRIPTION);
	}

	public void setSmallIcon(Icon icon) {
		putValue(SMALL_ICON, icon);
	}

	public Icon getSmallIcon() {
		return (Icon) getValue(Action.SMALL_ICON);
	}

	public void setAccelerator(KeyStroke keyStroke) {
		putValue(ACCELERATOR_KEY, keyStroke);
	}

	public KeyStroke getAccelerator() {
		return (KeyStroke) getValue(ACCELERATOR_KEY);
	}

	public void setMnemonic(Integer key) {
		putValue(MNEMONIC_KEY, key);
	}

	public Integer getMnemonic() {
		return (Integer) getValue(MNEMONIC_KEY);
	}

	public void setSelected(boolean selected) {
		putValue(SELECTED_KEY, selected);
	}

	public boolean isSelected() {
		return (Boolean) getValue(SELECTED_KEY);
	}

	protected void registerAccelerator(JComponent component) {
		component.getInputMap().put(getAccelerator(), this);
		component.getActionMap().put(this, this);
	}
}
