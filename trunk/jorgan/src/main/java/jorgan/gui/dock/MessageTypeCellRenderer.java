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
package jorgan.gui.dock;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableCellRenderer;

import jorgan.disposition.Elements;
import jorgan.disposition.InterceptMessage;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.gui.OrganPanel;
import jorgan.swing.CompoundIcon;

public class MessageTypeCellRenderer extends DefaultTableCellRenderer {

	private static final Icon inputIcon = new ImageIcon(
			MessageTypeCellRenderer.class
					.getResource("/jorgan/gui/img/input.gif"));

	private static final Icon outputIcon = new ImageIcon(
			MessageTypeCellRenderer.class
					.getResource("/jorgan/gui/img/output.gif"));

	private static final Icon interceptIcon = new ImageIcon(OrganPanel.class
			.getResource("/jorgan/gui/img/intercept.gif"));

	@Override
	protected void setValue(Object value) {
		Icon icon;
		if (value instanceof InputMessage) {
			icon = inputIcon;
		} else if (value instanceof OutputMessage) {
			icon = outputIcon;
		} else {
			throw new IllegalArgumentException();
		}

		if (value instanceof InterceptMessage) {
			icon = new CompoundIcon(icon, interceptIcon);
		}
		setIcon(icon);

		// might be null from accessibility
		if (value != null) {
			setText(Elements.getDisplayName(value.getClass()));
		}
	}
}