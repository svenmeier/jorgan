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
package jorgan.customizer.gui.expression;

import javax.swing.JPanel;

import jorgan.disposition.Continuous;
import jorgan.disposition.Controller;

/**
 * A panel for a single {@link Continuous} element on a {@link host}.
 */
public class ExpressionPanel extends JPanel {

	private Controller controller;

	private Continuous continuous;

	public ExpressionPanel(Controller controller, Continuous continuous) {
		if (controller == null) {
			throw new IllegalArgumentException("controller must not be null");
		}
		if (continuous == null) {
			throw new IllegalArgumentException("continuous must not be null");
		}

		this.controller = controller;
		this.continuous = continuous;
	}

	public void apply() {

	}
}