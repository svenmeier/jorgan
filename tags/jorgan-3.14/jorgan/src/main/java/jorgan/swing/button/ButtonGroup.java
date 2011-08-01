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
package jorgan.swing.button;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;

/**
 * Simplified group handling for {@link AbstractButton}s.
 */
public class ButtonGroup {

	private boolean allowNone;
	
	private List<AbstractButton> buttons = new ArrayList<AbstractButton>();

	public ButtonGroup() {
		this(false);
	}

	public ButtonGroup(boolean allowNone) {
		this.allowNone = allowNone;
	}

	public final void add(final AbstractButton button) {
		if (buttons.isEmpty() && !allowNone) {
			button.setSelected(true);
		}
		buttons.add(button);
		
		button.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				internalOnChange(button);
			}
		});
	}

	private boolean updating = false;
	
	private void internalOnChange(AbstractButton button) {
		if (updating) {
			return;
		}
		
		if (!button.isSelected()) {
			if (!allowNone) {
				button.setSelected(true);
				return;
			}
			button = null;
		} else {
			updating = true;
			for (AbstractButton other : buttons) {
				if (other != button) {
					other.setSelected(false);
				}
			}
			updating = false;
		}

		onSelected(button);
	}

	/**
	 * Override to get notified of a change.
	 * 
	 * @param button
	 *            the selected button or <code>null</code> if none
	 * @see #ButtonGroup(boolean)
	 */
	protected void onSelected(AbstractButton button) {

	}
}
