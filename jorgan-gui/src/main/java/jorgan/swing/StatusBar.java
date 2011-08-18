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

import java.awt.BorderLayout;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

/**
 * Bar for displaying status information.
 */
public class StatusBar extends JPanel {

	private JLabel label = new JLabel();

	private JPanel toolBar = new JPanel();

	/**
	 * Create a status bar.
	 */
	public StatusBar() {
		super(new BorderLayout());

		setBorder(new EmptyBorder(0, 0, 2, 0));

		label.setText(" ");
		add(label, BorderLayout.CENTER);

		add(toolBar, BorderLayout.EAST);
	}

	/**
	 * Add a new status component.
	 * 
	 * @param status
	 *            component
	 */
	public void addStatus(JComponent status) {

		toolBar.add(new JSeparator());
		toolBar.add(status);

		toolBar.repaint();
		toolBar.revalidate();
	}

	/**
	 * Get the current <em>main</em> status
	 * 
	 * @return the current status
	 */
	public String getStatus() {
		return label.getText();
	}

	/**
	 * Set the new <em>main</em> status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		setStatus(status, null);
	}

	/**
	 * Set the new <em>main</em> status.
	 * 
	 * @param status
	 *            the new status
	 * @param icon
	 *            optional icon
	 */
	public void setStatus(String status, Icon icon) {
		if (status == null || "".equals(status)) {
			label.setText(" ");
		} else {
			label.setText(status);
		}
		label.setIcon(icon);
	}
}