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
package jorgan.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jorgan.disposition.Elements;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.InterceptMessage;
import jorgan.disposition.Message;
import jorgan.disposition.Output.OutputMessage;
import jorgan.swing.CompoundIcon;
import jorgan.swing.table.SimpleCellRenderer;

public class MessageTableCellRenderer extends SimpleCellRenderer<Message> {

	private static final Icon inputIcon = new ImageIcon(
			MessageTableCellRenderer.class
					.getResource("/jorgan/gui/img/input.gif"));

	private static final Icon outputIcon = new ImageIcon(
			MessageTableCellRenderer.class
					.getResource("/jorgan/gui/img/output.gif"));

	private static final Icon interceptIcon = new ImageIcon(
			OrganPanel.class.getResource("/jorgan/gui/img/intercept.gif"));

	protected void init(Message message) {
		// might be null from accessibility
		if (message == null) {
			return;
		}

		Icon icon;
		if (message instanceof InputMessage) {
			icon = inputIcon;
		} else if (message instanceof OutputMessage) {
			icon = outputIcon;
		} else {
			throw new IllegalArgumentException();
		}

		if (message instanceof InterceptMessage) {
			icon = new CompoundIcon(icon, interceptIcon);
		}
		setIcon(icon);

		// might be null from accessibility
		if (message != null) {
			setText(Elements.getDisplayName(message.getClass()));
		}
	}
}